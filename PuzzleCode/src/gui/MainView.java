package gui;

import java.awt.EventQueue;


import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;

import sun.java2d.Disposer;
import utils.Logger;

import main.PuzzleCreator;
import java.awt.Color;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * The one and only frame in the application.
 * @author yonatan
 *
 */
public class MainView {

	private static MainView view = null; 

	public static MainView getView() {
		return view;
	}

	JFrame getFrame() {
		return frame;
	}


	private JFrame frame = null;
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

	//views instances
	private JPanel crosswordView = null;
	private JPanel welcomePanel = null;
	private JPanel management = null;
	private JPanel massive = null;
	private JPanel about = null;

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
						view.frame.setVisible(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void Dispose() {
		view = null;
		frame.dispose();
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
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setIconImage(new ImageIcon(MainView.class.getResource("/resources/crossword_tiny.gif")).getImage());
		frame.setTitle("Crossword Mastermind");

		// build mainPanel
		menuPanel = new JPanel();
		menuPanel.setSize((int) Math.round(0.5 * FRAME_WIDTH), FRAME_HEIGHT);
		menuPanel.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, null), "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		menuPanel.setLayout(new GridBagLayout());
		frame.getContentPane().add(menuPanel, BorderLayout.WEST);

		menuPanelBtns = new HashMap<String,JButton>();
		menuPanelBtnsArray = new JButton[MAX_NUM_BUTTONS_IN_MENU];
		btnLabels = new HashMap<JButton,JLabel>();

		JButton btn = null; 

		// top buttons
		btn = createButton("Play", "game.png");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				playBtnClicked();
			}
		});

		//btn = createButton("Continue Game", "continue.png");

		btn = createButton("Hall of Fame", "best.png");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				hallOfFameBtnClicked();
			}
		});

		// middle buttons
		btn = createButton("<html><center>Knowledge<br>Management</html>", "add.png");
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mamangementBtnClick();
			}
		});
		
		btn = createButton("Massive Import", "addDB.png");
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				massiveImportBtnClicked();
			}
		});

		// bottom buttons

		createButton("Help", "help.png");
		btn = createButton("About", "about.png");
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showAboutView();
			}
		});

		addButtonsTPanel(menuPanelBtnsArray);

		//build main panel 
		cardPanel = new JPanel();
		cardPanel.setLayout(new CardLayout());

		welcomePanel = new JPanel();
		welcomePanel.setLayout(new BorderLayout());
		welcomePanel.setBackground(Color.WHITE);
		JLabel logo = new JLabel(new ImageIcon(MainView.class.getResource("/resources/crossword.jpg")));
		welcomePanel.add(logo, BorderLayout.CENTER);
		
		JPanel titlePanel = new JPanel(new GridLayout(2,1));
		titlePanel.setBackground(Color.WHITE);
		JPanel empty = new JPanel();
		empty.setBackground(Color.WHITE);
		titlePanel.add(empty);
		
		JLabel title = new JLabel("Crossword Mastermind");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("Stencil", Font.PLAIN, 30));
		titlePanel.add(title);
		welcomePanel.add(titlePanel, BorderLayout.NORTH);
		showWelcomeView();

		//add formPanel - this panel will change
		frame.getContentPane().add(cardPanel, BorderLayout.CENTER);
		
		//close DB Connections when exiting
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
		    	int option = JOptionPane.showConfirmDialog(
	               	    MainView.this.frame,  
	                    "Are you sure you want to quit?", "Exit Crossword Mastermind", JOptionPane.YES_NO_OPTION);
	            if( option == JOptionPane.YES_OPTION ) {  
		        	try {
		        	PuzzleCreator.connectionPool.closeConnections();
		        	}
		        	catch (SQLException e) {
		        		Logger.writeErrorToLog("SQLException while trying to close DB Connections");
		        	}
		        	finally {
			            System.exit(0);
		        	}
	            }
		    }
		});

	}

	private void setSizes() {
		frame.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		frame.setMaximumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
	}

	private JButton createButton(String text, String resourceName) {
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
		return btn;
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
		for (row = 0; row <=7; row++ ) {
			btnConstraint.gridy = row;
			if ((row == 2) || (row== 5)) { // seperator cells
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

	void mamangementBtnClick() {
		showManagemntView();
	}
	
	void massiveImportBtnClicked() {
		showMassiveImportView();
	}
	
	void aboutBtnClicked() {
		showAboutView();
	}

	/**
	 * switch to PrepareView card
	 */
	void showPrepareView() {

		//create PrepareView Windows afresh
		prepareGame = PrepareGameView.start();
		cardPanel.add(prepareGame, Window.PrepareGame.toString());

		CardLayout cl = (CardLayout)(cardPanel.getLayout());
		setSizes();
		cl.show(cardPanel, Window.PrepareGame.toString());
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	
	public void setCrosswordView(CrosswordView view) {
		crosswordView = view;
		cardPanel.add(view, Window.Crossword.toString());
	}

	/**
	 * switch to CrosswordView
	 */
	public void showCrosswordview() {
		
		CardLayout cl = (CardLayout)(cardPanel.getLayout());
		cl.show(cardPanel,Window.Crossword.toString());
		((CrosswordView)crosswordView).setFrameSizeByBoardSize();

		//frame.pack();
		frame.setLocationRelativeTo(null);
	}

	/**
	 * switch to WaitView
	 */
	void showWaitView(int[] topics, int difficulty) {

		JPanel waitView = WaitView.start(topics, difficulty);
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
		setSizes();
		cl.show(cardPanel,Window.Welcome.toString());

		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	/**
	 * switch to hall of fame view
	 */
	void showHallOfFameView() {
		HallOfFameView view = HallOfFameView.start();
		cardPanel.add(view, Window.HallOfFame.toString());
		setSizes();
		CardLayout cl = (CardLayout)(cardPanel.getLayout());
		cl.show(cardPanel,Window.HallOfFame.toString());
		frame.pack();
		frame.setLocationRelativeTo(null);

	}

	void showManagemntView() {
		if (management == null) {
			management = ManagementView.start();
			cardPanel.add(management, Window.Management.toString());
		}

		CardLayout cl = (CardLayout)(cardPanel.getLayout());
		cl.show(cardPanel,Window.Management.toString());
		setSizes();
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	
	void showMassiveImportView() {
		if (massive == null) {
			massive = MassiveImportView.start();
			cardPanel.add(massive, Window.MassiveImport.toString());
		}

		CardLayout cl = (CardLayout)(cardPanel.getLayout());
		cl.show(cardPanel,Window.MassiveImport.toString());
		setSizes();
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	void showAboutView() {
		if (about== null) {
			about = AboutView.start();
			cardPanel.add(about, Window.About.toString());
		}

		CardLayout cl = (CardLayout)(cardPanel.getLayout());
		cl.show(cardPanel,Window.About.toString());
		setSizes();
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	
	
}
