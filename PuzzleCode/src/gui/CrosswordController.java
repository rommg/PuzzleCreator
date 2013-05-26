package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class CrosswordController {
	
	private CrosswordModel model;
	private CrosswordView view;
	CrosswordController(CrosswordModel model, CrosswordView view) {
		this.model = model;
		this.view = view;
		
		//add Controller listeners to View
		view.addPauseListener(new PauseListener());
	}
	
	class PauseListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			view.pause();
		}
		
	}
}
