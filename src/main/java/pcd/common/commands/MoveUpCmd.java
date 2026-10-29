package pcd.common.commands;

import pcd.common.model.Board;
import pcd.common.model.PlayerId;
import pcd.common.model.V2d;

public class MoveUpCmd implements Cmd {

    private static final double KICK_STRENGTH = 1.5;
    private final PlayerId player;

    public MoveUpCmd() {
        player = PlayerId.HUMAN;
    }

    public MoveUpCmd(PlayerId player) {
        this.player = player;
    }

    @Override
    public void execute(Board board) {
        board.kickBall(player, new V2d(0, KICK_STRENGTH));
    }
}
