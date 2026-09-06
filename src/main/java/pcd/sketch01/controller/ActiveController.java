package pcd.sketch01.controller;

import pcd.sketch01.model.Board;
import pcd.sketch01.util.BoundedBuffer;
import pcd.sketch01.util.BoundedBufferImpl;
import pcd.sketch01.view.View;
import pcd.sketch01.view.ViewModel;

public class ActiveController extends Thread {

    private final BoundedBuffer<Cmd> cmdBuffer;
    private final Board board;
    private final ViewModel viewModel;
    private View view;

    public ActiveController(Board board, ViewModel viewModel) {
        this.cmdBuffer = new BoundedBufferImpl<>(100);
        this.board = board;
        this.viewModel = viewModel;

    }

    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void run() {
// perchè farlo qui e non in view come in sketch 2?
        viewModel.update(board, 0);
        view.render();
        waitAbit();

        int nFrames = 0;
        long t0 = System.currentTimeMillis();
        long lastUpdateTime = System.currentTimeMillis();

        while (true) {
            long current = System.currentTimeMillis();
            long elapsed = current - lastUpdateTime;
            lastUpdateTime = current;

            // Esecuzione dei comandi in coda
            Cmd cmd;
            while ((cmd = cmdBuffer.poll()) != null) {
                cmd.execute(board);
            }

            board.updateState(elapsed);

            // Calcolo FPS
            nFrames++;
            int framePerSec = 0;
            long dt = current - t0;
            if (dt > 0) {
                framePerSec = (int) (nFrames * 1000 / dt);
            }

            viewModel.update(board, framePerSec);
            view.render();

//            try {
//                Thread.sleep(16);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
        }
    }

    public void notifyNewCmd(Cmd cmd) {
        try {
            cmdBuffer.put(cmd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void waitAbit() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
    }
}
