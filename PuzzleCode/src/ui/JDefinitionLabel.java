package ui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import core.algorithm.PuzzleDefinition;


/**
 * The Label containing The definition. 
 * In OneDefinitionSquare there is one JDefinitionLabel.
 * In TwoDefinitionSquare there are two JDefinitionLabels. 
 * @author yonatan
 *
 */
@SuppressWarnings("serial")
public class JDefinitionLabel extends JLabel{

	protected int difficulty;
	private PuzzleDefinition definition;
	private AbstractSquarePanel parentPanel;

	public void setParentPanel(AbstractSquarePanel parentPanel) {
		this.parentPanel = parentPanel;
	}

	public AbstractSquarePanel getParentPanel() {
		return parentPanel;
	}


	public PuzzleDefinition getDef() {
		return definition;
	}
	

	public JDefinitionLabel(PuzzleDefinition definition) {
	
		super("<html><center>" + definition.getDefinition() + " "+ definition.getAdditionalInformation() + "</html>");
		super.setHorizontalAlignment(SwingConstants.CENTER);
		super.setVerticalAlignment(SwingConstants.CENTER);
		this.definition = definition;
		super.setToolTipText(definition.getDefinition());
		setForeground(Color.LIGHT_GRAY);
		setForeground(Color.BLACK);
		setFocusable(false);
	}
	
}
