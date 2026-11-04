package pcd.multi_threaded_version.model;

public class Barrier {

    private final int totalParticipants;
    private int waiting;
    private int currentCycle;

    public Barrier(int totalParticipants) {
        this.totalParticipants = totalParticipants;
        this.waiting = 0;
        this.currentCycle = 0;
    }

    public synchronized void await() throws InterruptedException {

        int myCycle = currentCycle;
        waiting++;

        if (waiting == totalParticipants) {
            waiting = 0;
            currentCycle++;
            notifyAll();
        } else {
            while (myCycle == currentCycle) {
                wait();
            }
        }
    }
}
