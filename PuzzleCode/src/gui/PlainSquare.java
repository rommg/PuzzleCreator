package gui;

import java.awt.Color;

import javax.swing.border.BevelBorder;

/**
 * a filler square that some templates need
 * @author yonatan
 *
 */
class PlainSquare extends AbstractSquarePanel {

	public PlainSquare(int row, int col) {
		super(row, col);
		setBackground(Color.GRAY);
		setOpaque(true);
		setFocusable(false);
		setBorder(new BevelBorder(BevelBorder.RAISED));
	}
}
