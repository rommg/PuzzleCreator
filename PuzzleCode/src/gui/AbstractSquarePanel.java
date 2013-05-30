package gui;

import javax.swing.JPanel;

abstract class AbstractSquarePanel extends JPanel {

    private int row, col; // x,y of the square
    
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
	
}
