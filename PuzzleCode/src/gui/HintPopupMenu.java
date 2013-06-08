package gui;

import gui.CrosswordView.HintCounterLabel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;

/**
 * A Hint popup menu for a JDefinitionLabel
 * @author yonatan
 *
 */
final class HintPopupMenu extends JPopupMenu {
	private String[] hintArray;
	JDefinitionLabel label;
	HintCounterLabel hintCounterLabel;

	/**
	 * 
	 * @param label
	 * @param entityID
	 * @param hintCounterLabel - the counter to update wheneve a hint is used
	 */ 
	HintPopupMenu(JDefinitionLabel label, int entityID, HintCounterLabel hintCounterLabel) {
		List<String> hintLst = label.getDef().getHints();
		if (hintLst == null) {
			hintArray = new String[0];
		}
		else {
			hintArray = new String[hintLst.size()];
			int i = 0;

			for (String hintText : hintLst) {
				hintArray[i++] = hintText;
			}
		}

		this.label = label;
		this.hintCounterLabel = hintCounterLabel;
		buildPopupSubMenus();
	}
	//
	//	public int getUsedHintsCounter() {
	//		return usedHintsCounter;
	//	}

	private void buildPopupSubMenus() {


		Arrays.sort(hintArray); // sort before replace with entity name

		//		// add the hints to the popup menu
		//		for (int i=0; i<hintArray.length; i++){ // sorted by predicate
		//			add(new HintPopupSubmenu("Hint #" + (i+1), hintArray[i].getHintText())); 
		//		}

		JMenuItem item = new JMenuItem(label.getText());
		item.setIcon(new ImageIcon(HintPopupMenu.class.getResource("/resources/tip.png")));
		add(item);

		for (int i=0; i<hintArray.length; i++) {
			addSeparator();
			add(new HintItem(hintArray[i]));
		}
	}


	private class HintItem extends JPanel {
		private final String hintText;
		private JLabel lbl;
		private HintItem(String hintText) {

			super();

			this.hintText = hintText; // save the hint text for later
			this.setLayout(new BorderLayout());
			lbl = new JLabel("<html><p><left>&nbsp;&nbsp;&nbsp;&nbsp;" + createEchoString(hintText.length()) + "</p></html>");
			add(lbl, BorderLayout.CENTER);
			JButton imageBtn = new JButton(new ImageIcon(HintPopupMenu.class.getResource("/resources/locked.png")));

			// set button behavior
			imageBtn.addActionListener(new LockButtonListener(this.lbl));

			add(imageBtn, BorderLayout.EAST);
		}

		private String createEchoString(int length) {
			StringBuilder str = new StringBuilder();
			for (int i = 0; i<length; i++ ) {
				str.append('*');
			}
			return str.toString();
		}

		private class LockButtonListener implements ActionListener {
			boolean isLocked = true;
			JLabel lbl;
			private LockButtonListener(JLabel lbl) {
				this.lbl = lbl;
			}
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isLocked) {
					JButton btn = (JButton) e.getSource();
					btn.setIcon(new ImageIcon(HintPopupMenu.class.getResource("/resources/unlocked.png")));
					btn.setEnabled(false);
					HintPopupMenu.this.hintCounterLabel.updateCounter();
					this.lbl.setText("<html><p><left>&nbsp;&nbsp;&nbsp;&nbsp;" + HintItem.this.hintText);

				}
			}
		}
	}
}
