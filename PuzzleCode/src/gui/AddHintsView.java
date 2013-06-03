package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.JList;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.TitledBorder;
import javax.swing.UIManager;

class AddHintsView extends JPanel {
	static AddHintsView start() {
		return new AddHintsView();
	}
	/**
	 * Create the panel.
	 */
	private AddHintsView() {
		setLayout(new GridLayout(5, 1, 0, 0));
		
		JPanel knowledgeDetailsPanel = new JPanel();
		add(knowledgeDetailsPanel);
		knowledgeDetailsPanel.setLayout(new BoxLayout(knowledgeDetailsPanel, BoxLayout.X_AXIS));
		
		JPanel subjectSearchPanel = new AutoSuggestPanel("Knowledge #1");
		knowledgeDetailsPanel.add(subjectSearchPanel);
		FlowLayout fl_subjectSearchPanel = new FlowLayout(FlowLayout.LEFT, 5, 5);
		subjectSearchPanel.setLayout(fl_subjectSearchPanel);
		
		JPanel predicatePanel = new AutoSuggestPanel("Knowledge Relation");
		knowledgeDetailsPanel.add(predicatePanel);
		predicatePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JPanel panel = new AutoSuggestPanel("Knowledge #2");
		panel.isVisible();
		knowledgeDetailsPanel.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Created Hints", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JList list = new JList();
		panel_1.add(list, BorderLayout.CENTER);
		
		JPanel panel_2 = new JPanel();
		add(panel_2);
		
		JPanel addHintPanel = new JPanel();
		add(addHintPanel);
		
		JButton btnBack = new JButton(new ImageIcon(AddHintsView.class.getResource("/resources/back.png")));
		btnBack.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MainView.view.showWelcomeView();
			}
		});
		addHintPanel.add(btnBack);
		
		JButton btnClear = new JButton(new ImageIcon(AddHintsView.class.getResource("/resources/cancel.png")));
		addHintPanel.add(btnClear);
		
		JButton btnNewButton = new JButton("");
		addHintPanel.add(btnNewButton);
		btnNewButton.setIcon(new ImageIcon(AddHintsView.class.getResource("/resources/add_big.png")));
	
		initialize();

	}
	
	private void initialize() {
		
	}

}
