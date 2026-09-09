package pcd.sketch01.model;

import java.util.List;

public class Worker extends Thread {

    private int workerId;
    private int nWorkers;
    private List<Ball> balls;
    private long dt;
    private Board board;

    private Barrier updateBallBarrier;
    private Barrier resolveCollisionBarrier;

    public Worker(int workerId, int nWorkers, Board board, Barrier uB, Barrier rB){
        super("Worker" + workerId);
        this.balls = board.getBalls();
        this.nWorkers = nWorkers;
        //this.startBarrier = sB;
        this.updateBallBarrier = uB;
        this.resolveCollisionBarrier = rB;
        this.board = board;
    }

    @Override
    public void run() {
        int startBall = (balls.size() / nWorkers) * workerId;
        int lastBall = startBall + ((balls.size() / nWorkers) - 1);
        try {
            //startBarrier.await();

            for (int i = startBall; i <= lastBall; i++) {
                balls.get(i).updateState(dt, board);
            }
            System.out.println(Thread.currentThread());

            updateBallBarrier.await();

            System.out.println("dopo barriera" + Thread.currentThread());

            for (int i = startBall; i <= lastBall - 1; i++) {
                for (int j = i + 1; j < lastBall; j++) {
                    Ball.resolveCollision(balls.get(i), balls.get(j));
                }
            }

            resolveCollisionBarrier.await();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void setDt(long dt) {
        this.dt = dt;
    }
}
