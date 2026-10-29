package pcd.common.commands;

import pcd.common.model.Board;
import pcd.common.model.PlayerId;
import pcd.common.model.V2d;

public class MoveLeftCmd implements Cmd {

    private static final double KICK_STRENGTH = 1.5;
    private final PlayerId player;

    public MoveLeftCmd() {
        player = PlayerId.HUMAN;
    }

    public MoveLeftCmd(PlayerId player) {
        this.player = player;
    }

    @Override
    public void execute(Board board) {
        board.kickBall(player, new V2d(-KICK_STRENGTH, 0));
    }
}
