package pcd.executor_version.controller;

import pcd.executor_version.model.Board;
import pcd.executor_version.model.PlayerId;
import pcd.executor_version.model.V2d;

public class MoveRightCmd implements Cmd {

    private static final double KICK_STRENGTH = 1.5;
    private final PlayerId player;

    public MoveRightCmd() {
        player = PlayerId.HUMAN;
    }

    public MoveRightCmd(PlayerId player) {
        this.player = player;
    }

    @Override
    public void execute(Board board) {
        board.kickBall(player, new V2d(KICK_STRENGTH, 0));
    }
}
