package ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

final class OneDefinitionSquare extends AbstractSquarePanel{

	private JDefinitionLabel definitionLbl;

	private JDefinitionLabel bottomDefinition;
	private int definitionCount = 0;

	public JDefinitionLabel getDefinitionLbl() {
		return definitionLbl;
	}

	public int getDefinitionCount() {
		return definitionCount;
	}

	void addTop(JDefinitionLabel lbl) {
		this.definitionLbl = lbl;
		setLayout(new BorderLayout());
		lbl.setBackground(Color.GRAY);
		lbl.setParentPanel(this);
		//lbl.setFont(this.getFont().deriveFont(getFontSize(lbl)));
		add(lbl, BorderLayout.CENTER);
	
	}

	public OneDefinitionSquare(int row, int col) {
		super(row, col);
		super.labelCount = 1;
		setFocusable(false);
		setBorder(new BevelBorder(BevelBorder.RAISED));
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		definitionLbl.setEnabled(enabled);
		super.setEnabled(enabled);
	}

}
