package pcd.executor_version.controller;

import pcd.executor_version.model.Ball;
import pcd.executor_version.model.Board;

import java.util.List;

public class UpdatePosTask implements Runnable{

    private final List<Ball> balls;
    private final int startIdx;
    private final int endIdx;
    private final long dt;
    private final Board board;

    public UpdatePosTask(List<Ball> balls, int startIdx, int endIdx, long dt, Board board) {
        this.balls = balls;
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.dt = dt;
        this.board = board;
    }

    @Override
    public void run() {
        for (int i = startIdx; i <= endIdx; i++) {
            balls.get(i).updateState(dt,board);
        }
    }
}
