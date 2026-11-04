package jpf;

public class BoundedBufferMultiThreadTest {
    public static void main(String[] args) {
        BoundedBuffer<String> buffer = new BoundedBufferImpl<>(2);

        // Creiamo 2 Produttori (inseriscono 2 comandi ciascuno)
        Runnable producerTask = () -> {
            try {
                buffer.put(Thread.currentThread().getName() + "_A");
                buffer.put(Thread.currentThread().getName() + "_B");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // Creiamo 2 Consumatori (leggono 2 comandi ciascuno)
        Runnable consumerTask = () -> {
            int letti = 0;
            while (letti < 2) {
                String cmd = buffer.poll();
                if (cmd != null) {
                    letti++;
                    // Verifica che JPF valuterà ad ogni esplorazione
                    assert cmd.contains("Prod") : "Letto dato non valido o null!";
                } else {
                    try { Thread.sleep(10); } catch (InterruptedException e) {}
                }
            }
        };

        Thread p1 = new Thread(producerTask, "Prod1");
        Thread p2 = new Thread(producerTask, "Prod2");
        Thread c1 = new Thread(consumerTask, "Cons1");
        Thread c2 = new Thread(consumerTask, "Cons2");

        p1.start(); p2.start(); c1.start(); c2.start();

        try {
            p1.join(); p2.join(); c1.join(); c2.join();
        } catch (InterruptedException e) {}
    }
}
