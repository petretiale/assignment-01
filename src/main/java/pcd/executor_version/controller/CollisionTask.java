package pcd.executor_version.controller;

import pcd.executor_version.model.Ball;

import java.util.List;

public class CollisionTask implements Runnable {

    private final List<Ball> balls;
    private final int startIdx;
    private final int endIdx;
    private final int totalBalls;

    public CollisionTask(List<Ball> balls, int startIdx, int endIdx, int totalBalls) {
        this.balls = balls;
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.totalBalls = totalBalls;
    }

    @Override
    public void run() {
        for (int i = startIdx; i <= endIdx; i++) {
            for(int j = i + 1; j < totalBalls; j++) {
                Ball.resolveCollision(balls.get(i), balls.get(j));
            }
        }
    }
}
