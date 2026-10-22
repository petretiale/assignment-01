package pcd.executor_version.controller;

import pcd.executor_version.model.Ball;
import pcd.executor_version.model.Board;

public class UpdateStateTask implements Task{


    private final Ball ball;
    private final long dt;
    private Board board;

    public UpdateStateTask(Board board, Ball b, long elapsed){
        this.board = board;
        this.ball = b;
        this.dt = elapsed;
    }

    @Override
    public void run() {
        this.ball.updateState(this.dt, this.board);
    }
}
