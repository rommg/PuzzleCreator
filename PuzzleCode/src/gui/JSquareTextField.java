package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class JSquareTextField extends JTextField {
    private int limit;
  
    private JSquareTextField(int limit, Color backgroundColor) {
        super();
        this.limit = limit;
		setHorizontalAlignment(JTextField.CENTER);
		setFont(this.getFont().deriveFont(40f));
		setCaretColor(backgroundColor);
		setBackground(backgroundColor);
		setForeground(Color.BLACK);
		setHighlighter(null);

		addCaretListener(new CaretListener() { // for deleting inserted letter if needed
			
			@Override
			public void caretUpdate(CaretEvent arg0) {
				JSquareTextField.this.setCaretPosition(0);
				
			}
		});

    }
    
    public JSquareTextField(Color backgroundColor) {
    	this(1,backgroundColor);
    }

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