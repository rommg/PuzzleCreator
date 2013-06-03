package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class AutoSuggestPanel extends JPanel{
	private final JTextField tf;
	private final JComboBox<String> combo = new JComboBox<String>();
	private final Vector<String> v = new Vector<String>();
	
	public AutoSuggestPanel(String borderText) {
		super(new BorderLayout());
		combo.setEditable(true);
		tf = (JTextField) combo.getEditor().getEditorComponent();
		tf.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				EventQueue.invokeLater(new Runnable() { // seperate thread because gets Dictionary
					public void run() {
						String text = tf.getText();
						if(text.length()==0) {
							combo.hidePopup();
							setModel(new DefaultComboBoxModel<String>(v), "");
						}else{
							DefaultComboBoxModel<String> m = getSuggestedModel(v, text);
							if(m.getSize()==0 || hide_flag) {
								combo.hidePopup();
								hide_flag = false;
							}else{
								setModel(m, text);
								combo.showPopup();
							}
						}
					}
				});
			}
			public void keyPressed(KeyEvent e) {
				String text = tf.getText();
				int code = e.getKeyCode();
				if(code==KeyEvent.VK_ENTER) {
					if(!v.contains(text)) {
						v.addElement(text);
						Collections.sort(v);
						setModel(getSuggestedModel(v, text), text);
					}
					hide_flag = true; 
				}else if(code==KeyEvent.VK_ESCAPE) {
					hide_flag = true; 
				}else if(code==KeyEvent.VK_RIGHT) {
					for(int i=0;i<v.size();i++) {
						String str = v.elementAt(i);
						if(str.startsWith(text)) {
							combo.setSelectedIndex(-1);
							tf.setText(str);
							return;
						}
					}
				}
			}
		});
		
		String[] countries = {"Afghanistan", "Albania", "Algeria", "Andorra", "Angola","Argentina"
				,"Armenia","Austria","Bahamas","Bahrain", "Bangladesh","Barbados", "Belarus","Belgium",
				"Benin","Bhutan","Bolivia","Bosnia & Herzegovina","Botswana","Brazil","Bulgaria",
				"Burkina Faso","Burma","Burundi","Cambodia","Cameroon","Canada", "China","Colombia",
				"Comoros","Congo","Croatia","Cuba","Cyprus","Czech Republic","Denmark", "Georgia",
				"Germany","Ghana","Great Britain","Greece","Hungary","Holland","India","Iran","Iraq",
				"Italy","Somalia", "Spain", "Sri Lanka", "Sudan","Suriname", "Swaziland","Sweden",
				"Switzerland", "Syria","Uganda","Ukraine","United Arab Emirates","United Kingdom",
				"United States","Uruguay","Uzbekistan","Vanuatu","Venezuela","Vietnam",
				"Yemen","Zaire","Zambia","Zimbabwe"};
		
		// query the DB for all entitynames
		for(int i=0;i<countries.length;i++){
			v.addElement(countries[i]);
		}
		setModel(new DefaultComboBoxModel<String>(v), "");
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder(borderText));
		p.add(combo, BorderLayout.NORTH);
		add(p);
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		setPreferredSize(new Dimension(300, 150));
	}
	private boolean hide_flag = false;
	private void setModel(DefaultComboBoxModel<String> mdl, String str) {
		combo.setModel(mdl);
		combo.setSelectedIndex(-1);
		tf.setText(str);
	}
	/**
	 * iterate through dictionary and look for words fitting to prefix in suggestbox
	 * @param list
	 * @param text
	 * @return
	 */
	private static DefaultComboBoxModel<String> getSuggestedModel(java.util.List<String> list, String text) {
		DefaultComboBoxModel<String> m = new DefaultComboBoxModel<String>();
		for(String s: list) {
			if(s.toLowerCase().startsWith(text.toLowerCase())) m.addElement(s);
		}
		return m;
	}
}