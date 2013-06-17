package ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

/**
 * a filler square that some templates need
 * @author yonatan
 *
 */
@SuppressWarnings("serial")
class PlainSquare extends AbstractSquarePanel {

	private JLabel emptyLabel;

	public PlainSquare(int row, int col) {
		super(row, col);
		super.labelCount = 0;
		setFocusable(false);
		
		setLayout(new BorderLayout());
		emptyLabel = new JLabel();
		emptyLabel.setBackground(Color.LIGHT_GRAY);
		emptyLabel.setOpaque(true);
		emptyLabel.setFocusable(false);
		emptyLabel.setBorder(new BevelBorder(BevelBorder.RAISED));
		add(emptyLabel, BorderLayout.CENTER);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		emptyLabel.setEnabled(enabled);
		super.setEnabled(enabled);
	}
}
