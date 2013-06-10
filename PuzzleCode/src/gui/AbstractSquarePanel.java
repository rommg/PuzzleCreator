package gui;

import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * abstract layout of a square in the crossword board
 * @author yonatan
 *
 */
abstract class AbstractSquarePanel extends JPanel {

    private int row, col; // x,y of the square
    protected int labelCount = 0;
    
    int getLabelCount() {
		return labelCount;
	}

	int getRow() {
		return row;
	}

	int getCol() {
		return col;
	}
	
	public AbstractSquarePanel(int row, int col) {
		super();
		this.row = row;
		this.col = col;
		
		// change traversal policy
		//addTraversalKey("UP",KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS);
		//addTraversalKey("DOWN",KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS);
		addTraversalKey(KeyEvent.VK_LEFT, KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		addTraversalKey(KeyEvent.VK_RIGHT,KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
	}
	

	
	private void addTraversalKey(int keystroke, int strokeid) {
		Set<AWTKeyStroke> set = this.getFocusTraversalKeys(strokeid);
	    set = new HashSet<AWTKeyStroke>(set);
	    KeyStroke up = KeyStroke.getKeyStroke(keystroke,0);
	    this.setFocusTraversalKeys(strokeid, set);
	    set.add(up);
	}
	
//	protected abstract float getFontSize(JDefinitionLabel lbl);
    
}
