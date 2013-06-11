package gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

/**
 * a filler square that some templates need
 * @author yonatan
 *
 */
class PlainSquare extends AbstractSquarePanel {

	public PlainSquare(int row, int col) {
		super(row, col);
		super.labelCount = 0;
		setFocusable(false);
		
		setLayout(new BorderLayout());
		JLabel emptyLabel = new JLabel();
		emptyLabel.setBackground(Color.LIGHT_GRAY);
		emptyLabel.setOpaque(true);
		emptyLabel.setFocusable(false);
		emptyLabel.setBorder(new BevelBorder(BevelBorder.RAISED));
		add(emptyLabel, BorderLayout.CENTER);
	}
}
