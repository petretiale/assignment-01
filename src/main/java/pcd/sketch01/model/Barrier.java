package pcd.sketch01.model;

public class Barrier {

    private final int totalParticipants;
    private int waiting;
    //private int currentCycle;

    public Barrier(int totalParticipants) {
        this.totalParticipants = totalParticipants;
        this.waiting = 0;
       // this.currentCycle = 0;
    }

    public synchronized void await() throws InterruptedException {

        waiting++;
        while (waiting < totalParticipants) {
            wait();
        }
        System.out.println("in barriera:" + waiting);
        waiting = 0;
        notifyAll();
    }
}
