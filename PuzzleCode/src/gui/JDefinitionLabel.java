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

	protected int difficulty;
	private PuzzleDefinition definition;
	private AbstractSquarePanel parentPanel;
	
	final float EASY_ONE = 18;
	final float EASY_TWO = 15f;

	final float MEDIUM_ONE = 14f;
	final float MEDIUM_TWO = 10f;

	final float HARD_ONE = 15;
	final float HARD_TWO = 5f;


	public void setParentPanel(AbstractSquarePanel parentPanel) {
		this.parentPanel = parentPanel;
	}

	public AbstractSquarePanel getParentPanel() {
		return parentPanel;
	}


	public PuzzleDefinition getDef() {
		return definition;
	}
	

	public JDefinitionLabel(PuzzleDefinition definition, int difficulty) {
	
		super("<html><p><center>" + definition.getDefinition() + 
				"<br> " + definition.getAdditionalInformation() + "</p></html>");
		super.setHorizontalAlignment(JLabel.CENTER);
		super.setVerticalAlignment(JLabel.NORTH);
		this.definition = definition;
		super.setToolTipText(definition.getDefinition());
		setForeground(Color.LIGHT_GRAY);
		setForeground(Color.BLACK);
		setFocusable(false);
	}
	
	
	
}
