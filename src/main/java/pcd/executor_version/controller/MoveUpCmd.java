package pcd.executor_version.controller;

import pcd.executor_version.model.Board;
import pcd.executor_version.model.PlayerId;
import pcd.executor_version.model.V2d;

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
