package ui;

import javax.swing.JPanel;

/**
 * abstract layout of a square in the crossword board
 * @author yonatan
 *
 */
@SuppressWarnings("serial")
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
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}
}
