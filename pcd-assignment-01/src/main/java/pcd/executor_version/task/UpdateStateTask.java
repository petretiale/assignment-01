package pcd.executor_version.task;

import pcd.common.model.Ball;
import pcd.common.model.Board;

import java.util.List;

public class UpdateStateTask implements Task{


    private final List<Ball> balls;
    private final long dt;
    private Board board;

    public UpdateStateTask(Board board, List<Ball> b, long elapsed){
        this.board = board;
        this.balls = b;
        this.dt = elapsed;
    }

    @Override
    public void run() {
        for (Ball b : this.balls){
            b.updateState(this.dt, this.board);
        }
    }
}
