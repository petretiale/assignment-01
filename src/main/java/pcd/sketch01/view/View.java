package pcd.sketch01.view;


import pcd.sketch01.controller.Controller;

public class View {

	private ViewFrame frame;
	private ViewModel viewModel;
    private Controller controller;
	
	public View(ViewModel model,Controller controller, int w, int h) {
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
