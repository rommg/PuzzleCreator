package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.Color;

import puzzleAlgorithm.AlgorithmWorker;
import puzzleAlgorithm.BoardSolution;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.BevelBorder;

/**
 * The window that is shown to the user while the crossword is created
 * @author yonatan
 *
 */
public class WaitView extends JPanel {

	private BoardSolution board = null;
	private JButton btnSkip;
	private JLabel infoLabel;
	private JPanel animationPanel;
	private int[] topics;
	private int difficulty;

	public void setBoard(BoardSolution solution) {
		this.board = solution;
	}
	/**
	 * 
	 * @param view - to get the topics and difficulty selected by user
	 * @return
	 */
	static WaitView start(int[] topics, int difficulty) {
		return new WaitView(topics,difficulty);
	}
	/**
	 * Create the panel.
	 */
	public WaitView(int[] topics, int difficulty) {
		this.topics = topics;
		this.difficulty = difficulty;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout(0, 0));

		JLabel lblWeArePreparing = new JLabel("<HTML><center>Get Your Juices Going!</HTML>");
		lblWeArePreparing.setBackground(Color.WHITE);
		lblWeArePreparing.setFont(new Font("Tw Cen MT Condensed", Font.PLAIN, 35));
		lblWeArePreparing.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblWeArePreparing, BorderLayout.NORTH);

		animationPanel = new JPanel();
		animationPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		add(animationPanel, BorderLayout.SOUTH);
		animationPanel.setBackground(Color.WHITE);
		animationPanel.setLayout(new GridLayout(0, 2, 0, 0));
		infoLabel = new JLabel(new ImageIcon(WaitView.class.getResource("/resources/about.png")));
		infoLabel.setHorizontalAlignment(SwingConstants.LEFT);
		infoLabel.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.PLAIN, 16));
		animationPanel.add(infoLabel);
		infoLabel.setText("Starting...");
		animationPanel.invalidate();

		//replace rotating animation with skip button
		btnSkip = new JButton("GO!");
		btnSkip.setEnabled(false);
		btnSkip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				MainView.view.showCrosswordview(); // must be available only after BoardSolution was created (board != null)
			}
		});
		animationPanel.add(btnSkip);
		animationPanel.invalidate();

		JPanel centerPanel = new JPanel();
		centerPanel.setBackground(Color.WHITE);
		centerPanel.setLayout(new GridLayout(2, 1, 0, 0));
		add(centerPanel, BorderLayout.CENTER);	

		// start running algorithm in background
		startAlgorithmCalculationThread(topics, difficulty);

	}

	private void startAlgorithmCalculationThread(int[] topics, int difficulty)  {
		AlgorithmWorker worker = new AlgorithmWorker(this, topics, difficulty);
		worker.execute();
	}

	public void setSkipBtnEnabled() {
		btnSkip.setText("GO!");
		btnSkip.setEnabled(true);
	}

	public void setProgressMessage(String text) {
		infoLabel.setText(text);
	}




}
