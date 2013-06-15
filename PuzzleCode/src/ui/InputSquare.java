package ui;

import java.awt.BorderLayout;
import java.awt.Color;

/**
 * A regular Square (with no definition) in the crossword
 * @author yonatan
 *
 */
final class InputSquare extends AbstractSquarePanel{
	
	private SquareTextField field;

	public SquareTextField getField() {
		return field;
	}

	public InputSquare(SquareTextField field, int row, int col) {
		super(row, col);
		super.labelCount = 1;
		setLayout(new BorderLayout());
		this.field = field;
		setBackground(Color.WHITE);		
		field.setBackground(super.getBackground());
		add(field, BorderLayout.CENTER);
	}
}
