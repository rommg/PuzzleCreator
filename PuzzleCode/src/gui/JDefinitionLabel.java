package gui;

import java.awt.Color;

import javax.swing.JLabel;

import puzzleAlgorithm.PuzzleDefinition;

/**
 * The Label containing The definition. 
 * In OneDefinitionSquare there is one JDefinitionLabel.
 * In TwoDefinitionSquare there are two JDefinitionLabels. 
 * @author yonatan
 *
 */
public class JDefinitionLabel extends JLabel{

	private PuzzleDefinition def;
	private AbstractSquarePanel parentPanel;

	public void setParentPanel(AbstractSquarePanel parentPanel) {
		this.parentPanel = parentPanel;
	}

	public AbstractSquarePanel getParentPanel() {
		return parentPanel;
	}


	public PuzzleDefinition getDef() {
		return def;
	}
	

	public JDefinitionLabel(PuzzleDefinition def) {
		//super("<html><p>" + def.getDefinition() + "</p></html>");
		super("<html><center><p>" + "Signatory of the Israeli Declaration of Independence" + "</p></html>");
		this.def = def;
		setForeground(Color.LIGHT_GRAY);
		setForeground(Color.BLACK);
		setFocusable(false);
	}
	
	
	
}
