package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import puzzleAlgorithm.Answer;

import utils.Logger;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class AddDefView extends JPanel {

	List<String> topicList = null;
	List<String> definitionList = null;
	private JButton addBtn;
	private JPanel mainPanel;
	private JComboBox<String> definitionBox;
	private JComboBox<String> topicBox;
	private JTextField answerField;
	private JLabel label;

	static AddDefView start() {
		return new AddDefView();
	}

	/**
	 * Create the panel.
	 */
	private  AddDefView() {
		initialize();

		setLayout(new GridLayout(4, 1));

		JPanel comboboxPanel = new JPanel();
		comboboxPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		add(comboboxPanel);
		comboboxPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		topicBox = createAutoCompleteBox(topicList);
		topicBox.addItemListener(new TopicBoxListener());
		JPanel topicBoxPanel = new JPanel();
		topicBoxPanel.setBorder(new TitledBorder("Topic"));
		topicBoxPanel.add(topicBox);
		comboboxPanel.add(topicBoxPanel);

		definitionBox = createAutoCompleteBox(definitionList);
		definitionBox.setEnabled(false);
		JPanel definitionBoxPanel = new JPanel();
		definitionBoxPanel.setBorder(new TitledBorder("Definition"));
		definitionBoxPanel.add(definitionBox);
		comboboxPanel.add(definitionBoxPanel);

		answerField = new LimitedTextField(20);
		answerField.setEnabled(false);
		answerField.addKeyListener(new JTextFieldListener());
		JPanel textPanel = new JPanel();
		textPanel.setBorder(new TitledBorder("Answer"));
		textPanel.add(answerField);
		comboboxPanel.add(textPanel);

		addBtn = new JButton(new ImageIcon(AddDefView.class.getResource("/resources/add.png")));
		addBtn.addActionListener(new AddBtnListener());
		addBtn.setEnabled(false);
		comboboxPanel.add(addBtn);

		mainPanel = new JPanel();
		label = new JLabel();
		mainPanel.add(label);
		add(mainPanel);

		JPanel emptyPanel = new JPanel();
		add(emptyPanel);

		JPanel btnPanel = new JPanel();
		add(btnPanel);

		JButton btnBack = new JButton("");
		btnBack.setIcon(new ImageIcon(AddDefView.class.getResource("/resources/back.png")));
		btnBack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				MainView.view.showWelcomeView();
			}
		});

		btnPanel.add(btnBack);		
	}

	private void initialize() {
		//topicList = getTopics(); Query DB for topics
		topicList = new ArrayList<String>();
		topicList.add("cinema");
		topicList.add("bla");

		definitionList = new ArrayList<String>();
		definitionList.add("");
		definitionList.add("saaaaaaaaaaaaaaaaaaaaaaaa");
	}

	private JComboBox<String> createAutoCompleteBox(List<String> valueList) {

		JComboBox<String> box = new JComboBox<String>();
		AutoCompleteSupport<Object> autoBox =  AutoCompleteSupport.install(box, GlazedLists.eventListOf(valueList.toArray()));
		autoBox.setStrict(true);
		return box;
	}

	private class JTextFieldListener extends KeyAdapter {
		public void keyTyped(KeyEvent e) {  
			addBtn.setEnabled(true);
		} 
	}

	private class AddBtnListener implements ActionListener {


		@Override
		public void actionPerformed(ActionEvent e) {
			boolean succceeded = true;
			String topicText = topicBox.getSelectedItem().toString();
			String definitionText = definitionBox.getSelectedItem().toString();
			String entityText = answerField.getText();
			if (topicText != "" && definitionText != "" && entityText != "") {
				// send query to DB
				if (succceeded) {
					label.setIcon(new ImageIcon(AddDefView.class.getResource("/resources/check.png")));
					label.setText("<html><left>ADDED New Defintion: <br>Definition Text: " + definitionText + "<br> Answer: " + entityText +
							"<br>Topic: " + topicText + "</p></html>");
					mainPanel.add(label);
					
				}
				else  {
					label.setIcon(new ImageIcon(AddDefView.class.getResource("/resources/fail.png")));
					label.setText("FAILED TO ADD New Defintion: <br>Definition Text: " + definitionText + "<br> Answer: " + entityText +
							"<br>Topic: " + topicText + "</p></html>");
				}
				mainPanel.validate();
				mainPanel.repaint();
			}
		}
	}

	private class TopicBoxListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				String topicText = topicBox.getSelectedItem().toString(); // selected topic
				//query DB for categories relevant to selected topic
				//definitionList = getDefinitionsBtTopic(topicText);
				definitionBox.setEnabled(true);
				answerField.setEnabled(true);
			}
		}

	}

}
