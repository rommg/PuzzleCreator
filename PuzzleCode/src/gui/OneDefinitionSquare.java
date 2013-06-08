package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.border.BevelBorder;

final class OneDefinitionSquare extends AbstractSquarePanel{

	private JDefinitionLabel definitionLbl;

	private JDefinitionLabel bottomDefinition;
	private int definitionCount = 0;

	public JDefinitionLabel getDefinitionLbl() {
		return definitionLbl;
	}

	public JDefinitionLabel getBottomDefinition() {
		return bottomDefinition;
	}

	public int getDefinitionCount() {
		return definitionCount;
	}

	void addTop(JDefinitionLabel lbl) {
		this.definitionLbl = lbl;
		setLayout(new BorderLayout());
		lbl.setBackground(Color.GRAY);
		lbl.setParentPanel(this);
		lbl.setFont(this.getFont().deriveFont(getFontSize(lbl)));
		add(lbl, BorderLayout.CENTER);
	
	}

	public OneDefinitionSquare(int row, int col) {
		super(row, col);
		super.labelCount = 1;
		setFocusable(false);
		setBorder(new BevelBorder(BevelBorder.RAISED));
	}

	@Override
	protected float getFontSize(JDefinitionLabel lbl) {
		switch (lbl.boardSize) {
		case 0 : return 20f;
		case 1: return 15f;
		case 2: return 10f;
		}
		return -1;
	}

}
