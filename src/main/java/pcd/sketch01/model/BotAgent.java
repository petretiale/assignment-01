package pcd.sketch01.model;

import pcd.sketch01.controller.*;

import java.util.Random;

public class BotAgent extends Thread {

    private final ActiveController controller;
    private final Board board;
    private final Random rnd;


    public BotAgent(ActiveController controller, Board board) {
        this.controller = controller;
        this.board = board;
        this.rnd = new Random();
    }

    @Override
    public void run() {
        while (!board.isGameOver()) {
            try {
                // Attesa asincrona tra una mossa e l'altra
                Thread.sleep(1500);

                if (board.isGameOver()) {
                    break;
                }

                // Sceglie a caso una delle 4 direzioni
                Cmd cmd = chooseRandomMove();
                controller.notifyNewCmd(cmd);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private Cmd chooseRandomMove() {
        int choice = rnd.nextInt(4);
        return switch (choice) {
            case 0 -> new MoveUpCmd(PlayerId.BOT);
            case 1 -> new MoveDownCmd(PlayerId.BOT);
            case 2 -> new MoveLeftCmd(PlayerId.BOT);
            default -> new MoveRightCmd(PlayerId.BOT);
        };
    }

}
