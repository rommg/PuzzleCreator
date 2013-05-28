package gui;


import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import net.miginfocom.swing.MigLayout;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JRadioButton;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;

import puzzleAlgorithm.PuzzleSquare;

import Utils.GuiAlgorithmConnector;
import Utils.GuiDBConnector;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;


public class PrepareGameView extends JPanel {
	private final ButtonGroup difficultyBtnsGrp = new ButtonGroup();
	private List<String> TopicsList;
	private List<JCheckBox> topicsCheckBoxes;
	private JPanel topicsPanel;
	private static PrepareGameModel model;

	public PrepareGameView() {
		initialize();
		this.setVisible(true);
	}

	static JPanel start() {
		PrepareGameView view = new PrepareGameView();
		model = new PrepareGameModel();
		@SuppressWarnings("unused")
		PrepareGameController controller = new PrepareGameController(model, view);
		return view;
	}

	private void initialize() {

		getTopics();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{577, 0};
		gridBagLayout.rowHeights = new int[]{461, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{577, 0};
		gbl_panel.rowHeights = new int[]{56, 258, 147, 0};
		gbl_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);

		JPanel difficultyPanel = new JPanel();
		GridBagConstraints gbc_difficultyPanel = new GridBagConstraints();
		gbc_difficultyPanel.anchor = GridBagConstraints.NORTH;
		gbc_difficultyPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_difficultyPanel.insets = new Insets(0, 0, 5, 0);
		gbc_difficultyPanel.gridx = 0;
		gbc_difficultyPanel.gridy = 0;
		panel.add(difficultyPanel, gbc_difficultyPanel);
		difficultyPanel.setBorder(new TitledBorder(null, "Difficulty", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		difficultyPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));


		JRadioButton easyRadioBtn = new JRadioButton("Easy");
		difficultyPanel.add(easyRadioBtn);
		difficultyBtnsGrp.add(easyRadioBtn);
		
		JRadioButton mediumHardBtn = new JRadioButton("Medium");
		difficultyPanel.add(mediumHardBtn);
		mediumHardBtn.setSelected(true);
		difficultyBtnsGrp.add(easyRadioBtn);


		JRadioButton hardRadioBtn = new JRadioButton("Hard");
		difficultyPanel.add(hardRadioBtn);
		difficultyBtnsGrp.add(hardRadioBtn);
		topicsPanel = new JPanel();
		GridBagConstraints gbc_topicsPanel = new GridBagConstraints();
		gbc_topicsPanel.fill = GridBagConstraints.BOTH;
		gbc_topicsPanel.insets = new Insets(0, 0, 5, 0);
		gbc_topicsPanel.gridx = 0;
		gbc_topicsPanel.gridy = 1;
		panel.add(topicsPanel, gbc_topicsPanel);
		topicsPanel.setBorder(new TitledBorder(null, "Topics", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		topicsPanel.setLayout(new GridLayout(2, 0, 0, 0));

		JPanel btnPanel = new JPanel();
		GridBagConstraints gbc_btnPanel = new GridBagConstraints();
		gbc_btnPanel.anchor = GridBagConstraints.NORTH;
		gbc_btnPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPanel.gridx = 0;
		gbc_btnPanel.gridy = 2;
		panel.add(btnPanel, gbc_btnPanel);

		JButton goBtn = new JButton();
		btnPanel.add(goBtn);
		goBtn.setIcon(new ImageIcon(PrepareGameView.class.getResource("/resources/start-icon.png")));

		addTopicsCheckBoxes();
	}

	/**
	 * 
	 * @return user selected topics
	 */
	public List<String> getUserSelectedTopics() {
		List<String> lst = new LinkedList<String>();
		for (JCheckBox box : topicsCheckBoxes) {
			if (box.isSelected())
				lst.add(box.getText());
		}
		return lst;
	}
	
	public int getDifficulty() {
		 for (Enumeration<AbstractButton> buttons = difficultyBtnsGrp.getElements(); buttons.hasMoreElements();) {
	            AbstractButton button = buttons.nextElement();
	            if (button.isSelected()) {
	                if (button.getText().compareTo("Easy") == 0)
	                	return 0;
	                if (button.getText().compareTo("Hard") == 0)
	                	return 1;
	            }
	        }
		 return -1; //Error - no btn selected.

	}

	private void addTopicsCheckBoxes() {
		topicsCheckBoxes = new LinkedList<JCheckBox>();
		for (String topic : TopicsList) {
			JCheckBox box = new JCheckBox();
			box.setText(topic);
			topicsPanel.add(box);
		}
	}

	private void getTopics() {
		//topicsList =GuiDBConnector.getTopics();
		//for now - fixed values
		TopicsList = new LinkedList<String>();

		TopicsList.add("Geography");
		TopicsList.add("Cinema");
		TopicsList.add("Music");
		TopicsList.add("Television");
		TopicsList.add("General");
		TopicsList.add("Israel");
	}

	void goBtnClicked() {
		PuzzleSquare[][] board = model.getBoard(this); // This is in separate Thread
		// in the meantime, answer question in WaitingView
	}
}