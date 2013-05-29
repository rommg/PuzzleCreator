package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.sun.corba.se.impl.protocol.giopmsgheaders.KeyAddr;



public class CrosswordController extends AbstractController<CrosswordModel, CrosswordView> {

	CrosswordController(CrosswordModel model, CrosswordView view) {
		super(model,view);
		//add Controller listeners to View
		view.addPauseListener(new PauseListener());
		view.addCheckListener(new CheckListener());

	}

	class PauseListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			view.pause();
		}

	}

	class CheckListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			view.notifyCorrectness(CrosswordModel.isCorrect(view));
		}

	}

	class SquareKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			char direction;
			switch (e.getKeyChar()) {
			case KeyEvent.VK_LEFT: {
				direction = 'l';
				break;
			}
			case KeyEvent.VK_RIGHT: {
				direction = 'r';
				break;
			}
			case KeyEvent.VK_UP : {
				direction = 'u';
				break;
			}
			case KeyEvent.VK_DOWN : {
				direction = 'd';
			}
			//view.keyPressed(e.getSource(), direction);
			}
		}

	}
}
