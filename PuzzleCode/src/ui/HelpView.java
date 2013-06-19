package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class HelpView extends JPanel {
	static HelpView start() {
		return new HelpView();
	}

	/**
	 * Create the panel.
	 */
	private HelpView() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel btnPanel = new JPanel();
		panel.add(btnPanel, BorderLayout.SOUTH);

		JButton btnBack = new JButton("");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainView.getView().showWelcomeView();
			}
		});
		btnBack.setIcon(new ImageIcon(getClass().getClassLoader().getResource("resources/back.png")));
		btnPanel.add(btnBack);

		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.WHITE);
		panel.add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

		JLabel lblTitle = new JLabel("Crossword Mastermind");
		topPanel.add(lblTitle);
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Stencil", Font.PLAIN, 25));

		JLabel lblCourse = new JLabel("<html><br><b>Don't be shy, Just call us!</b></html>");
		topPanel.add(lblCourse);
		lblCourse.setHorizontalAlignment(SwingConstants.LEFT);
		lblCourse.setFont(new Font("Tahoma", Font.PLAIN, 24));

		JPanel mainPanel = new JPanel();
		panel.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel lblProgrammers = new JLabel("<html><h3>Yonatan Wilkof: 050 789 7788</h3><h3>Saleet Klein: 054 255 9280</h3><h3>Guy Romm: 052 377 0338</h3><h3>David Franco: 054 540 8227</h3><br><h4>*not on saturdays</h4></html>");
		mainPanel.add(lblProgrammers);
		lblProgrammers.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblProgrammers.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel lblImg = new JLabel("");
		mainPanel.add(lblImg);
		lblImg.setHorizontalAlignment(SwingConstants.CENTER);
		lblImg.setIcon(new ImageIcon(getClass().getClassLoader().getResource("resources/crossword_small.jpg")));

	}

}
