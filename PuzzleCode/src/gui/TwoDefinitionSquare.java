package gui;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

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
		lbl.setFont(this.getFont().deriveFont(8f));
		lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		add(lbl);
	}

	void addBottom(JDefinitionLabel lbl) {
		lbl.setBackground(Color.GRAY);
		lbl.setParentPanel(this);
		lbl.setBackground(Color.GRAY);
		lbl.setFont(this.getFont().deriveFont(8f));
		add(lbl);
	}

	public TwoDefinitionSquare(int row, int col) {
		super(row, col);
		super.labelCount = 2;
		
		setLayout(new GridLayout(2, 1)); // a 2X1 grid for top and bottom defs
		setFocusable(false);
		setBorder(new BevelBorder(BevelBorder.RAISED));

	}

}

