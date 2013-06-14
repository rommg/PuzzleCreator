package gui;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

/**
 * A DefinitionSquare with two definitions
 * @author yonatan
 *
 */
final public class TwoDefinitionSquare extends AbstractSquarePanel {

	private JDefinitionLabel topDefinitionLbl;
	private JDefinitionLabel bottomDefinitionLbl;

	private int definitionCount = 0;

	public JDefinitionLabel getTopDefinitionLbl() {
		return topDefinitionLbl;
	}

	public JDefinitionLabel getBottomDefinition() {
		return bottomDefinitionLbl;
	}

	public int getDefinitionCount() {
		return definitionCount;
	}

	void addTop(JDefinitionLabel lbl) {
		this.topDefinitionLbl = lbl;
		lbl.setBackground(Color.GRAY);
		lbl.setParentPanel(this);
		lbl.setBackground(Color.GRAY);
		//lbl.setFont(this.getFont().deriveFont(getFontSize(lbl)));
		lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		add(lbl);
	}
	


	void addBottom(JDefinitionLabel lbl) {
		this.bottomDefinitionLbl = lbl;
		lbl.setBackground(Color.GRAY);
		lbl.setParentPanel(this);
		lbl.setBackground(Color.GRAY);
		//lbl.setFont(this.getFont().deriveFont(getFontSize(lbl)));
		add(lbl);
	}
	
//	@Override
//	protected float getFontSize(JDefinitionLabel lbl) {
//		switch (lbl.difficulty) {
//		case 0 : return lbl.EASY_TWO;
//		case 1: return lbl.MEDIUM_TWO;
//		case 2: return lbl.HARD_TWO;
//		}
//		return -1;
//	}

	public TwoDefinitionSquare(int row, int col) {
		super(row, col);
		super.labelCount = 2;
		
		setLayout(new GridLayout(2, 1)); // a 2X1 grid for top and bottom defs
		setFocusable(false);
		setBorder(new BevelBorder(BevelBorder.RAISED));

	}

	@Override
	public void setEnabled(boolean enabled) {
		topDefinitionLbl.setEnabled(enabled);
		bottomDefinitionLbl.setEnabled(enabled);
		super.setEnabled(enabled);
	}

}

