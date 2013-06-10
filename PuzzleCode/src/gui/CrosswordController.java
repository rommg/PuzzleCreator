package gui;

import java.awt.Color;
import java.awt.Component;
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
		//view.addKeyListenerToSquares(new SquareKeyListener());

	}


//	class SquareKeyListener extends KeyAdapter {
//		@Override
//		public void keyPressed(KeyEvent e) {
//			int direction = 0;
//			int key = (char) e.getKeyCode();
//			System.out.println(key);
//			switch (key) {
//			case KeyEvent.VK_LEFT: {
//				direction = 'l';
//				break;
//			}
//			case KeyEvent.VK_RIGHT: {
//				direction = 'r';
//				break;
//			}
//			case KeyEvent.VK_UP : {
//				direction = 'u';
//				break;
//			}
//			case KeyEvent.VK_DOWN : {
//				direction = 'd';
//				break;
//			}
//			default : return;
//			}
//			view.keyPressed((Component)e.getSource(), direction);
//		}
//
//	}
}
