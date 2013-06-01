package gui;

import java.awt.EventQueue;


import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.JSeparator;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;

import com.sun.java.swing.plaf.windows.resources.windows;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * The one and only frame in the application.
 * @author yonatan
 *
 */
public class MainView {

	public static MainView view = null; 

	JFrame frame;
	private JPanel menuPanel; // left side menu 
	private Map<String,JButton> menuPanelBtns; //mapping from windows name to the button that leads to it
	private JPanel cardPanel; // This is the main panel in the application; this is what switches "screens"
	private int menuBtnCounter = 0;


	private final int FRAME_HEIGHT = 600;
	private final int FRAME_WIDTH = 600;
	private final int MAX_NUM_BUTTONS_IN_MENU = 8;
	// define application's view names


	JPanel prepareGame = null;
	JButton[] menuPanelBtnsArray;
	Map<JButton, JLabel> btnLabels; 

	private JPanel crosswordView = null;
	private JPanel welcomePanel;


	/**
	 * Launch the application.
	 */
	public static void start() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					if (view == null) {
						view = new MainView();
						@SuppressWarnings("unused")
						MainController controller = new MainController(null, view);
						view.frame.setVisible(true);
					}
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
		setSizes();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// build mainPanel
		menuPanel = new JPanel();
		menuPanel.setSize((int) Math.round(0.5 * FRAME_WIDTH), FRAME_HEIGHT);
		menuPanel.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, null), "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		menuPanel.setLayout(new GridBagLayout());
		frame.getContentPane().add(menuPanel, BorderLayout.WEST);

		menuPanelBtns = new HashMap<String,JButton>();
		menuPanelBtnsArray = new JButton[MAX_NUM_BUTTONS_IN_MENU];
		btnLabels = new HashMap<JButton,JLabel>();

		// top buttons
		createButton("Play", "game.png");
		createButton("Continue Game", "continue.png");
		createButton("Hall of Fame", "best.png");

		// middle buttons
		createButton("Add Definition", "add.png");
		createButton("Add Hints", "add.png");
		createButton("Massive Import", "addDB.png");

		// bottom buttons

		createButton("Help", "help.png");
		createButton("About", "about.png");

		addButtonsTPanel(menuPanelBtnsArray);

		//build main panel 
		cardPanel = new JPanel();
		cardPanel.setLayout(new CardLayout());

		welcomePanel = new JPanel();
		welcomePanel.setLayout(new BorderLayout());
		welcomePanel.setBackground(Color.WHITE);
		JLabel logo = new JLabel(new ImageIcon(MainView.class.getResource("/resources/crossword.jpg")));
		welcomePanel.add(logo, BorderLayout.CENTER);
		showWelcomeView();

		//add formPanel - this panel will change
		frame.getContentPane().add(cardPanel, BorderLayout.CENTER);

	}

	private void setSizes() {
		frame.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		frame.setMaximumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
	}

	private void createButton(String text, String resourceName) {
		JButton btn = new JButton();
		btn.setLayout(new BorderLayout());
		JLabel label = new JLabel(text);
		btnLabels.put(btn, label);
		label.setHorizontalAlignment(JLabel.CENTER);
		JLabel image = new JLabel(new ImageIcon(MainView.class.getResource("/resources/" + resourceName)));
		btn.add(label, BorderLayout.CENTER);
		btn.add(image, BorderLayout.WEST);
		menuPanelBtns.put(text, btn);
		menuPanelBtnsArray[menuBtnCounter++] = btn;
	}

	/**
	 * populates the left side menu
	 */
	private void addButtonsTPanel(JButton[] btnArray) {
		int row = 0;

		//Button constraint
		GridBagConstraints btnConstraint = new GridBagConstraints();
		btnConstraint.gridx = 0;
		btnConstraint.gridy = 0;
		btnConstraint.fill = GridBagConstraints.BOTH;
		btnConstraint.weightx = 1;
		btnConstraint.weighty = 1;
		btnConstraint.insets = new Insets(5, 5, 5, 5);

		int buttonCounter = 0;

		// add buttons to Jpanel
		for (row = 0; row <=9; row++ ) {
			btnConstraint.gridy = row;
			if ((row == 3) || (row== 7)) { // seperator cells
				JSeparator seperator = new JSeparator();
				seperator.setPreferredSize(new Dimension(1,1));
				btnConstraint.insets = new Insets(0, 0, 0, 0);
				btnConstraint.weightx = 0.3;
				btnConstraint.weighty = 0.3;
				btnConstraint.fill = GridBagConstraints.HORIZONTAL;
				menuPanel.add(seperator, btnConstraint);

				//return old values
				btnConstraint.insets = new Insets(5, 5, 5, 5);
				btnConstraint.fill = GridBagConstraints.BOTH;
				btnConstraint.weightx = 1;
				btnConstraint.weighty = 1;
			}
			else { //button 
				JButton btn = btnArray[buttonCounter++];
				menuPanel.add(btn, btnConstraint);
			}
		}
	}

	void addMenuBtnsListener(ActionListener listener) {
		for (JButton btn : menuPanelBtns.values())
			btn.addActionListener(listener);
	}

	void playBtnClicked() {
		showPrepareView();
	}
	
	void hallOfFameBtnClicked() {
		showHallOfFameView();
	}

	/**
	 * switch to PrepareView card
	 */
	void showPrepareView() {
		if (prepareGame == null) {
			prepareGame = PrepareGameView.start();
			cardPanel.add(prepareGame, Window.PrepareGame.toString());
		}
		CardLayout cl = (CardLayout)(cardPanel.getLayout());
		setSizes();
		frame.pack();
		frame.setLocationRelativeTo(null);
		cl.show(cardPanel, Window.PrepareGame.toString());
	}

	/**
	 * switch to CrosswordView
	 */
	void showCrosswordview() {
		if (crosswordView == null) {
			crosswordView = CrosswordView.start();
			cardPanel.add(crosswordView, Window.Crossword.toString());
		}

		CardLayout cl = (CardLayout)(cardPanel.getLayout());
		((CrosswordView)crosswordView).setSizes();
		frame.pack();
		frame.setLocationRelativeTo(null);
		cl.show(cardPanel,Window.Crossword.toString());
	}

	/**
	 * switch to WaitView
	 */
	void showWaitView() {
		JPanel waitView = WaitView.start();
		cardPanel.add(waitView, Window.Wait.toString());

		CardLayout cl = (CardLayout)(cardPanel.getLayout());
		setSizes();
		cl.show(cardPanel, Window.Wait.toString());
	}

	/**
	 * switch to welcome screen
	 */
	void showWelcomeView() {
		if (crosswordView == null) {
			cardPanel.add(welcomePanel, Window.Welcome.toString());
		}

		CardLayout cl = (CardLayout)(cardPanel.getLayout());
		cl.show(cardPanel,Window.Welcome.toString());
	}

	/**
	 * switch to hall of fame view
	 */
	void showHallOfFameView() {
		HallOfFameView view = HallOfFameView.start();
		cardPanel.add(view, Window.HallOfFame.toString());

		CardLayout cl = (CardLayout)(cardPanel.getLayout());
		cl.show(cardPanel,Window.HallOfFame.toString());
	}



}
