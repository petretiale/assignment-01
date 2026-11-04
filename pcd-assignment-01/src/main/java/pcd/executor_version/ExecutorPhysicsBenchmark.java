package pcd.executor_version;

import pcd.executor_version.model.*;
import pcd.executor_version.controller.UpdatePosTask;
import pcd.executor_version.controller.CollisionTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorPhysicsBenchmark {

    public static void main(String[] args) {
        // Configurazioni: da 1 thread fino al numero di core logici + 1 per task compute-intensive
        int[] threadCounts = {1, 2, 4, Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1};
        int framesToSimulate = 1000;
        long dt = 16;
        System.out.println("Avvio benchmark Executor per " + framesToSimulate + " frame.");

        for (int nWorkers : threadCounts) {
            Board board = new Board();
            BoardConf conf = new MassiveBoardConf();
            board.init(conf);

            // Creazione di un pool di dimensione fissa
            ExecutorService exec = Executors.newFixedThreadPool(nWorkers);

            Ball playerBall = board.getPlayerBall();
            Ball botBall = board.getBotBall();
            int nBalls = board.getBalls().size();

            // Warm-up per stabilizzare la compilazione dinamica (JIT) della JVM
            runSimulationLoop(50, nWorkers, nBalls, board, playerBall, botBall, dt, exec);

            // Misurazione effettiva ad alta precisione
            long startTime = System.nanoTime();

            runSimulationLoop(framesToSimulate, nWorkers, nBalls, board, playerBall, botBall, dt, exec);

            long endTime = System.nanoTime();
            double executionTimeMs = (endTime - startTime) / 1_000_000.0;

            System.out.printf("Thread nel pool: %2d | Tempo di esecuzione: %8.2f ms%n", nWorkers, executionTimeMs);

            // Chiusura fondamentale per permettere alla JVM di terminare
            exec.shutdown();
        }
    }

    private static void runSimulationLoop(int iterations, int nWorkers, int nBalls, Board board, Ball playerBall, Ball botBall, long dt, ExecutorService exec) {
        try {
            for (int f = 0; f < iterations; f++) {
                List<Future<?>> updateTasks = new ArrayList<>();
                List<Ball> balls = board.getBalls();
                nBalls = balls.size(); // Aggiorna in caso di palline cadute nelle buche

                // Fase 1: Update State
                for (int i = 0; i < nWorkers; i++) {
                    int startIdx = (i * nBalls) / nWorkers;
                    int endIdx = ((i + 1) * nBalls / nWorkers) - 1;
                    if (startIdx <= endIdx) {
                        updateTasks.add(exec.submit(new UpdatePosTask(balls, startIdx, endIdx, dt, board)));
                    }
                }

                for (Future<?> future : updateTasks) {
                    future.get(); // Attesa completamento (Sincronizzazione implicita)
                }

                if (playerBall != null) playerBall.updateState(dt, board);
                if (botBall != null) botBall.updateState(dt, board);

                // Fase 2: Risoluzione Collisioni
                List<Future<?>> collisionTasks = new ArrayList<>();
                for (int i = 0; i < nWorkers; i++) {
                    int startIdx = (i * nBalls) / nWorkers;
                    int endIdx = ((i + 1) * nBalls / nWorkers) - 1;
                    if (startIdx <= endIdx) {
                        collisionTasks.add(exec.submit(new CollisionTask(balls, startIdx, endIdx, nBalls)));
                    }
                }

                for (Future<?> future : collisionTasks) {
                    future.get();
                }

                // Fase 3: Collisioni finali e aggiornamento Board
                if (playerBall != null && botBall != null) {
                    for (Ball b : balls) {
                        Ball.resolveCollision(playerBall, b);
                        Ball.resolveCollision(botBall, b);
                    }
                    Ball.resolveCollision(playerBall, botBall);
                }

                board.updateState();
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}