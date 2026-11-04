package pcd.executor_version.view;


import pcd.executor_version.controller.ActiveController;

public class View {

	private ViewFrame frame;
	private ViewModel viewModel;
    private ActiveController controller;
	
	public View(ViewModel model, ActiveController controller, int w, int h) {
		frame = new ViewFrame(model, controller, w, h);
		frame.setVisible(true);
		this.viewModel = model;
	}
		
	public void render() {
		frame.render();
	}
	
	public ViewModel getViewModel() {
		return viewModel;
	}
}
