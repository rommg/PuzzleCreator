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

	protected int boardSize;
	private PuzzleDefinition definition;
	private AbstractSquarePanel parentPanel;
	
	static final float EASY_ONE = 30f;
	static final float EASY_TWO = 15f;

	static final float MEDIUM_ONE = 20f;
	static final float MEDIUM_TWO = 10f;

	static final float HARD_ONE = 10f;
	static final float HARD_TWO = 5f;


	public void setParentPanel(AbstractSquarePanel parentPanel) {
		this.parentPanel = parentPanel;
	}

	public AbstractSquarePanel getParentPanel() {
		return parentPanel;
	}


	public PuzzleDefinition getDef() {
		return definition;
	}
	

	public JDefinitionLabel(PuzzleDefinition definition, int boardSize) {
		//super("<html><p>" + def.getDefinition() + "</p></html>");
		super("<html><p><center>" + definition.getDefinition() + "</p></html>");
		super.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		this.definition = definition;
		setForeground(Color.LIGHT_GRAY);
		setForeground(Color.BLACK);
		setFocusable(false);
	}
	
	
	
}
