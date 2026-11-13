package jpf;

import java.util.List;

public class ResolveCollisionTaskJPFTest {

    static class MockBall {
        private final int id;
        public int collisionsCount = 0;

        public MockBall(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static void resolveCollision(MockBall a, MockBall b) {
            MockBall first  = (a.getId() < b.getId()) ? a : b;
            MockBall second = (a.getId() < b.getId()) ? b : a;

            synchronized (first) {
                synchronized (second) {
                    a.collisionsCount++;
                    b.collisionsCount++;
                }
            }
        }
    }

    // Riproduce fedelmente pcd.executor_version.task.ResolveCollisionTask
    static class ResolveCollisionTask implements Runnable {
        private final List<MockBall> balls;
        private final int ballIdx;
        private final int nBalls;

        public ResolveCollisionTask(List<MockBall> balls, int ballIdx) {
            this.balls = balls;
            this.ballIdx = ballIdx;
            this.nBalls = balls.size();
        }

        @Override
        public void run() {
            MockBall bI = balls.get(ballIdx);
            for (int step = 1; step <= ((nBalls - 1) / 2); step++) {
                int indexJ = (ballIdx + step) % nBalls;
                MockBall.resolveCollision(bI, balls.get(indexJ));
            }
            if ((nBalls % 2 == 0) && ballIdx < nBalls / 2) {
                int oppositeIndex = ballIdx + (nBalls / 2);
                MockBall.resolveCollision(bI, balls.get(oppositeIndex));
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Test con N=4 (pari, testa sia il ciclo step sia il ramo oppositeIndex)
        List<MockBall> balls = List.of(
                new MockBall(0), new MockBall(1), new MockBall(2), new MockBall(3)
        );
        int nBalls = balls.size();
        Thread[] tasks = new Thread[nBalls];

        for (int i = 0; i < nBalls; i++) {
            tasks[i] = new Thread(new ResolveCollisionTask(balls, i));
            tasks[i].start();
        }

        for (int i = 0; i < nBalls; i++) {
            tasks[i].join();
        }

        // Ogni pallina su 4 deve aver partecipato esattamente a 3 controlli (nessun duplicato o mancante)
        for (MockBall b : balls) {
            assert b.collisionsCount == nBalls - 1 : "Distribuzione collisioni errata!";
        }
    }
}