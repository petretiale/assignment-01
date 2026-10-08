package pcd.sketch01.controller;

import pcd.sketch01.model.Ball;
import pcd.sketch01.model.Barrier;
import pcd.sketch01.model.Board;
import pcd.sketch01.model.Worker;
import pcd.sketch01.util.BoundedBuffer;
import pcd.sketch01.util.BoundedBufferImpl;
import pcd.sketch01.view.View;
import pcd.sketch01.view.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class ActiveController extends Thread {

    private final BoundedBuffer<Cmd> cmdBuffer;
    private final Board board;
    private final ViewModel viewModel;
    private View view;

    private int nWorkers = Runtime.getRuntime().availableProcessors();
    private CyclicBarrier startingBarrier = new CyclicBarrier(nWorkers + 1);
    private CyclicBarrier updateBallBarrier = new CyclicBarrier(nWorkers + 1);
    private CyclicBarrier resolveCollisionBarrier = new CyclicBarrier(nWorkers + 1);


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

        List<Worker> workers = new ArrayList<>();
        for (int i = 0; i < nWorkers; i++) {
            Worker w = new Worker(i, board, startingBarrier, updateBallBarrier, resolveCollisionBarrier);
            workers.add(w);
            w.start();
        }

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

                int nBalls = board.getBalls().size();
                for (int i = 0; i < nWorkers; i++) {
                    Worker w = workers.get(i);
                    int startBall = (i * nBalls) / nWorkers;
                    int lastBall = ((i + 1) * nBalls / nWorkers) - 1;
                    w.setBallsSection(startBall, lastBall, nBalls);
                    w.setDt(elapsed);
                }
                startingBarrier.await();
                playerBall.updateState(elapsed, board);
                botBall.updateState(elapsed, board);
                updateBallBarrier.await();
                for (var b: board.getBalls()) {
                    Ball.resolveCollision(playerBall, b);
                    Ball.resolveCollision(botBall, b);
                }
                // Collisione diretta tra la pallina del giocatore e quella del bot
                Ball.resolveCollision(playerBall, botBall);
                resolveCollisionBarrier.await();

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
        } finally {
            for (Worker w : workers){
                w.stopRun();
            }
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
