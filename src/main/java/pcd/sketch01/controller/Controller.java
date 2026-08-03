package pcd.sketch01.controller;

import pcd.sketch01.model.Board;
import pcd.sketch01.model.V2d;

public class Controller {

    private static final double KICK_STRENGTH = 1.5;
    private final Board board;

    public Controller(Board board) {
        this.board = board;
    }

    public void moveUp() {
        board.getPlayerBall().kick(new V2d(0, KICK_STRENGTH));
    }

    public void moveDown() {
        board.getPlayerBall().kick(new V2d(0, -KICK_STRENGTH));
    }

    public void moveLeft() {
        board.getPlayerBall().kick(new V2d(-KICK_STRENGTH, 0));
    }

    public void moveRight() {
        board.getPlayerBall().kick(new V2d(KICK_STRENGTH, 0));
    }
}
