package pcd.sketch01.model;

import pcd.sketch01.controller.ActiveController;
import pcd.sketch01.controller.MoveBotRandomCmd;

public class Bot extends Thread{
    private final ActiveController controller;

    long lastKickTime;

    public Bot(ActiveController controller){
        this.controller = controller;
        lastKickTime = System.currentTimeMillis();
    }

    public void run() {
        while (true) {
            if (canMove()){
                move();
            }
        }
    }

    private boolean canMove() {
        return System.currentTimeMillis() - lastKickTime > 5000;
    }

    private void move() {
        controller.notifyNewCmd(new MoveBotRandomCmd());
        lastKickTime = System.currentTimeMillis();
    }
}
