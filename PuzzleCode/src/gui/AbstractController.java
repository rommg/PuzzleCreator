package gui;

abstract class AbstractController<S,T> {
	protected T view;
	protected S model;
	
	/**
	 * 
	 * @param model
	 * @param view
	 */
	AbstractController(S model, T view) {
		this.view = view;
		this.model = model;
	}
}
