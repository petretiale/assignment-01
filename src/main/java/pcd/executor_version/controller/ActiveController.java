package pcd.executor_version.controller;

import pcd.executor_version.model.Ball;
import pcd.executor_version.model.Board;
import pcd.executor_version.util.BoundedBuffer;
import pcd.executor_version.util.BoundedBufferImpl;
import pcd.executor_version.view.View;
import pcd.executor_version.view.ViewModel;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ActiveController extends Thread {

    private final BoundedBuffer<Cmd> cmdBuffer;
    private final Board board;
    private final ViewModel viewModel;
    private View view;


    public ActiveController(Board board, ViewModel viewModel) {
        this.cmdBuffer = new BoundedBufferImpl<>(100);
        this.board = board;
        this.viewModel = viewModel;
    }

    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void run() {

        viewModel.update(board, 0);
        view.render();
        waitAbit();

        int nFrames = 0;
        long t0 = System.currentTimeMillis();
        long lastUpdateTime = System.currentTimeMillis();

        Ball playerBall = board.getPlayerBall();
        Ball botBall = board.getBotBall();

        try {
            while (true) {
                long current = System.currentTimeMillis();
                long elapsed = current - lastUpdateTime;
                lastUpdateTime = current;

                // Esecuzione dei comandi in coda
                Cmd cmd;
                while ((cmd = cmdBuffer.poll()) != null) {
                    cmd.execute(board);
                }
                ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
                for (Ball b : board.getBalls()) {
                    exec.execute(() -> {
                        b.updateState(elapsed, board);
                    });
                }
                exec.shutdown();
                exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
                playerBall.updateState(elapsed, board);
                botBall.updateState(elapsed, board);
                List<Ball> balls = board.getBalls();
                int nBalls = balls.size();
                for (int i = 0; i < nBalls; i++) {
                    Ball bI = balls.get(i);
                    for (int j = i + 1; j < nBalls; j++) {
                        Ball bJ = balls.get(j);
                        exec.execute(() ->{
                            Ball.resolveCollision(bI, bJ);
                        });
                    }
                }
                exec.shutdown();
                exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                for (var b: board.getBalls()) {
                    Ball.resolveCollision(playerBall, b);
                    Ball.resolveCollision(botBall, b);
                }
                // Collisione diretta tra la pallina del giocatore e quella del bot
                Ball.resolveCollision(playerBall, botBall);

                board.updateState();

                // Calcolo FPS
                nFrames++;
                int framePerSec = 0;
                long dt = current - t0;
                if (dt > 0) {
                    framePerSec = (int) (nFrames * 1000 / dt);
                }

                viewModel.update(board, framePerSec);
                view.render();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyNewCmd(Cmd cmd) {
        try {
            cmdBuffer.put(cmd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void waitAbit() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
    }
}
