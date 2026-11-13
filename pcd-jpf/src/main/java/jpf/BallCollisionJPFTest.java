package jpf;

public class BallCollisionJPFTest {

    static class MockBall {
        private final int id;
        public double x, y, snapshotX, snapshotY;

        public MockBall(int id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.snapshotX = x;
            this.snapshotY = y;
        }
        public int getId() { return id; }
    }

    public static void resolveCollision(MockBall a, MockBall b) {
        double dx = b.snapshotX - a.snapshotX;
        double dy = b.snapshotY - a.snapshotY;

        if (dx * dx + dy * dy >= 25) {
            return;
        }
        MockBall first = (a.getId() < b.getId()) ? a : b;
        MockBall second = (a.getId() < b.getId()) ? b : a;

        synchronized (first) {
            synchronized (second) {
                // Ricalcolo protetto
                dx = b.x - a.x;
                dy = b.y - a.y;

                // Simula l'aggiornamento di stato
                if (dx * dx + dy * dy < 25) {
                    a.x += 1;
                    b.x -= 1;
                }
            }
        }
    }

    public static void main(String[] args) {
        MockBall b1 = new MockBall(1, 0.0, 0.0);
        MockBall b2 = new MockBall(2, 0.5, 0.5);
        MockBall b3 = new MockBall(3, 1, 1);

        // Thread 1: controlla la collisione b1->b2
        Thread t1 = new Thread(() -> resolveCollision(b1, b2));
        // Thread 2: controlla la collisione b2->b1 (innesca potenziale deadlock)
        Thread t2 = new Thread(() -> resolveCollision(b2, b1));
        // Thread 3: sposta le palline (innesca data race sulle letture)
        Thread t3 = new Thread(() -> resolveCollision(b1, b3));


        t1.start(); t2.start(); t3.start();

        try {
            t1.join(); t2.join(); t3.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
