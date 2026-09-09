package pcd.sketch01.model;

public class Worker extends Thread {

    private int workeId;
    private int nWorkers;


    private Barrier startBarrier;
    private Barrier midBarrier;
    private Barrier doneBarrier;


    @Override
    public void run() {
    }
}
