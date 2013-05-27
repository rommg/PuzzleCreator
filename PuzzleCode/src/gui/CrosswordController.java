package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class CrosswordController extends AbstractController<CrosswordModel, CrosswordView> {
	
	CrosswordController(CrosswordModel model, CrosswordView view) {
		super(model,view);
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
