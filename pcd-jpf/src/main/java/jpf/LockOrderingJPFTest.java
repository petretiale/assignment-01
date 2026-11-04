package jpf;

public class LockOrderingJPFTest {

    static class Ball {
        private final int id;
        private int x;
        private int y;

        public Ball(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
        public int getId() { return id; }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public static void resolveCollision(Ball a, Ball b) {

        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();

        if (dx > 5 && dy > 5) {
            return;
        }

        Ball first  = (a.getId() < b.getId()) ? a : b;
        Ball second = (a.getId() < b.getId()) ? b : a;

        synchronized (first) {
            synchronized (second) {
                // Sezione critica simulata
                int dummy = first.getId() + second.getId();
            }
        }
    }

    public static void main(String[] args) {
        Ball b1 = new Ball(1, 2, 2);
        Ball b2 = new Ball(2, 8, 8);

        // Thread 1 vede la collisione tra b1 e b2
        Thread t1 = new Thread(() -> resolveCollision(b1, b2));

        // Thread 2 vede la collisione tra b2 e b1
        Thread t2 = new Thread(() -> resolveCollision(b2, b1));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
