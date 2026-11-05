package jpf;

public class BoundedBufferJPFTest {

    public static void main(String[] args) {
        // Buffer minuscolo per forzare le condizioni di attesa in JPF
        BoundedBuffer<String> buffer = new BoundedBufferImpl<>(2);

        // Simula la GUI che invia comandi (Produttore)
        Thread guiThread = new Thread(() -> {
            try {
                buffer.put("CMD_UP");
                buffer.put("CMD_DOWN");
                buffer.put("CMD_LEFT"); // Qui dovrebbe bloccarsi e aspettare
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        });

        // Simula il Controller che legge i comandi (Consumatore)
        Thread controllerThread = new Thread(() -> {
            int comandiElaborati = 0;

            // In un gioco vero sarebbe un while(running).
            // Qui aspettiamo di aver letto esattamente 3 comandi reali.
            while (comandiElaborati < 3) {
                String cmd = buffer.poll();

                if (cmd != null) {
                    // Abbiamo ricevuto un vero comando, lo elaboriamo
                    comandiElaborati++;
                } else {
                    // Il buffer era vuoto.
                    // Invece di usare yield, mettiamo il thread in pausa per 10 millisecondi.
                    // Questo simula l'attesa tra un "frame" e l'altro del controller
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break; // Usciamo dal ciclo se il thread viene interrotto
                    }
                }
            }
        });

        guiThread.start();
        controllerThread.start();

        try {
            guiThread.join();
            controllerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
