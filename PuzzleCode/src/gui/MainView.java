package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.UIManager;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import javax.swing.JSplitPane;
import javax.swing.JLayeredPane;
import javax.swing.JToolBar;
import javax.swing.JButton;

public class MainView {

	private JFrame frame;
	private JMenuItem mntmUpdateKnowledge;
	private JMenuItem mntmMassiveImport;
	private JMenu mnImport;
	private JButton btnPlay;

	/**
	 * Launch the application.
	 */
	public static void startGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainView window = new MainView();
					@SuppressWarnings("unused")
					MainController controller = new MainController(null, window);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainView() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 606, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("Game");
		menuBar.add(mnFile);

		JMenuItem mntmSd = new JMenuItem("Play new Crossword");
		mnFile.add(mntmSd);

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mnFile.add(mntmQuit);

		mnImport = new JMenu("Knowledge Management");
		menuBar.add(mnImport);

		mntmMassiveImport = new JMenuItem("Massive Import...");
		mnImport.add(mntmMassiveImport);

		mntmUpdateKnowledge = new JMenuItem("Add/Update Existing Information...");
		mnImport.add(mntmUpdateKnowledge);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

		btnPlay = new JButton("Play!");
		btnPlay.setIcon(new ImageIcon(MainView.class.getResource("/resources/k-timer-icon.png")));
		frame.getContentPane().add(btnPlay);
	}

	void addMassiveImportListener(ActionListener listener) {
		mntmMassiveImport.addActionListener(listener);
	}
	void addUpdateKnowledgeListener(ActionListener listener) {
		mntmUpdateKnowledge.addActionListener(listener);
	}
	void addPlayListener(ActionListener listener) {
		btnPlay.addActionListener(listener);
	}
}
