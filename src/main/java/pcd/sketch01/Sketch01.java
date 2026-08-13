package pcd.sketch01;

import pcd.sketch01.controller.ActiveController;
import pcd.sketch01.model.Board;
import pcd.sketch01.model.LargeBoardConf;
import pcd.sketch01.model.MassiveBoardConf;
import pcd.sketch01.model.MinimalBoardConf;
import pcd.sketch01.view.View;
import pcd.sketch01.view.ViewModel;

public class Sketch01 {

	
	public static void main(String[] argv) {

		/*
		 * Different board configs to try:
		 * - minimal: 2 small balls
		 * - large: 400 small balls
		 * - massive: 4500 small balls
		 */

		var boardConf = new MinimalBoardConf();
        //var boardConf = new LargeBoardConf();
        //var boardConf = new MassiveBoardConf();

		Board board = new Board();
		board.init(boardConf);

		ViewModel viewModel = new ViewModel();

        ActiveController controller = new ActiveController(board, viewModel);

        View view = new View(viewModel, controller, 1200, 800);

        controller.setView(view);
        controller.start();

						
//		viewModel.update(board, 0);
//		view.render();
//		waitAbit();
//
//		int nFrames = 0;
//		long t0 = System.currentTimeMillis();
//		long lastUpdateTime = System.currentTimeMillis();
//
//		/* main simulation loop */
//
//		while (true){
//
//
//
//			/* update board state */
//
//			long elapsed = System.currentTimeMillis() - lastUpdateTime;
//			lastUpdateTime = System.currentTimeMillis();
//			board.updateState(elapsed);
//
//			/* render */
//
//			nFrames++;
//			int framePerSec = 0;
//			long dt = (System.currentTimeMillis() - t0);
//			if (dt > 0) {
//				framePerSec = (int)(nFrames*1000/dt);
//			}
//
//			viewModel.update(board, framePerSec);
//			view.render();
//
//		}
	}
//
//	private static void waitAbit() {
//		try {
//			Thread.sleep(2000);
//		} catch (Exception ex) {}
//	}
//
}
