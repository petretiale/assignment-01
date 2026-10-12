package pcd.multi_threaded_version.controller;

import pcd.multi_threaded_version.model.Board;
import pcd.multi_threaded_version.model.PlayerId;
import pcd.multi_threaded_version.model.V2d;

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
