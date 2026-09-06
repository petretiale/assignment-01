package pcd.sketch01.controller;

import pcd.sketch01.model.Board;
import pcd.sketch01.model.V2d;

import java.util.Random;

public class MoveBotRandomCmd implements Cmd{

    private static final double KICK_STRENGTH = 1.5;
    private final Random rand = new Random(2);;

    @Override
    public void execute(Board board) {
        var angle = rand.nextDouble()*Math.PI*2.0;
        var v = new V2d(Math.cos(angle),Math.sin(angle)).mul(KICK_STRENGTH);
        board.getBotBall().kick(v);
    }
}

