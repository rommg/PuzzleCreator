package ui;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import ui.CrosswordView.HintCounterLabel;

/**
 * A Hint popup menu for a JDefinitionLabel
 * @author yonatan
 *
 */
@SuppressWarnings("serial")
final class HintPopupMenu extends JPopupMenu {
	private String[] hintArray;
	private JDefinitionLabel label;
	private HintCounterLabel hintCounterLabel;
	int maxLength;


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
		
		maxLength = getMaxLength();

		this.label = label;
		this.hintCounterLabel = hintCounterLabel;
		buildPopupSubMenus();
	}

	/**
	 * used by a view to make hint menu enabled/disabled
	 * @param enabled
	 */
	void setHintPopupMenuEnable(boolean enable) {
		//Utils.enableComponents(this, enable);
		setEnabled(enable);
		setVisible(enable);
	}

	private void buildPopupSubMenus() {

		JMenuItem menu = new JMenuItem(label.getText());
		menu.setIcon(new ImageIcon(getClass().getClassLoader().getResource("resources/tip.png")));
		add(menu);
		
		for (int i=0; i<hintArray.length && i<10; i++) {
			addSeparator();
			add(new HintItem(hintArray[i]));
		}
	}

	private int getMaxLength() {
		int max = -1;
		int length;
		for (int i=0; i<hintArray.length; i++) {
			if (( length = hintArray[i].length()) > max)
				max = length;
		}
		return max;
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
			JButton imageBtn = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/locked.png")));

			// set button behavior
			imageBtn.addActionListener(new LockButtonListener(this.lbl));

			add(imageBtn, BorderLayout.EAST);
		}

		private String createEchoString(int length) {
			StringBuilder str = new StringBuilder();
			for (int i = 0; i<maxLength; i++ ) {
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
					btn.setIcon(new ImageIcon(getClass().getClassLoader().getResource("resources/unlocked.png")));
					btn.setEnabled(false);
					HintPopupMenu.this.hintCounterLabel.updateCounter();
					this.lbl.setHorizontalTextPosition(SwingConstants.LEFT);
					//this.lbl.setText("<html><p><left>&nbsp;&nbsp;&nbsp;&nbsp;" + HintItem.this.hintText);
					this.lbl.setText("<html><p>" + HintItem.this.hintText + "</p></html>");


				}
			}
		}
	}
}
