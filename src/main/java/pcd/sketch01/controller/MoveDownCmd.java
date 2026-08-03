package pcd.sketch01.controller;

import pcd.sketch01.model.Board;
import pcd.sketch01.model.V2d;

public class MoveDownCmd implements Cmd{

    private static final double KICK_STRENGTH = 1.5;

    @Override
    public void execute(Board board) {
        board.getPlayerBall().kick(new V2d(0, -KICK_STRENGTH));
    }
}
