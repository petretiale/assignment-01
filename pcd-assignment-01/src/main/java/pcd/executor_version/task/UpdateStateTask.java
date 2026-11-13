package pcd.executor_version.task;

import pcd.common.model.Ball;
import pcd.common.model.Board;

import java.util.List;

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
        ball.updateState(this.dt, this.board);
    }
}
