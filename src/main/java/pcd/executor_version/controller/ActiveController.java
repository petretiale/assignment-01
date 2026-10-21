package pcd.executor_version.controller;

import pcd.executor_version.model.Ball;
import pcd.executor_version.model.Board;
import pcd.executor_version.util.BoundedBuffer;
import pcd.executor_version.util.BoundedBufferImpl;
import pcd.executor_version.view.View;
import pcd.executor_version.view.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ActiveController extends Thread {

    private final BoundedBuffer<Cmd> cmdBuffer;
    private final Board board;
    private final ViewModel viewModel;
    private View view;

    private final int nTasks;
    private final ExecutorService executor;

    public ActiveController(Board board, ViewModel viewModel) {
        this.cmdBuffer = new BoundedBufferImpl<>(100);
        this.board = board;
        this.viewModel = viewModel;
        this.nTasks = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(nTasks);
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
            while (!board.isGameOver()) {
                long current = System.currentTimeMillis();
                long elapsed = current - lastUpdateTime;
                lastUpdateTime = current;

                Cmd cmd;
                while ((cmd = cmdBuffer.poll()) != null) {
                    cmd.execute(board);
                }

                List<Ball> balls = board.getBalls();
                int totalBalls = balls.size();

                if (totalBalls > 0) {
                    // TASK DI MOVIMENTO
                    List<Future<?>> moveTasks = new ArrayList<>();

                    for (int i = 0; i < nTasks; i++) {
                        int start = (i * totalBalls) / nTasks;
                        int end = Math.min((((i + 1) * totalBalls) / nTasks) - 1, totalBalls - 1);

                        if (start < totalBalls && start <= end) {
                            UpdatePosTask task = new UpdatePosTask(balls, start, end, elapsed, board);
                            moveTasks.add(executor.submit(task));
                        }
                    }

                    // Aspettiamo che tutti i task di movimento finiscano
                    for (Future<?> f : moveTasks) {
                        f.get();
                    }

                    if (playerBall != null) playerBall.updateState(elapsed, board);
                    if (botBall != null) botBall.updateState(elapsed, board);


                    // TASK DI COLLISIONE
                    List<Future<?>> collisionFutures = new ArrayList<>();

                    for (int i = 0; i < nTasks; i++) {
                        int start = (i * totalBalls) / nTasks;
                        int end = Math.min((((i + 1) * totalBalls) / nTasks) - 1, totalBalls - 1);

                        if (start < totalBalls && start <= end) {
                            CollisionTask task = new CollisionTask(balls, start, end, totalBalls);
                            collisionFutures.add(executor.submit(task));
                        }
                    }
                    for (Future<?> f : collisionFutures) {
                        f.get();
                    }
                }
                for (var b : board.getBalls()) {
                    if (playerBall != null) Ball.resolveCollision(playerBall, b);
                    if (botBall != null) Ball.resolveCollision(botBall, b);
                }
                if (playerBall != null && botBall != null) {
                    Ball.resolveCollision(playerBall, botBall);
                }

                board.updateState();

                nFrames++;
                long dt = System.currentTimeMillis() - t0;
                int framePerSec = 0;
                if (dt > 0) {
                    framePerSec = (int) (nFrames * 1000 / dt);
                }

                viewModel.update(board, framePerSec);
                view.render();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gioco interrotto o errore: " + e.getMessage());
        } finally {
            shutdownExecutor();
        }
    }


    private void shutdownExecutor() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
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