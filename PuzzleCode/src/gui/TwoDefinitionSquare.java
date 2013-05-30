package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

import Utils.Logger;

final public class TwoDefinitionSquare extends AbstractSquarePanel {

	private JDefinitionLabel TopDefinitionLbl;
	private JDefinitionLabel bottomDefinitionLbl;

	private int definitionCount = 0;

	public JDefinitionLabel getTopDefinitionLbl() {
		return TopDefinitionLbl;
	}

	public JDefinitionLabel getBottomDefinition() {
		return bottomDefinitionLbl;
	}

	public int getDefinitionCount() {
		return definitionCount;
	}

	void addTop(JDefinitionLabel lbl) {
		lbl.setBackground(Color.GRAY);
		lbl.setParentPanel(this);
		lbl.setBackground(Color.GRAY);
		lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		add(lbl);
	}

	void addBottom(JDefinitionLabel lbl) {
		lbl.setBackground(Color.GRAY);
		lbl.setParentPanel(this);
		lbl.setBackground(Color.GRAY);
		add(lbl);
	}

	public TwoDefinitionSquare(int row, int col) {
		super(row, col);
		setLayout(new GridLayout(2, 1)); // a 2X1 grid for top and bottom defs
		setFocusable(false);
		setBorder(new BevelBorder(BevelBorder.RAISED));

	}

}

