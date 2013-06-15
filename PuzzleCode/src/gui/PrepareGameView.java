package gui;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JRadioButton;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;


import utils.DBUtils;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;


public class PrepareGameView extends JPanel {
	private final ButtonGroup difficultyBtnsGrp = new ButtonGroup();
	Map<String,Integer> topicsList;
	private List<JCheckBox> topicsCheckBoxes;
	private JPanel centerPanel;
	private JButton goBtn;
	private JButton backBtn;
	private JPanel topicsPanel;
	private int[] selectedTopicsId;

	private static final String GENERAL_KNOWLEDGE_TOPIC = "General Knowledge";

	public PrepareGameView() {
		initialize();
		this.setVisible(true);
	}

	static JPanel start() {
		return new PrepareGameView();
	}

	private void initialize() {

		
		setLayout(new BorderLayout());

		JPanel difficultyPanel = new JPanel();
		difficultyPanel.setBackground(Color.WHITE);
		add(difficultyPanel, BorderLayout.NORTH);
		difficultyPanel.setBorder(new TitledBorder(null, "Difficulty", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		difficultyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));


		JRadioButton easyRadioBtn = new JRadioButton("Easy");
		easyRadioBtn.setBackground(Color.WHITE);
		difficultyPanel.add(easyRadioBtn);
		difficultyBtnsGrp.add(easyRadioBtn);

		JRadioButton mediumHardBtn = new JRadioButton("Medium");
		mediumHardBtn.setBackground(Color.WHITE);
		difficultyPanel.add(mediumHardBtn);
		mediumHardBtn.setSelected(true);
		difficultyBtnsGrp.add(mediumHardBtn);


		JRadioButton hardRadioBtn = new JRadioButton("Hard");
		hardRadioBtn.setBackground(Color.WHITE);
		difficultyPanel.add(hardRadioBtn);
		difficultyBtnsGrp.add(hardRadioBtn);
		
		centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new GridLayout(2, 1, 0, 0));

		topicsPanel = new JPanel();
		topicsPanel.setBackground(Color.WHITE);
		topicsPanel.setBorder(new TitledBorder(null, "Topics", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		centerPanel.add(topicsPanel);
		topicsPanel.setLayout(new GridLayout(4, 2, 0, 0));

		JPanel btnPanel = new JPanel();
		btnPanel.setBackground(Color.WHITE);
		centerPanel.add(btnPanel);

		goBtn = new JButton(new ImageIcon(getClass().getResource("/resources/forward.png")));
		goBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				goBtnClicked();
			}
		});
		backBtn = new JButton(new ImageIcon(getClass().getResource("/resources/back.png")));
		backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainView.getView().showWelcomeView();
			}
		});
		btnPanel.add(backBtn);
		btnPanel.add(goBtn);

		getTopics(); // query topics from DB
		if (topicsList.size() == 0) {
			Utils.showDBConnectionErrorMessage();
			return;
		}
		addTopicsCheckBoxes(); // add then as checkboxes
	}

	void goBtnClicked() {
		int[] selectedTopicIDs = getUserSelectedTopics();
		int difficulty = getUserSelectedDifficulty();

		if (selectedTopicIDs.length < 2) { // must choose at least two topics
			JOptionPane.showMessageDialog(MainView.getView().getFrame(),
					"At least two Topics must be selected.");
		}
		else {
			MainView.getView().showWaitView(selectedTopicIDs,difficulty);
		}
	}


	/**
	 * 
	 * @return user selected topics
	 */
	public int[] getUserSelectedTopics() {

		List<Integer> selectedTopics = new ArrayList<Integer>();
		
		for (JCheckBox box : topicsCheckBoxes) {
			if (box.isSelected()){
				selectedTopics.add(topicsList.get(box.getText()));
			}
		}
		
		// add General Knowledge update
		selectedTopics.add(topicsList.get(GENERAL_KNOWLEDGE_TOPIC));


		int[] topicsArray = new int[selectedTopics.size()];
		
		for (int i = 0; i < selectedTopics.size(); i++){
			topicsArray[i] = selectedTopics.get(i);
		}

		this.selectedTopicsId = topicsArray;
		return topicsArray;
	}

	public int[] getSelectedTopicsIds() {
		return this.selectedTopicsId;
	}

	public int getUserSelectedDifficulty() {
		for (Enumeration<AbstractButton> buttons = difficultyBtnsGrp.getElements(); buttons.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();
			if (button.isSelected()) {
				if (button.getText().compareTo("Easy") == 0)
					return 0;
				if (button.getText().compareTo("Medium") == 0)
					return 1;
				if (button.getText().compareTo("Hard") == 0)
					return 2;
			}
		}
		return -1; //Error - no btn selected.

	}

	private void addTopicsCheckBoxes() {
		topicsCheckBoxes = new LinkedList<JCheckBox>();
		for (String topic :topicsList.keySet()) {
			if (topic.compareTo(GENERAL_KNOWLEDGE_TOPIC) != 0 ) { // do not add checkbox for general knowledge 
				JCheckBox box = new JCheckBox();
				box.setBackground(Color.WHITE);
				box.setSelected(true);
				box.setText(topic);
				topicsCheckBoxes.add(box);
				topicsPanel.add(box);
			}
		}
	}

	private void getTopics() {
		topicsList  = DBUtils.getAllTopicIDsAndNames();
		
	}
}
