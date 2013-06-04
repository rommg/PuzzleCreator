package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Font;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.JList;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.border.TitledBorder;
import javax.swing.UIManager;

import utils.Logger;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

import com.sun.org.apache.bcel.internal.generic.CASTORE;

final class AddHintsView extends JPanel {

	private List<String> literalFacts = null;

	private JPanel knowledgeDetailsPanel;
	private JPanel subjectSearchPanel;
	private JPanel predicatePanel;
	private JPanel objectPanel;

	private List<String> entityList;
	private List<String> predicateList; // merging of predicateSubjectList + predicateObjectList


	private JLabel lbl;

	static AddHintsView start() {
		return new AddHintsView();
	}
	/**
	 * Create the panel.
	 */
	@SuppressWarnings("unchecked")
	private AddHintsView() {
		setLayout(new GridLayout(5, 1, 0, 0));

		knowledgeDetailsPanel = new JPanel();
		knowledgeDetailsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		add(knowledgeDetailsPanel);

		getEntities(); //in another thread
		getPredicates(); // in another thread 

		subjectSearchPanel = new JPanel();
		subjectSearchPanel.setBorder(new TitledBorder("Knowledge Piece #1"));
		JComboBox<String> box1 = createAutoCompleteBox(entityList);
		box1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				predicatePanel.setVisible(true);
			}
		});
		subjectSearchPanel.add(box1);
		knowledgeDetailsPanel.add(subjectSearchPanel);


		predicatePanel = new JPanel();
		predicatePanel.setVisible(false);
		predicatePanel.setBorder(new TitledBorder("Knowledge Relation"));
		JComboBox<String> box2 = createAutoCompleteBox(predicateList);
		box2.addItemListener(new PredicatedItemListener());
		predicatePanel.add(box2);
		knowledgeDetailsPanel.add(predicatePanel);

		JPanel listPanel = new JPanel();
		listPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Created Hints", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(listPanel);
		listPanel.setLayout(new BorderLayout(0, 0));

		lbl = new JLabel();
		listPanel.add(lbl, BorderLayout.CENTER);

		JPanel emptyPanel = new JPanel();
		add(emptyPanel);

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

		initialize();

	}

	private void getEntities() {
		//queryDB for entites
		entityList = new ArrayList<String>();
		entityList.add("");
		entityList.add("baaaaaaaaaaaa");
		entityList.add("aghhhhhhhh");
	}

	private void getPredicates() {
		// query DB for predicates
		// tempPredicateList = 
		literalFacts = new ArrayList<String>();
		
		List<Map<String,String>> tempPredicateList = new ArrayList<Map<String,String>>();
		for (Map<String, String> row : tempPredicateList) {
			String subject = row.get("subject_str");
			String object = row.get("object_str");
			if ( object == null) {
				literalFacts.add(subject); // object_str is a string for a literal fact
			}
			predicateList.add(subject);
			predicateList.add(object);
		}

		predicateList = new ArrayList<String>();
		predicateList.add("");
		predicateList.add("Populated by ? people");
		literalFacts.add("Populated by ? people");
		predicateList.add("Created ?");
		predicateList.add("was created by ?");

		


	}
	private JComboBox<String> createAutoCompleteBox(List<String> valueList) {

		JComboBox<String> box = new JComboBox<String>();
		AutoCompleteSupport<Object> autoBox =  AutoCompleteSupport.install(box, GlazedLists.eventListOf(valueList.toArray()));
		autoBox.setStrict(true);
		return box;
	}

	private void initialize() {
		
	}

	private class PredicatedItemListener implements ItemListener {
		String predicateTxt = null;
		private JButton addBtn;
		@SuppressWarnings("unchecked")
		@Override
		public void itemStateChanged(ItemEvent e) {
			JComboBox<String> txtBox = null;

			if (e.getStateChange() == ItemEvent.SELECTED) {
				try {
					txtBox = (JComboBox<String>)e.getSource();
				}
				catch (ClassCastException exception) {
					Logger.writeErrorToLog("PredicatedItemListener.itemStateChanged : " + exception.getMessage());
				}
				predicateTxt = txtBox.getSelectedItem().toString(); 
				if (literalFacts.contains(predicateTxt)) { // need to draw special LiteralPanel (object is a literal)
					if (objectPanel != null)
						knowledgeDetailsPanel.remove(objectPanel);
					// create NumberLiteralBox
					if (predicateTxt.compareTo("Populated by ? people") == 0)
						objectPanel = new NumberPanel();					
					else { // all other literal boxes are dates
						objectPanel = new DatePanel();
					}
				}
				else { // draw regular AutoComplete (object is an entity)
					if (objectPanel != null)
						knowledgeDetailsPanel.remove(objectPanel);

					objectPanel = new JPanel();
					JComboBox<String> box = createAutoCompleteBox(entityList);
					objectPanel.setBorder(new TitledBorder("Knowledge Piece #2"));
					objectPanel.add(box);
				}


				knowledgeDetailsPanel.add(objectPanel);


				if (addBtn == null)  {
					addBtn = new JButton(new ImageIcon(AddHintsView.class.getResource("/resources/add.png")));
					addBtn.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							boolean sendSucceded = true;
							//Send to DB, check it doesnt already exist
							if (sendSucceded) {
								String subjectText = ((JComboBox<String>)subjectSearchPanel.getComponent(0)).getSelectedItem().toString();
								Component comp = objectPanel.getComponent(0);
								String objectText;
								if (comp instanceof JComboBox<?>) 
									objectText = ((JComboBox<String>)comp).getSelectedItem().toString();
								else 
									objectText = ((JTextField)comp).getText();

								addHintLbl(subjectText, predicateTxt, objectText);
							}
						}
					});
				}
				else {
					knowledgeDetailsPanel.remove(addBtn); // remove if already was added once
				}
				knowledgeDetailsPanel.add(addBtn);
				//	knowledgeDetailsPanel.validate();
				knowledgeDetailsPanel.getParent().validate();
				knowledgeDetailsPanel.getParent().repaint();
			}
		}
	}

	private abstract class LiteralPanel extends JPanel {
		LiteralPanel(String borderText) {
			setLayout(new BorderLayout());
			setBorder(new TitledBorder(borderText));
		}

		protected void addTextField(JTextField tf) {
			tf.addKeyListener(new KeyAdapter() { // ignore non numerical values
				public void keyTyped(KeyEvent e) {  
					char c = e.getKeyChar(); // Get the typed character  
					// Don't ignore backspace or delete  
					if (c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {  
						// If the key was not a number then discard it (this is a sloppy way to check)  
						if (!(c == '0' || c == '1' || c == '2' || c == '3' || c == '4' ||  
								c == '5' || c == '6' || c == '7' || c == '8' || c == '9')) {  
							e.consume();  // Ignore this key  
						}  
					}  
				} 
			});
			this.add(tf, BorderLayout.CENTER);
		}
	}

	/**
	 * a panel with a text box for dates (literal)
	 * @author yonatan
	 *
	 */
	private class DatePanel extends LiteralPanel {
		private final DateFormat df = new SimpleDateFormat("yyyy-mm-dd");

		public DatePanel() {
			super("Date");
			JTextField tf = new JFormattedTextField(df);	
			MaskFormatter dateMask = null;
			try {
				dateMask = new MaskFormatter("####-##-##");
				dateMask.install((JFormattedTextField)tf);
				tf = new JFormattedTextField(df);
			} catch (ParseException e) {
				Logger.writeErrorToLog("DatePanel.DatePanel(): " + e.getMessage());
			}
			finally {
				tf.setText(""); //clear
				super.addTextField(tf);
			}
		}
	}

	/**
	 * a panel with a text box for number (literal)
	 * @author yonatan
	 *
	 */
	private class NumberPanel extends LiteralPanel {

		NumberPanel() {
			super("Number");
			super.addTextField(new LimitedTextField(10));
		}

	}

	/**
	 * writes the hint label on the screen
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	private void addHintLbl(String subject, String predicate, String object) {
		if (subject != "" && predicate != "" && object != "")
				lbl.setText(subject + ": " + predicate.replaceAll("\\?", object.toString()));
	}
}



