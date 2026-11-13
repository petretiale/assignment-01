package jpf;

public class MTIntegrationJPFTest {

    static class MockBall {
        private final int id;
        private int pos;
        public MockBall(int id, int pos) { this.id = id; this.pos = pos; }
        public int getId() { return id; }
        public synchronized void update(int dt) { pos += dt; }

        public static void resolveCollision(MockBall a, MockBall b) {
            MockBall first  = (a.getId() < b.getId()) ? a : b;
            MockBall second = (a.getId() < b.getId()) ? b : a;
            synchronized (first) {
                synchronized (second) {
                    if (Math.abs(b.pos - a.pos) < 5) {
                        a.pos -= 1;
                        b.pos += 1;
                    }
                }
            }
        }
    }

    static class TestWorker extends Thread {
        private final int idx;
        private final MockBall[] balls;
        private final Barrier startB, updateB, collisionB;
        private int dt;

        public TestWorker(int idx, MockBall[] balls, Barrier sB, Barrier uB, Barrier cB) {
            this.idx = idx;
            this.balls = balls;
            this.startB = sB;
            this.updateB = uB;
            this.collisionB = cB;
        }

        public void setDt(int dt) { this.dt = dt; }

        @Override
        public void run() {
            try {
                for (int cycle = 0; cycle < 2; cycle++) {
                    startB.await();
                    balls[idx].update(dt);
                    updateB.await();
                    int next = (idx + 1) % balls.length;
                    MockBall.resolveCollision(balls[idx], balls[next]);
                    collisionB.await();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int nWorkers = 2;
        Barrier startB = new Barrier(nWorkers + 1);
        Barrier updateB = new Barrier(nWorkers + 1);
        Barrier collisionB = new Barrier(nWorkers + 1);

        MockBall[] balls = { new MockBall(1, 0), new MockBall(2, 2) };
        MockBall playerBall = new MockBall(0, 1);

        TestWorker w0 = new TestWorker(0, balls, startB, updateB, collisionB);
        TestWorker w1 = new TestWorker(1, balls, startB, updateB, collisionB);
        w0.start();
        w1.start();

        // Simula MTActiveController per 2 frame
        for (int cycle = 0; cycle < 2; cycle++) {
            w0.setDt(1);
            w1.setDt(1);
            startB.await(); // Sblocca Fase 1

            playerBall.update(1);
            updateB.await(); // Fine Fase 1, inizio Fase 2

            for (MockBall b : balls) {
                MockBall.resolveCollision(playerBall, b);
            }
            collisionB.await(); // Fine Fase 2
        }

        w0.join();
        w1.join();
    }
}