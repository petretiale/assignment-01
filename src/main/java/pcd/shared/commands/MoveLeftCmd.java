package pcd.shared.commands;

import pcd.shared.model.Board;
import pcd.shared.model.PlayerId;
import pcd.shared.model.V2d;

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
