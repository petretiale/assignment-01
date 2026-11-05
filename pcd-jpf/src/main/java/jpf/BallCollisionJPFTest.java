package jpf;

public class BallCollisionJPFTest {

    static class MockBall {
        private final int id;
        public double x, y; // Stato condiviso vulnerabile

        public MockBall(int id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
        public int getId() { return id; }
    }

    public static void resolveCollision(MockBall a, MockBall b) {
        // Data Race voluta: lettura senza lock
        double dx = b.x - a.x;
        double dy = b.y - a.y;

        MockBall first = (a.getId() < b.getId()) ? a : b;
        MockBall second = (a.getId() < b.getId()) ? b : a;

        synchronized (first) {
            synchronized (second) {
                // Ricalcolo protetto
                dx = b.x - a.x;
                dy = b.y - a.y;

                // Simula l'aggiornamento di stato
                a.x += 1;
                b.x -= 1;
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
