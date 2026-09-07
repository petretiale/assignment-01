package pcd.sketch01.controller;

import pcd.sketch01.model.Board;
import pcd.sketch01.model.PlayerId;
import pcd.sketch01.model.V2d;

public class MoveDownCmd implements Cmd{

    private static final double KICK_STRENGTH = 1.5;
    private final PlayerId player;

    public MoveDownCmd() {
        player = PlayerId.HUMAN;
    }

    public MoveDownCmd(PlayerId player) {
        this.player = player;
    }

    @Override
    public void execute(Board board) {
        board.kickBall(player, new V2d(0, -KICK_STRENGTH));
    }
}
