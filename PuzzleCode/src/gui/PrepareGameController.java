package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gui.MainController.playBtnListener;

public class PrepareGameController extends AbstractController<PrepareGameModel, PrepareGameView> {

	PrepareGameController(PrepareGameModel model, PrepareGameView view) {
		super(model,view);
		
		view.addGoListener(new GoListener());
	}
	
	class GoListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.goBtnClicked();			
		}
		
		
	}


}
