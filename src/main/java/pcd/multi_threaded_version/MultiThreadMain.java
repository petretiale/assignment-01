package pcd.multi_threaded_version;

import pcd.multi_threaded_version.controller.MTActiveController;
import pcd.shared.model.*;
import pcd.shared.view.View;
import pcd.shared.view.ViewModel;

public class MultiThreadMain {

	
	public static void main(String[] argv) {

		/*
		 * Different board configs to try:
		 * - minimal: 2 small balls
		 * - large: 400 small balls
		 * - massive: 4500 small balls
		 */

		//var boardConf = new MinimalBoardConf();
        //var boardConf = new LargeBoardConf();
        var boardConf = new MassiveBoardConf();

		Board board = new Board();
		board.init(boardConf);

		ViewModel viewModel = new ViewModel();

        MTActiveController controller = new MTActiveController(board, viewModel);

        View view = new View(viewModel, controller, 1200, 800);

        BotAgent bot = new BotAgent(controller, board);

        controller.setView(view);
        controller.start();
        bot.start();
	}
}
