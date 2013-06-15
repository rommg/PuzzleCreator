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
import java.sql.SQLException;
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

	private boolean initialize() {
		setLayout(new BorderLayout(0, 0));

		JPanel topPanel = new JPanel();
		add(topPanel, BorderLayout.NORTH);

		JLabel lblHallOfFame = new JLabel("Hall Of Fame");
		lblHallOfFame.setFont(new Font("Stencil", Font.PLAIN, 25));
		topPanel.add(lblHallOfFame);

		JPanel btnPanel = new JPanel();
		add(btnPanel, BorderLayout.SOUTH);

		JButton btnBack = new JButton();
		btnBack.setFont(btnBack.getFont().deriveFont(15f));
		btnBack.setIcon(new ImageIcon(getClass().getResource("../resources/back.png")));

		btnBack.addActionListener(new BackButtonListener());
		btnPanel.add(btnBack);

		tablePanel = new JPanel();
		add(tablePanel, BorderLayout.CENTER);
		tablePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		tablePanel.setLayout(new GridLayout(0, 4, 0, 0));

		if (!populateTablePanel()) {
			Utils.showErrorMessage("Oops! There was a DB error. Scores will not be shown." );
			return false;
		}
		return true;
	}

	private boolean populateTablePanel() {

		Border border = LineBorder.createGrayLineBorder();

		// column name labels
		JLabel rankCol = new JLabel("RANK");
		rankCol.setFont(new Font("Stencil", Font.PLAIN, 19));
		rankCol.setHorizontalAlignment(SwingConstants.CENTER);
		tablePanel.add(rankCol);

		JLabel nameCol = new JLabel("NAME");
		nameCol.setFont(new Font("Stencil", Font.PLAIN, 19));
		nameCol.setHorizontalAlignment(SwingConstants.CENTER);
		tablePanel.add(nameCol);

		JLabel scoreCol = new JLabel("SCORE");
		scoreCol.setFont(new Font("Stencil", Font.PLAIN, 19));
		scoreCol.setHorizontalAlignment(SwingConstants.CENTER);
		tablePanel.add(scoreCol);

		JLabel dateCol = new JLabel("Date");
		dateCol.setFont(new Font("Stencil", Font.PLAIN, 19));
		dateCol.setHorizontalAlignment(SwingConstants.CENTER);
		tablePanel.add(dateCol);

		String[][] results = null;
		try { 
			results = getBestScores();
		}
		catch (SQLException exception) {
			return false;
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

			JLabel dateLabel = new JLabel();
			dateLabel.setBorder(border);

			if (i < results.length) { // still more scores
				nameLabel.setText(results[i][0]);
				nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
				scoreLabel.setText(results[i][1]);
				scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
				dateLabel.setText(results[i][2]);
				dateLabel.setHorizontalAlignment(SwingConstants.CENTER);

			}

			tablePanel.add(rank);
			tablePanel.add(nameLabel);
			tablePanel.add(scoreLabel);
			tablePanel.add(dateLabel);


		}
		return true;
	}


	/**
	 * query DB for best names and scores, descending by score
	 * @return
	 * @throws SQLException 
	 */
	private String[][] getBestScores() throws SQLException {
		return DBUtils.getTenBestScores();
	}

	private class BackButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			MainView.getView().showWelcomeView();
		}
	}

}
