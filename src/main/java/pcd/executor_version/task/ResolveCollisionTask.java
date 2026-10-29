package pcd.executor_version.task;

import pcd.common.model.Ball;

import java.util.List;

public class ResolveCollisionTask implements Task{

    private final List<Ball> balls;
    private final int ballIdx;
    private final int nBalls;

    public ResolveCollisionTask(List<Ball> balls, int ballIdx){
        this.balls = balls;
        this.ballIdx = ballIdx;
        this.nBalls = balls.size();
    }
    @Override
    public void run() {
        Ball bI = balls.get(ballIdx);

        for (int step = 1; step <= ((nBalls - 1) / 2); step++) {
            int indexJ = (ballIdx + step) % nBalls;
            Ball.resolveCollision(bI, balls.get(indexJ));
        }

        if ((nBalls % 2 == 0) && ballIdx < nBalls / 2) {
            int oppositeIndex = ballIdx + (nBalls / 2);
            Ball.resolveCollision(bI, balls.get(oppositeIndex));
        }
    }
}
