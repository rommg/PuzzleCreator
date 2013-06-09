package gui;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;

import sun.applet.Main;
import utils.DBUtils;

import javax.swing.border.EtchedBorder;

public class HallOfFameView extends JPanel {

	private JPanel tablePanel;

	static HallOfFameView start() {
		return new HallOfFameView();
	}
	/**
	 * Create the panel.
	 */
	private HallOfFameView() {
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.NORTH);

		JLabel lblHallOfFame = new JLabel("Hall Of Fame");
		lblHallOfFame.setFont(new Font("Tekton Pro", Font.PLAIN, 20));
		panel_1.add(lblHallOfFame);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		JButton btnBack = new JButton();
		btnBack.setFont(btnBack.getFont().deriveFont(15f));
		btnBack.setIcon(new ImageIcon(HallOfFameView.class.getResource("/resources/back.png")));

		btnBack.addActionListener(new BackButtonListener());
		panel.add(btnBack);

		JPanel gridPanel = new JPanel();
		add(gridPanel, BorderLayout.CENTER);
		GridBagLayout gbl_gridPanel = new GridBagLayout();
		gbl_gridPanel.columnWidths = new int[]{112, 112, 112, 0};
		gbl_gridPanel.rowHeights = new int[]{267, 0};
		gbl_gridPanel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_gridPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		gridPanel.setLayout(gbl_gridPanel);

		tablePanel = new JPanel();
		tablePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagConstraints gbc_tablePanel = new GridBagConstraints();
		gbc_tablePanel.gridwidth = 3;
		gbc_tablePanel.fill = GridBagConstraints.BOTH;
		gbc_tablePanel.insets = new Insets(0, 0, 0, 5);
		gbc_tablePanel.gridx = 0;
		gbc_tablePanel.gridy = 0;
		gridPanel.add(tablePanel, gbc_tablePanel);
		tablePanel.setLayout(new GridLayout(0, 3, 0, 0));

		populateTablePanel();
	}

	private void populateTablePanel() {

		Border border = LineBorder.createGrayLineBorder();

		String[][] results = getBestScores();

		if (results == null) {
			return;
		}

		for (int i = 0; i<10; i++ ) {
			JLabel rank = new JLabel("#" + (i+1), SwingConstants.CENTER);
			rank.setBorder(border);
			rank.setOpaque(true);
			rank.setBackground(Color.PINK);

			JLabel nameLabel = new JLabel();
			nameLabel.setBorder(border);

			JLabel scoreLabel = new JLabel();
			scoreLabel.setBorder(border);
			
			if (i < results.length) { // still more scores
				nameLabel.setText(results[i][0]);
				nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
				scoreLabel.setText(results[i][1]);
				scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

			}

			tablePanel.add(rank);
			tablePanel.add(nameLabel);
			tablePanel.add(scoreLabel);

		}
	}


	/**
	 * query DB for best names and scores, descending by score
	 * @return
	 */
	private String[][] getBestScores() {
		return DBUtils.getTenBestScores();
	}

	private class BackButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			MainView.getView().showWelcomeView();
		}
	}

}
