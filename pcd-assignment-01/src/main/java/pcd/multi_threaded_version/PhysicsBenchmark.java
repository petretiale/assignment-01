package pcd.multi_threaded_version;

import pcd.multi_threaded_version.model.*;
import java.util.ArrayList;
import java.util.List;

public class PhysicsBenchmark {

    public static void main(String[] args) {
        int[] threadCounts = {1, 2, 4, Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1};
        int framesToSimulate = 1000;
        long dt = 16;

        System.out.println("Avvio benchmark per " + framesToSimulate + " frame.");

        for (int nWorkers : threadCounts) {
            Board board = new Board();
            BoardConf conf = new MassiveBoardConf();
            //BoardConf conf = new LargeBoardConf();
            board.init(conf);

            Barrier startingBarrier = new Barrier(nWorkers + 1);
            Barrier updateBallBarrier = new Barrier(nWorkers + 1);
            Barrier resolveCollisionBarrier = new Barrier(nWorkers + 1);

            List<Worker> workers = new ArrayList<>();
            for (int i = 0; i < nWorkers; i++) {
                Worker w = new Worker(i, board, startingBarrier, updateBallBarrier, resolveCollisionBarrier);
                workers.add(w);
                w.start();
            }

            Ball playerBall = board.getPlayerBall();
            Ball botBall = board.getBotBall();
            int nBalls = board.getBalls().size();

            // Warm-up per la JVM (Compilazione dinamica)
            runSimulationLoop(50, nWorkers, nBalls, workers, board, playerBall, botBall, dt, startingBarrier, updateBallBarrier, resolveCollisionBarrier);

            long startTime = System.nanoTime();

            runSimulationLoop(framesToSimulate, nWorkers, nBalls, workers, board, playerBall, botBall, dt, startingBarrier, updateBallBarrier, resolveCollisionBarrier);

            long endTime = System.nanoTime();
            double executionTimeMs = (endTime - startTime) / 1_000_000.0;

            System.out.printf("Thread: %2d | Tempo di esecuzione: %8.2f ms%n", nWorkers, executionTimeMs);

            for (Worker w : workers) {
                w.stopRun();
                w.interrupt();
            }
        }
    }

    private static void runSimulationLoop(int iterations, int nWorkers, int nBalls, List<Worker> workers, Board board, Ball playerBall, Ball botBall, long dt, Barrier start, Barrier update, Barrier resolve) {
        try {
            for (int f = 0; f < iterations; f++) {
                for (int i = 0; i < nWorkers; i++) {
                    Worker w = workers.get(i);
                    int startBall = (i * nBalls) / nWorkers;
                    int lastBall = ((i + 1) * nBalls / nWorkers) - 1;
                    w.setBallsSection(startBall, lastBall, nBalls);
                    w.setDt(dt);
                }

                start.await();
                if(playerBall != null) playerBall.updateState(dt, board);
                if(botBall != null) botBall.updateState(dt, board);

                update.await();

                if (playerBall != null && botBall != null) {
                    for (Ball b : board.getBalls()) {
                        Ball.resolveCollision(playerBall, b);
                        Ball.resolveCollision(botBall, b);
                    }
                    Ball.resolveCollision(playerBall, botBall);
                }

                resolve.await();
                board.updateState();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
