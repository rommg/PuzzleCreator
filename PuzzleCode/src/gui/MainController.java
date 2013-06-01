package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

class MainController extends AbstractController<MainModel, MainView>{

	MainController(MainModel model, MainView view) {
		super(model,view);

		//add Controller listeners to View
		view.addMenuBtnsListener(new BtnListener());
	}

	class BtnListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton)e.getSource();
			JLabel lbl = (JLabel)btn.getComponent(0);
			if (lbl.getText().compareTo("Play") == 0) {
				view.playBtnClicked();
				return;
			}
			if (lbl.getText().compareTo("Hall of Fame") == 0) {
				view.hallOfFameBtnClicked();
				return;
			}
				
		}
	}
	
}
