package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gui.MainController.BtnListener;

public class PrepareGameController extends AbstractController<PrepareGameModel, PrepareGameView> {

	PrepareGameController(PrepareGameModel model, PrepareGameView view) {
		super(model,view);
		
		view.addGoListener(new GoListener());
		view.addBackListener(new BackListener());
	}
	
	class GoListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.goBtnClicked();			
		}	
	}
	
	class BackListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			MainView.view.showWelcomeView();
		}
		
	}


}
