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

/**
 * a text box (in a RegulareSquare) that holds at most one letter
 * @author yonatan
 *
 */
public class SquareTextField extends JTextField {
    private int limit;

    private SquareTextField(int limit) {
        super();
        this.limit = limit;
		setHorizontalAlignment(JTextField.CENTER);
		setFont(this.getFont().deriveFont(40f));
		setForeground(Color.BLACK);
		setHighlighter(null);

    }

    public SquareTextField() {
    	this(1);
    }

    /**
     * DefaultModel now limits the text according to limit
     */
    @Override
    protected Document createDefaultModel() {
        return new LimitDocument();
    }

    private class LimitDocument extends PlainDocument {

        @Override
        public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
            if (str == null) return;
            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }       
    }

}