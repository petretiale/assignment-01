package jpf;

public class BarrierJPFTest {

    // Parametri ridotti al minimo per evitare l'esplosione degli stati in JPF
    private static final int NUM_THREADS = 3;
    private static final int NUM_CYCLES = 2;

    public static void main(String[] args) {
        Barrier barrier = new Barrier(NUM_THREADS);
        Thread[] workers = new Thread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            workers[i] = new Thread(() -> {
                try {
                    for (int c = 0; c < NUM_CYCLES; c++) {
                        // Simula un task generico
                        int dummyWork = c * 2;

                        // Sincronizzazione
                        barrier.await();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            workers[i].start();
        }

        // Il thread principale attende la fine di tutti i worker
        for (int i = 0; i < NUM_THREADS; i++) {
            try {
                workers[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
