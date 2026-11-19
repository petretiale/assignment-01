package pcd.executor_version;

import pcd.common.model.*;
import pcd.executor_version.task.UpdateStateTask;
import pcd.executor_version.task.ResolveCollisionTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorBenchmark {

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

                for (Ball b : board.getBalls()) {
                    UpdateStateTask task = new UpdateStateTask(board, b, dt);
                    updateTasks.add(exec.submit(task));
                }

                for (Future<?> future : updateTasks) {
                    future.get(); // Attesa completamento (Sincronizzazione implicita)
                }

                if (playerBall != null) playerBall.updateState(dt, board);
                if (botBall != null) botBall.updateState(dt, board);


                List<Future<?>> collisionTasks = new ArrayList<>();
                for (int i = 0; i < nBalls; i++) {
                    ResolveCollisionTask task = new ResolveCollisionTask(balls, i);
                    collisionTasks.add(exec.submit(task));
                }

                for (Future<?> future : collisionTasks) {
                    future.get();
                }

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