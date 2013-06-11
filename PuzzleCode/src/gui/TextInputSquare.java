package gui;

import java.awt.BorderLayout;
import java.awt.Color;

/**
 * A regular Square (with no definition) in the crossword
 * @author yonatan
 *
 */
final class TextInputSquare extends AbstractSquarePanel{
	
	SquareTextField field;

	public SquareTextField getField() {
		return field;
	}

	public TextInputSquare(SquareTextField field, int row, int col) {
		super(row, col);
		super.labelCount = 1;
		setLayout(new BorderLayout());
		this.field = field;
		setBackground(Color.WHITE);		
		field.setBackground(super.getBackground());
		add(field, BorderLayout.CENTER);
	}

//	@Override
//	protected float getFontSize(JDefinitionLabel lbl) {
//		// TODO Auto-generated method stub
//		return 0;
//	}


}
