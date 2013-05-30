package gui;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class JSquareTextField extends JTextField {
    private int limit;

    private JSquareTextField(int limit) {
//	private JSquareTextField(int limit, Color backgroundColor, int row, int col) {
        super();
        this.limit = limit;
		setHorizontalAlignment(JTextField.CENTER);
		setFont(this.getFont().deriveFont(40f));
//		setBackground(backgroundColor);
		setForeground(Color.BLACK);
		setHighlighter(null);
		
		// change traversal policy
		//addTraversalKey("UP",KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS);
		//addTraversalKey("DOWN",KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS);
		addTraversalKey(KeyEvent.VK_LEFT,KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		addTraversalKey(KeyEvent.VK_RIGHT,KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);

    }

	private void addTraversalKey(int keystroke, int strokeid) {
		Set<AWTKeyStroke> set = this.getFocusTraversalKeys(strokeid);
	    set = new HashSet<AWTKeyStroke>(set);
	    KeyStroke up = KeyStroke.getKeyStroke(keystroke,0);
	    this.setFocusTraversalKeys(strokeid, set);
	    set.add(up);
	}
    
    public JSquareTextField() {
//    	this(1,backgroundColor, row,col);
    	this(1);
    }

    @Override
    protected Document createDefaultModel() {
        return new LimitDocument();
    }

    private class LimitDocument extends PlainDocument {

        @Override
        public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
            if (str == null) return;
            getCaret().setVisible(false);
            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }       
    }

}