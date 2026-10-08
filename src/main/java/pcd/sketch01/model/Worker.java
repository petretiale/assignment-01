package pcd.sketch01.model;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker extends Thread {

    private int workerId;
    private List<Ball> balls;
    private long dt;
    private Board board;
    private boolean running = true;

    int startBall;
    int lastBall;
    private int totalBalls;

    private CyclicBarrier startBarrier;
    private CyclicBarrier updateBallBarrier;
    private CyclicBarrier resolveCollisionBarrier;

    public Worker(int workerId, Board board, CyclicBarrier sB, CyclicBarrier uB, CyclicBarrier rB){
        super("Worker" + workerId);
        this.workerId = workerId;
        this.balls = board.getBalls();
        this.startBarrier = sB;
        this.updateBallBarrier = uB;
        this.resolveCollisionBarrier = rB;
        this.board = board;
    }

    @Override
    public void run() {
        try {
            while (running){

                startBarrier.await();

                if (startBall <= lastBall && startBall < totalBalls) {
                    for (int i = startBall; i <= Math.min(lastBall, totalBalls - 1); i++) {
                        balls.get(i).updateState(dt, board);
                    }
                }

                updateBallBarrier.await();
                for (int i = startBall; i <= lastBall; i++) {
                    for (int j = i + 1; j < totalBalls; j++) {
                        Ball.resolveCollision(balls.get(i), balls.get(j));
                    }
                }

                resolveCollisionBarrier.await();
            }
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public void setBallsSection(int startBall, int lastBall, int totalBalls){
        this.startBall = startBall;
        this.lastBall = lastBall;
        this.totalBalls = totalBalls;
    }

    public void stopRun(){
        running = false;
    }

    public int getWorkerId() {
        return workerId;
    }
}
