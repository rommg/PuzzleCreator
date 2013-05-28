package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

class MainController extends AbstractController<MainModel, MainView>{

	MainController(MainModel model, MainView view) {
		super(model,view);

		//add Controller listeners to View
		view.addMenuBtnsListener(new playBtnListener());
	}

	class playBtnListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();
			if (view.btnLabels.get(btn).getText().compareTo("Play") == 0) {
				view.playBtnClicked();
			}
		}
	}
}
