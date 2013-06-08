package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;

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
import java.awt.CardLayout;
import javax.swing.UIManager;

import puzzleAlgorithm.AlgorithmWorker;
import puzzleAlgorithm.BoardSolution;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * The window that is shown to the user while the crossword is created
 * @author yonatan
 *
 */
public class WaitView extends JPanel {

	private PrepareGameView view = null;
	private BoardSolution board = null;
	private JButton btnSkip;
	private JLabel animationLbl;
	private JPanel animationPanel;

	/**
	 * 
	 * @param view - to get the topics and difficulty selected by user
	 * @return
	 */
	static WaitView start(PrepareGameView view) {
		return new WaitView(view);
	}
	/**
	 * Create the panel.
	 */
	public WaitView(PrepareGameView view) {
		this.view = view;
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout(0, 0));

		JLabel lblWeArePreparing = new JLabel("<HTML><center>We are preparing you Crossword.<br>In the meantime, get your juices going...</HTML>");
		lblWeArePreparing.setBackground(Color.WHITE);
		lblWeArePreparing.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblWeArePreparing.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblWeArePreparing, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(2, 1, 0, 0));

		JPanel questionLbl = new JPanel();
		questionLbl.setBackground(UIManager.getColor("Panel.background"));
		centerPanel.add(questionLbl);

		animationPanel = new JPanel();
		animationPanel.setBackground(Color.WHITE);
		centerPanel.add(animationPanel);
		animationPanel.setLayout(new CardLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBackground(UIManager.getColor("Panel.background"));
		animationPanel.add(panel, "name_978375015424865");
		animationLbl = new JLabel(new ImageIcon(WaitView.class.getResource("/resources/rotating-circle.gif")));
		panel.add(animationLbl);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.LIGHT_GRAY);
		animationPanel.add(panel_1, "name_978428366539881");
		panel_1.setLayout(new GridLayout(5, 5, 0, 0));

		JButton nextBtn = new JButton(">>>>");
		panel_1.add(nextBtn);
		add(centerPanel, BorderLayout.CENTER);	

		
		int difficulty = view.getDifficulty();
		//int[] selectedTopics = view.getUserSelectedTopics();
		AlgorithmWorker worker = new AlgorithmWorker(null, difficulty);
		worker.execute();
		
		animationPanel.remove(animationLbl);
		animationPanel.invalidate();
		
		//replace rotating animation with skip button
		btnSkip = new JButton("Skip");
		btnSkip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainView.view.showCrosswordview(board); // must be available only after BoardSolution was created (board != null)
			}
		});
		animationPanel.add(btnSkip);
		animationPanel.invalidate();
		
	}
	
	


}
