package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.ItemSelectable;

import javax.swing.JApplet;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.ListCellRenderer;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import javax.swing.text.TabExpander;
import org.japura.gui.CheckComboBox;
import org.japura.gui.model.ListCheckModel;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

import utils.DBUtils;
import utils.Logger;

import javax.swing.JTabbedPane;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ManagementView extends JPanel {
	private int definitionCounter = 0;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField newTextField;
	private JComboBox<String> existingTextField;
	private Map<String,Integer> allTopics; // map of all topics
	private Map<String,Integer> allEntities; // Map of entity PROPER NAME - which is unique.
	private String[] allTopicNamesArray;
	private Map<String, Integer> definitions; // definitions for an entity as queries from DB
	private Map<String,Integer> allDefinitions = null;


	private static final String USER_UPDATES_TOPIC = "User Updates";
	private static final int ADD_ROWS_NUM = 1;
	private static final int MAX_NUM_DEFS = 10;

	private JPanel definitionPanel;
	private JPanel hintsPanel;
	private JTabbedPane tabbedPane;


	static JPanel start() {
		return new ManagementView();
	}
	/**
	 * Create the panel.
	 */
	private ManagementView() {

		initialize(); // get all DB knowledge

		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Choose Knowledge Fact", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel topBtnPanel = new JPanel();
		panel.add(topBtnPanel, BorderLayout.EAST);
		topBtnPanel.setLayout(new GridLayout(2, 1, 0, 5));

		final JButton btnAddNewFact = new JButton("");
		btnAddNewFact.setIcon(new ImageIcon(ManagementView.class.getResource("/resources/add_tiny.png")));
		btnAddNewFact.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String entityText = newTextField.getText(); 
				if (!entityText.isEmpty()) {
					buildEmptyDefinitionPanel();
					buildEmptyHintPanel();
				}
			}
		});
		topBtnPanel.add(btnAddNewFact);

		final JButton btnSearchFact = new JButton("");
		btnSearchFact.setEnabled(false);
		btnSearchFact.setIcon(new ImageIcon(ManagementView.class.getResource("/resources/search_tiny.png")));
		btnSearchFact.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String searchText = existingTextField.getSelectedItem().toString();

				if (searchText.isEmpty())
					return; // do nothing if no text entered


				Integer id = allEntities.get(searchText);

				if (id != null) { // found
					buildDefinitionPanel(id); 
					buildHintsPanel(id);
				}
			}
		});

		topBtnPanel.add(btnSearchFact);

		JPanel radioBtnPanel = new JPanel();
		panel.add(radioBtnPanel, BorderLayout.WEST);

		JRadioButton newCheckBox = new JRadioButton("New Knowledge Fact");
		newCheckBox.setSelected(true);
		newCheckBox.addItemListener(new ItemListener() {

			void setEnabled(boolean flag) {
				btnAddNewFact.setEnabled(flag);
				newTextField.setEnabled(flag);
				if (!flag) {
					tabbedPane.removeAll();
				}
			}
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					setEnabled(true);
				}
				else {
					setEnabled(false);
				}
			}
		});
		radioBtnPanel.setLayout(new GridLayout(2, 1, 0, 5));

		buttonGroup.add(newCheckBox);
		radioBtnPanel.add(newCheckBox);

		JRadioButton existingCheckBox = new JRadioButton("Existing Knowledge Fact");
		existingCheckBox.addItemListener(new ItemListener() {

			void setEnabled(boolean flag) {
				btnSearchFact.setEnabled(flag);
				existingTextField.setEnabled(flag);
				if (!flag) {
					tabbedPane.removeAll();
				}
			}

			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					setEnabled(true);
				}
				else { 
					setEnabled(false);
				}
			}
		});
		buttonGroup.add(existingCheckBox);
		radioBtnPanel.add(existingCheckBox);

		JPanel fieldPanel = new JPanel();
		panel.add(fieldPanel, BorderLayout.CENTER);
		fieldPanel.setLayout(new GridLayout(2, 1, 0, 5));

		newTextField = new JTextField();
		fieldPanel.add(newTextField);
		newTextField.setColumns(10);

		existingTextField = createAutoCompleteBox(allEntities.keySet(), "", true);
		existingTextField.setEnabled(false);
		fieldPanel.add(existingTextField);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.LIGHT_GRAY);
		add(tabbedPane, BorderLayout.CENTER);

		definitionPanel = new JPanel();
		hintsPanel = new JPanel();

		JPanel btnPanel = new JPanel();
		btnPanel.setAlignmentX(FlowLayout.CENTER);
		JButton btnBack = new JButton();
		btnBack.setFont(btnBack.getFont().deriveFont(15f));
		btnBack.setIcon(new ImageIcon(HallOfFameView.class.getResource("/resources/back.png")));

		btnBack.addActionListener(new BackButtonListener());
		btnPanel.add(btnBack);
		add(btnPanel, BorderLayout.SOUTH);
	}


	private void initialize() {

		allDefinitions = DBUtils.getAllDefinitions(); // get all definition strings in definitions table
		allTopics = DBUtils.getAllTopicIDsAndNames(); // get all pairs of topic: topic ID, topic name
		Object[] array = allTopics.keySet().toArray(); // topic array for JList combobox
		allTopicNamesArray = new String[array.length];

		for (int i = 0 ; i< array.length; i++) {
			allTopicNamesArray[i] = array[i].toString();
		}

		//get all entities for search from DB
		allEntities = DBUtils.getAllEntities();
	}

	private Map<String,Integer> getTopicsForDefinition(int entityID) {
		return DBUtils.getTopicsByDefinitionID(entityID);
	}

	private void getDefinitionsForEntity(int entityID) {
		definitions = DBUtils.getDefinitionsByEntityID(entityID);
		definitionCounter = definitions.size();
	}

	private void buildEmptyDefinitionPanel() {

		tabbedPane.remove(definitionPanel);

		definitionPanel = new JPanel();
		definitionPanel.setLayout(new GridLayout(MAX_NUM_DEFS, 1, 0, 10));

		for (int i = 0; i<MAX_NUM_DEFS; i++) {
			definitionPanel.add(new NewDefinitionLine());
		}
		definitionPanel.revalidate();
		tabbedPane.add("Definitions", definitionPanel);
	}

	private void buildDefinitionPanel(int entityID) {

		getDefinitionsForEntity(entityID);

		int row_num = definitions.size();

		tabbedPane.remove(definitionPanel);
		// refreshes panel
		definitionPanel = new JPanel();
		definitionPanel.setLayout(new GridLayout(MAX_NUM_DEFS, 1, 0, 10));

		for (String definition : definitions.keySet()) { // definition : MAP : DEFINITON STRING - > ID
			DefinitionLine line = new DefinitionLine(definitions.get(definition), definition);
			definitionPanel.add(line);
		}
		for (int i = 0; i<ADD_ROWS_NUM; i++) { // add new definition lines
			definitionPanel.add(new NewDefinitionLine());
		}

		// add padding lines, if needed
		for (int i = 0; i<MAX_NUM_DEFS - ADD_ROWS_NUM - row_num; i++) {
			definitionPanel.add(new JPanel());
		}

		definitionPanel.revalidate();
		tabbedPane.addTab("Definitions", null, definitionPanel,null);
		tabbedPane.revalidate();

		return;
	}

	private void buildHintsPanel(int entityID) {
		Map<Integer,List<String>> hintResults = DBUtils.getHintsByEntityID(entityID);
		List<HintTuple> hintTupleLst = new ArrayList<HintTuple>();

		for (int id : hintResults.keySet()) {
			hintTupleLst.add(new HintTuple(id, hintResults.get(id).get(0)));
		}


		int row_num = hintResults.size();

		tabbedPane.remove(hintsPanel);

		// refreshes panel
		hintsPanel = new JPanel();
		hintsPanel.setLayout(new GridLayout(MAX_NUM_DEFS, 1, 0, 10));

		for (HintTuple hint : hintTupleLst) { 
			HintResultLine line = new HintResultLine(hint);
			hintsPanel.add(line);
		}
		for (int i = 0; i<ADD_ROWS_NUM; i++) { // add new definition lines
			hintsPanel.add(new NewHintLine());
		}

		// add padding lines, if needed
		for (int i = 0; i<MAX_NUM_DEFS - ADD_ROWS_NUM - row_num; i++) {
			hintsPanel.add(new JPanel());
		}

		hintsPanel.revalidate();
		tabbedPane.addTab("Hints", null, hintsPanel,null);
		tabbedPane.revalidate();

		return;
	}

	private void buildEmptyHintPanel() {
		tabbedPane.remove(hintsPanel);

		hintsPanel = new JPanel();
		hintsPanel.setLayout(new GridLayout(MAX_NUM_DEFS, 1, 0, 10));

		for (int i = 0; i<MAX_NUM_DEFS; i++) {
			hintsPanel.add(new NewHintLine());
		}
		hintsPanel.revalidate();
		tabbedPane.add("Hints", hintsPanel);
	}


	/**
	 * one line in definitions tab: topic(s),definition,edit/delete buttons
	 * @author yonatan
	 *
	 */
	private class DefinitionLine extends JPanel {
		protected CheckComboBox topicBox;
		//protected JComboBox<String> definitionBox;
		protected JTextField definitionBox;
		protected JButton saveBtn;
		protected JButton deleteBtn;

		protected JPanel btnPanel;

		public DefinitionLine(int definitionID, String definition) {
			setLayout(new BorderLayout());

			Map<String,Integer> topics = getTopicsForDefinition(definitionID);
			topicBox = new TopicsCheckComboBox(allTopics.keySet(), topics.keySet(), true);
			super.add(topicBox, BorderLayout.WEST);
			definitionBox = new LimitedTextField(30);
			definitionBox.setText(definition);
			definitionBox.setEditable(false);

			super.add(definitionBox, BorderLayout.CENTER);

			btnPanel = new JPanel();
			btnPanel.setLayout(new GridLayout(1,2));
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
					if (definitionCounter - 1 >= 1){
						//						if (!topicBox.getSelectedItem().toString().isEmpty() &&
						//								!definitionBox.getSelectedItem().toString().isEmpty()) {
						//							//delete row from DB
						//						}
					}
					else { // popup error message
						JOptionPane.showMessageDialog(MainView.getView().getFrame(),
								"Knowledge Fact must have at least one definition.");
					}
				}
			});

			//btnPanel.add(saveBtn);
			btnPanel.add(deleteBtn);
			add(btnPanel, BorderLayout.EAST);
		}
	}

	private class HintResultLine extends JPanel {

		protected JButton saveBtn;
		protected JButton deleteBtn;
		protected JPanel btnPanel;
		private HintTuple hint;


		public HintResultLine(HintTuple hint) {
			setLayout(new BorderLayout());
			this.hint = hint;

			JTextField field = new JTextField(hint.getText());
			field.setEditable(false);
//			field.addKeyListener(new KeyAdapter() {
//
//				@Override
//				public void keyTyped(KeyEvent arg0) {
//					saveBtn.setEnabled(true);					
//				}
//			});

			add(field, BorderLayout.CENTER);

			btnPanel = new JPanel();
			btnPanel.setLayout(new BorderLayout());
			//			btnPanel.setLayout(new GridLayout(1,2));
			//			saveBtn = new JButton(new ImageIcon(ManagementView.class.getResource("/resources/save_small.png")));
			//			saveBtn.addActionListener(new ActionListener() {
			//
			//				@Override
			//				public void actionPerformed(ActionEvent arg0) {
			//
			//				}
			//			});
			deleteBtn = new JButton(new ImageIcon(ManagementView.class.getResource("/resources/delete_small.png")));		
			deleteBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					//do delete
					btnPanel.getParent().remove(HintResultLine.this);
					btnPanel.getParent().revalidate();
				}
			});

			//			btnPanel.add(saveBtn);
			btnPanel.add(deleteBtn, BorderLayout.CENTER);
			add(btnPanel, BorderLayout.EAST);
		}
	}

	private List<Integer> getUserSelectedTopics(Object[] selected) {
		List<Integer>  lst = new ArrayList<Integer>();
		for (Object object : selected) {
			lst.add((Integer)object);
		}
		return lst;
	}

	private class NewDefinitionLine extends JPanel {

		JComboBox<String> field;
		CheckComboBox topicBox;

		NewDefinitionLine() {

			setLayout(new BorderLayout());

			topicBox = new TopicsCheckComboBox(allTopics.keySet(), Collections.<String>emptySet() , true);
			add(topicBox, BorderLayout.WEST);

			field = createAutoCompleteBox(allDefinitions.keySet(), "", false);
			field.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent itemEvent) { 
					NewDefinitionLine.this.remove(topicBox);
					ItemSelectable is = itemEvent.getItemSelectable();
					String text =  selectedString(is);
					if (allDefinitions.containsKey(text)) { // existing definition in definitions table
						// show the topics linked to this definition, cannot be changed
						Map<String,Integer> chosenDefinitionTopics = getTopicsForDefinition(allDefinitions.get(text)); 
						topicBox = new TopicsCheckComboBox(allTopics.keySet(), chosenDefinitionTopics.keySet(), true);
					}
					else {
						// user may choose topics himself, beacuse this is a new definition
						topicBox = new TopicsCheckComboBox(allTopics.keySet(), Collections.singleton(USER_UPDATES_TOPIC), false);
					}
					NewDefinitionLine.this.add(topicBox,BorderLayout.WEST);
					NewDefinitionLine.this.revalidate();

				}

				private String selectedString(ItemSelectable is) {
					Object selected[] = is.getSelectedObjects();
					return ((selected.length == 0) ? "null" : (String) selected[0]);
				}
			});

			add(field, BorderLayout.CENTER);

			JButton saveBtn = new JButton(new ImageIcon(ManagementView.class.getResource("/resources/add_small.png")));
			saveBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (topicBox.getModel().getCheckeds().size() < 1) 
						JOptionPane.showMessageDialog(MainView.getView().getFrame(),"<html><center>You must choose at least one topic.</html>" );
					if	(!field.getSelectedItem().toString().isEmpty() && 
							(isValidString(field.getSelectedItem().toString()))) {
						//add DB procedure
					}
					else  { // show error message
						JOptionPane.showMessageDialog(MainView.getView().getFrame(),
								"<html><center>Invalid Text.</html>");
					}

				}
			});
			add(saveBtn, BorderLayout.EAST);
		}

	}

	private class NewHintLine extends JPanel {


		private JButton saveBtn;
		private JTextField field;


		NewHintLine() {
			setLayout(new BorderLayout());

			field = new JTextField();
			add(field, BorderLayout.CENTER);
			field.addKeyListener(new KeyAdapter() {

				@Override
				public void keyTyped(KeyEvent arg0) {
					saveBtn.setEnabled(true);					
				}
			});

			saveBtn = new JButton(new ImageIcon(ManagementView.class.getResource("/resources/add_small.png")));
			saveBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (!field.getText().isEmpty() && isValidString(field.getText())) {
						//call DB add procedure
					}
					else  { // show error message
						JOptionPane.showMessageDialog(MainView.getView().getFrame(),
								"Invalid Text.");
					}
				}
			});
			add(saveBtn, BorderLayout.EAST);
		}
	}


	/**
	 * because hint text may not be unique, keep the ID and String together
	 * @author yonatan
	 *
	 */
	private class HintTuple {
		private int id;
		private String text;

		public HintTuple(int id, String text) {
			this.id = id;
			this.text = text;
		}

		public int getId() {
			return id;
		}

		public String getText() {
			return text;
		}
	}


	private class BackButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			MainView.getView().showWelcomeView();
		}
	}


	/**
	 * AutoComplete for SearchBox
	 * @param valueList
	 * @param firstvalue
	 * @param strict
	 * @return
	 */
	private JComboBox<String> createAutoCompleteBox(Set<String> valueList, String firstvalue, boolean strict) {

		JComboBox<String> box = new JComboBox<String>();
		AutoCompleteSupport<Object> autoBox =  AutoCompleteSupport.install(box, GlazedLists.eventListOf(valueList.toArray()));
		autoBox.setFirstItem(firstvalue);
		autoBox.setStrict(strict);
		return box;
	}


	private boolean isValidString(String string) {
		return string.matches("[a-zA-Z0-9 \\(\\)]+");
	}

	class TopicsCheckComboBox extends CheckComboBox {
		private boolean lock;
		private Set<String> allTopics;
		private Set<String> topics;

		public TopicsCheckComboBox(Set<String> allTopics, Set<String> topics, boolean lock) {
			this.lock = lock;
			this.allTopics = allTopics;
			this.topics = topics;

			super.setTextFor(CheckComboBox.NONE, "* no items selected *"); 
			super.setTextFor(CheckComboBox.MULTIPLE, "* multiple items *"); 
			super.setTextFor(CheckComboBox.ALL, "* all selected *"); 

			addTopicCheckBoxes();

		}

		private void addTopicCheckBoxes() {
			ListCheckModel model = this.getModel(); 
			for (String topic : allTopics) { 
				model.addElement(topic);
				if (topics.contains(topic)) {
					model.addCheck(topic);
					if (topic.compareTo(USER_UPDATES_TOPIC) == 0) { // if USER_UPDATES box should be checked, make it locked
						model.addLock(topic);
					}
				}
			}
			if (lock)
				model.lockAll();


		}
	}

}