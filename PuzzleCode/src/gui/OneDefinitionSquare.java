package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

import Utils.Logger;

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
		setLayout(new BorderLayout());
		lbl.setBackground(Color.GRAY);
		lbl.setParentPanel(this);
		add(lbl, BorderLayout.CENTER);
	
	}

	public OneDefinitionSquare(int row, int col) {
		super(row, col);
		setFocusable(false);
		setBorder(new BevelBorder(BevelBorder.RAISED));
	}

}
