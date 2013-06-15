package gui;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.FlowLayout;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AboutView extends JPanel {
	
	static AboutView start() {
		return new AboutView();
	}

	/**
	 * Create the panel.
	 */
	private AboutView() {
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
		btnBack.setIcon(new ImageIcon(getClass().getResource("/resources/back.png")));
		btnPanel.add(btnBack);
		
		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.WHITE);
		panel.add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		
		JLabel lblTitle = new JLabel("Crossword Mastermind");
		topPanel.add(lblTitle);
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Stencil", Font.PLAIN, 25));
		
		JLabel lblCourse = new JLabel("<html>Database Systems, Spring Semester 2013</html>");
		topPanel.add(lblCourse);
		lblCourse.setHorizontalAlignment(SwingConstants.LEFT);
		lblCourse.setFont(new Font("Tahoma", Font.PLAIN, 18));
		
		JPanel mainPanel = new JPanel();
		panel.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblProgrammers = new JLabel("<html><center>Programmed By:  <br> <br>Guy Romm<br>David Franco<br>Saleet Klein<br>Yonatan Wilkof</html>");
		mainPanel.add(lblProgrammers);
		lblProgrammers.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblProgrammers.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblImg = new JLabel("");
		mainPanel.add(lblImg);
		lblImg.setHorizontalAlignment(SwingConstants.CENTER);
		lblImg.setIcon(new ImageIcon(getClass().getResource("/resources/crossword_small.jpg")));

	}

}
