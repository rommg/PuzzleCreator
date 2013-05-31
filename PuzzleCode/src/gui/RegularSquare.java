package gui;

import java.awt.BorderLayout;
import java.awt.Color;

/**
 * A regular Square (with no definition) in the crossword
 * @author yonatan
 *
 */
final class RegularSquare extends AbstractSquarePanel{
	
	JSquareTextField field;

	public JSquareTextField getField() {
		return field;
	}

	public RegularSquare(JSquareTextField field, int row, int col) {
		super(row, col);
		super.labelCount = 1;
		setLayout(new BorderLayout());
		this.field = field;
		setBackground(Color.WHITE);		
		field.setBackground(super.getBackground());
		add(field, BorderLayout.CENTER);
	}

}
