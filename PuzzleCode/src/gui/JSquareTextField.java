package gui;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class JSquareTextField extends JTextField {
    private int limit;
    private int row, col; // x,y of the square
  
    public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	private JSquareTextField(int limit, Color backgroundColor, int row, int col) {
        super();
        this.limit = limit;
		setHorizontalAlignment(JTextField.CENTER);
		setFont(this.getFont().deriveFont(40f));
	
		//getCaret().setVisible(false);
		setBackground(backgroundColor);
		setForeground(Color.BLACK);
		setHighlighter(null);

    }
    
    public JSquareTextField(Color backgroundColor, int row,int col) {
    	this(1,backgroundColor, row,col);
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