package gui;

import gui.MainController.BtnListener;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.ListCellRenderer;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;
import javax.swing.JScrollPane;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

import utils.DBUtils;

public class ManagementView extends JPanel {
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField newTextField;
	private JTextField existingTextField;
	private Map<String,Integer> topics;
	private Map<String,Integer> allTopics;
	private String[] allTopicsArray;
	private Map<String, Integer> definitions;
	private int definitionCounter = 0;
	private Set<String> allDefinitions = null;


	private static final int ADD_ROWS_NUM = 1;
	private JPanel definitionPanel;


	static JPanel start() {
		return new ManagementView();
	}
	/**
	 * Create the panel.
	 */
	private ManagementView() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Select Knowledge Fact", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel topBtnPanel = new JPanel();
		panel.add(topBtnPanel, BorderLayout.EAST);
		topBtnPanel.setLayout(new GridLayout(2, 0, 1, 0));

		JButton btnNewButton = new JButton("");
		btnNewButton.setIcon(new ImageIcon(ManagementView.class.getResource("/resources/add_tiny.png")));
		topBtnPanel.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("");
		btnNewButton_1.setIcon(new ImageIcon(ManagementView.class.getResource("/resources/search_tiny.png")));
		topBtnPanel.add(btnNewButton_1);

		JPanel radioBtnPanel = new JPanel();
		panel.add(radioBtnPanel, BorderLayout.WEST);
		radioBtnPanel.setLayout(new BoxLayout(radioBtnPanel, BoxLayout.Y_AXIS));

		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("New Knowledge Fact");
		buttonGroup.add(rdbtnNewRadioButton_1);
		radioBtnPanel.add(rdbtnNewRadioButton_1);

		JRadioButton rdbtnNewRadioButton = new JRadioButton("Existing Knowledge Fact");
		buttonGroup.add(rdbtnNewRadioButton);
		radioBtnPanel.add(rdbtnNewRadioButton);

		JPanel fieldPanel = new JPanel();
		panel.add(fieldPanel, BorderLayout.CENTER);
		fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));

		newTextField = new JTextField();
		fieldPanel.add(newTextField);
		newTextField.setColumns(10);

		existingTextField = new JTextField();
		fieldPanel.add(existingTextField);
		existingTextField.setColumns(10);

		JPanel detailsPanel = new JPanel();
		add(detailsPanel, BorderLayout.CENTER);
		detailsPanel.setLayout(new GridLayout(2, 0, 1, 0));

		definitionPanel = new JPanel();
		definitionPanel.setBorder(new TitledBorder(null, "Definitions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		detailsPanel.add(definitionPanel);
		definitionPanel.setLayout(new BorderLayout(0, 0));

		JPanel hintPanel = new JPanel();
		hintPanel.setBorder(new TitledBorder(null, "Hints", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		detailsPanel.add(hintPanel);

	}

	private void initialize() {
		allDefinitions = DBUtils.getAllDefinitions().keySet();
		allTopics = DBUtils.getAllTopicIDsAndNames();
		allTopicsArray = (String[]) topics.keySet().toArray(); // topic array for JList combobox
	}

	private void getTopics() {
		topics = DBUtils.getTopicByDefinitionIDs(new ArrayList<Integer>(definitions.values()));
	}

	private void getDefinitionsForEntity(String entityName) {
		//definitions = DBUtils.getDefinitionsByEntityName(entityName); // SALEET need to create Map<Entity_id,ProperEntityName> can then send this  ID to entity_definitions
		definitionCounter = definitions.size();
	}

	private void buildDefinitionPanel(String entityName) {
		int row_num = definitions.size();

		definitionPanel.setLayout(new GridLayout(row_num + ADD_ROWS_NUM, 1, 0, 5));

		for (String definition : definitions.keySet()) { // definition : MAP : DEFINITON STRING - > ID
			ResultLine line = new ResultLine(definition);
			definitionPanel.add(line);
		}

		definitionPanel.add(new NewDefinitionResultLine());

		return  ;

	}

	private void buildHintsPanel(String entityName) {

	}

	private JComboBox<String> createAutoCompleteBox(Set<String> valueList, String firstvalue, boolean strict) {

		JComboBox<String> box = new JComboBox<String>();
		AutoCompleteSupport<Object> autoBox =  AutoCompleteSupport.install(box, GlazedLists.eventListOf(valueList.toArray()));
		autoBox.setStrict(strict);
		return box;
	}

	private class ResultLine extends JPanel {
		protected JComboBox<String> topicBox;
		protected JComboBox<String> definitionBox;
		protected JButton saveBtn;
		protected JButton deleteBtn;

		public JComboBox<String> getTopic() {
			return topicBox;
		}

		public JComboBox<String> getDefinition() {
			return definitionBox;
		}

		public JButton getLockBtn() {
			return saveBtn;
		}

		public JButton getDeleteBtn() {
			return deleteBtn;
		}

		public JButton getAddBtn() {
			return addBtn;
		}

		private JButton addBtn;
		protected JPanel btnPanel;

		public ResultLine(String definition) {
			setLayout(new BorderLayout());

			topicBox = new JComboBox<String>();
			topicBox.setRenderer(new TopicComboboxRenderer(allTopicsArray));
			topicBox.setEnabled(false);
			super.add(topicBox, BorderLayout.WEST);
			definitionBox = createAutoCompleteBox(definitions.keySet(), definition, true);
			super.add(definitionBox, BorderLayout.EAST);

			btnPanel = new JPanel();
			saveBtn = new JButton(new ImageIcon(ManagementView.class.getResource("/resources/save_small.png")));
			saveBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					topicBox.setEnabled(true);
					definitionBox.setEnabled(true);
				}
			});
			deleteBtn = new JButton(new ImageIcon(ManagementView.class.getResource("/resources/delete_small.png")));		
			deleteBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (definitionCounter - 1 < 1){
						if (topicBox.getSelectedItem().toString() != "" &&
								definitionBox.getSelectedItem().toString() != "") {
							//delete row from DB
						}
					}
					else { // popup error message

					}
				}
			});
			btnPanel.add(saveBtn);
			btnPanel.add(deleteBtn);
			add(btnPanel, BorderLayout.EAST);
		}
	}

	private class NewDefinitionResultLine extends JPanel {

		JTextField field;
		JComboBox<String> topicField;

		NewDefinitionResultLine() {

			setLayout(new BorderLayout());
			
			topicField = createAutoCompleteBox(topics.keySet(), "", false);
			add(topicField, BorderLayout.WEST);
			field = new LimitedTextField(20);
			add(field, BorderLayout.CENTER);

			JButton saveBtn = new JButton(new ImageIcon(ManagementView.class.getResource("/resources/delete_small.png")));
			saveBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if ((topicField.getSelectedItem().toString()) != "" && (field.getText() != "")) {
						//call DB Save procedure
					}
				}
			});
			add(saveBtn, BorderLayout.EAST);
		}

	}

	public class TopicComboboxRenderer implements ListCellRenderer {

		private String[] items;
		private boolean[] selected;

		public TopicComboboxRenderer(String[] items){
			this.items = items;
			this.selected = new boolean[items.length];
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean hasFocus) {
			JCheckBox box = new JCheckBox(allTopicsArray[index]); // Create here a JCheckBox
			if (topics.keySet().contains(allTopicsArray[index])) {
				setSelected(index, true);
			}
			list.add(box);

			return box;
		}

		public void setSelected(int i, boolean flag)
		{
			this.selected[i] = flag;
		}


	}
}