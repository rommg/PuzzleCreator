package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

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
	
	static AddDefView start() {
		return new AddDefView();
	}
	
	/**
	 * Create the panel.
	 */
	private  AddDefView() {
		initialize();

		setLayout(new BorderLayout(0, 0));
		
		JPanel comboboxPanel = new JPanel();
		add(comboboxPanel, BorderLayout.NORTH);
		comboboxPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JComboBox<String> topicBox = createAutoCompleteBox(topicList);
		JPanel topicBoxPanel = new JPanel();
		topicBoxPanel.setBorder(new TitledBorder("Topics"));
		topicBoxPanel.add(topicBox);
		comboboxPanel.add(topicBoxPanel);
		
		JComboBox<String> definitionBox = createAutoCompleteBox(definitionList);
		definitionBox.setEditable(false);
		JPanel definitionBoxPanel = new JPanel();
		definitionBoxPanel.setBorder(new TitledBorder("Definition"));
		definitionBoxPanel.add(definitionBox);
		comboboxPanel.add(definitionBoxPanel);
		
		JTextField field = new LimitedTextField(20);
		JPanel textPanel = new JPanel();
		textPanel.setBorder(new TitledBorder("Answer"));
		textPanel.add(field);
		comboboxPanel.add(textPanel);
		
		addBtn = new JButton(new ImageIcon(AddDefView.class.getResource("/resources/add.png")));
		addBtn.setEnabled(false);
		comboboxPanel.add(addBtn);
		
		JPanel mainPanel = new JPanel();
		add(mainPanel, BorderLayout.CENTER);
		
		JPanel btnPanel = new JPanel();
		add(btnPanel, BorderLayout.SOUTH);
		
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
	
}
