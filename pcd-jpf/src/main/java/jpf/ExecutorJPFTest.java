package jpf;

import java.util.ArrayList;
import java.util.List;

public class ExecutorJPFTest {

    // Modella exec.submit(task) e Future.get() per JPF senza caricare ThreadPoolExecutor
    static class SimpleFuture {
        private boolean done = false;

        public static SimpleFuture submit(Runnable task) {
            SimpleFuture f = new SimpleFuture();
            new Thread(() -> {
                try {
                    task.run();
                } finally {
                    f.complete();
                }
            }).start();
            return f;
        }

        private synchronized void complete() {
            done = true;
            notifyAll();
        }

        public synchronized void get() throws InterruptedException {
            while (!done) {
                wait();
            }
        }
    }

    static class MockBall {
        public int snapshotPos = 0;

        public synchronized void updateState(long dt) {
            snapshotPos += (int) dt;
        }
    }

    // Riproduce pcd.executor_version.task.UpdateStateTask (1 pallina per task)
    static class UpdateStateTask implements Runnable {
        private final MockBall ball;
        private final long dt;

        public UpdateStateTask(MockBall b, long elapsed) {
            this.ball = b;
            this.dt = elapsed;
        }

        @Override
        public void run() {
            ball.updateState(this.dt);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<MockBall> balls = new ArrayList<>();
        balls.add(new MockBall());
        balls.add(new MockBall());
        int nBalls = balls.size();

        // 1. Fase UpdateStateTask (1 task per pallina)
        List<SimpleFuture> updateTasks = new ArrayList<>();
        for (MockBall b : balls) {
            updateTasks.add(SimpleFuture.submit(new UpdateStateTask(b, 5)));
        }
        for (SimpleFuture f : updateTasks) {
            f.get(); // Barriera 1: garantisce che snapshotPos sia aggiornato per tutte le palline
        }

        // 2. Fase ResolveCollisionTask (legge snapshotPos senza lock)
        List<SimpleFuture> collisionTasks = new ArrayList<>();
        for (int i = 0; i < nBalls; i++) {
            final int ballIdx = i;
            collisionTasks.add(SimpleFuture.submit(() -> {
                int nextIdx = (ballIdx + 1) % nBalls;
                // Se la Barriera 1 non funzionasse, qui JPF rileverebbe una Data Race su snapshotPos!
                int dx = balls.get(nextIdx).snapshotPos - balls.get(ballIdx).snapshotPos;
                assert dx == 0;
            }));
        }
        for (SimpleFuture f : collisionTasks) {
            f.get(); // Barriera 2: garantisce che nessun task legga più la lista balls
        }

        // 3. Simula board.updateState() che rimuove una pallina caduta in buca
        balls.remove(0);
    }
}