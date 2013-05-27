package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

import javax.swing.Box.Filler;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.SwingConstants;
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
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JSeparator;

public class MainView {

	private JFrame frame;
	private JButton btnPlay;
	private JButton btnContinueGame;
	private JButton btnHallOfFame;
	private JButton btnHelp;
	private List<JButton> menuPanelTopBtns;
	private List<JButton> menuPanelBottomBtns;
	private List<JButton> menuPanelAboutBtns;


	private final int FRAME_HEIGHT = 850;
	private final int FRAME_WIDTH = 800;
	private final int MAIN_PANEL_BTN_WIDTH = (int) Math.round(0.3 * FRAME_WIDTH);
	private final int ICON_TEXT_GAP = 10;
	private JButton btnAddDef;
	private JButton btnAddHints;
	private JButton btnImport;
	private JPanel menuPanel;
	private JPanel formPanel;
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
		frame.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

		// center screen
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize(); 
		int x=(int)((dimension.getWidth() - 800)/2);
		int y=(int)((dimension.getHeight() - 800)/2);
		frame.setLocation(x, y);  
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// build mainPanel
		menuPanelTopBtns = new ArrayList<JButton>();
		menuPanelBottomBtns = new ArrayList<JButton>();
		menuPanelAboutBtns = new ArrayList<JButton>();

		menuPanel = new JPanel();
		menuPanel.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, null), "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		frame.getContentPane().add(menuPanel, BorderLayout.WEST);
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));	
		menuPanel.setMinimumSize(new Dimension(menuPanel.getPreferredSize().width + 10, menuPanel.getPreferredSize().height));
		menuPanel.setMaximumSize(new Dimension((int) Math.round(0.2 * FRAME_WIDTH), FRAME_HEIGHT));
		menuPanel.setPreferredSize(new Dimension((int) Math.round(0.2 * FRAME_WIDTH), FRAME_HEIGHT));

		// top buttons

		btnPlay = new JButton("<html><center><body>Play!</body></html>", new ImageIcon(MainView.class.getResource("/resources/game.png")));
		menuPanelTopBtns.add(btnPlay);

		btnContinueGame = new JButton("<html><center><body>Continue <Br>Game</body></html>",new ImageIcon(MainView.class.getResource("/resources/continue.png")));
		menuPanelTopBtns.add(btnContinueGame);

		btnHallOfFame = new JButton("<html><center><body>Hall of <br>Fame</body></html>", new ImageIcon(MainView.class.getResource("/resources/best.png")));
		menuPanelTopBtns.add(btnHallOfFame);



		setButtonsDimensionAndAlignment(menuPanelTopBtns);

		// add top buttons to Jpanel
		addButtonsTPanel(menuPanelTopBtns);

		menuPanel.add(getBoxFiller());

		JSeparator seperator1 = getSeperatorForMenuPanel();

		menuPanel.add(seperator1);

		// bottom buttons

		btnAddDef = new JButton("<html><left><body>Add New <Br>Definition</body></html>",new ImageIcon(MainView.class.getResource("/resources/add.png")));
		menuPanelBottomBtns.add(btnAddDef);

		btnAddHints = new JButton("<html><left><body>Add Hints</body></html>",new ImageIcon(MainView.class.getResource("/resources/add.png")));
		//btnAddHints.setIconTextGap(ICON_TEXT_GAP);
		menuPanelBottomBtns.add(btnAddHints);

		btnImport = new JButton("<html><center><body>Massive <Br>Import</body></html>", new ImageIcon(MainView.class.getResource("/resources/addDB.png")));
		menuPanelBottomBtns.add(btnImport);

		setButtonsDimensionAndAlignment(menuPanelBottomBtns);

		// add bottom buttons to Jpanel
		addButtonsTPanel(menuPanelBottomBtns);

		menuPanel.add(getBoxFiller());

		JSeparator seperator2 = getSeperatorForMenuPanel();

		menuPanel.add(seperator2);

		btnHelp = new JButton("<html><center><body>Help</body></html>", new ImageIcon(MainView.class.getResource("/resources/help.png")));
		menuPanelAboutBtns.add(btnHelp);

		JButton btnAbout = new JButton("<html><center><body>About</body></html>", new ImageIcon(MainView.class.getResource("/resources/about.png")));
		menuPanelAboutBtns.add(btnAbout);

		setButtonsDimensionAndAlignment(menuPanelAboutBtns, 20);
		addButtonsTPanel(menuPanelAboutBtns);

		//build formPanel 
		formPanel = new JPanel();
		formPanel.setLayout(new BorderLayout(0, 0));
		formPanel.setBackground(Color.WHITE);
		JLabel logo = new JLabel(new ImageIcon(MainView.class.getResource("/resources/crossword.jpg")));
		formPanel.add(logo, BorderLayout.CENTER);

		//add formPanel - this panel will change
		frame.getContentPane().add(formPanel, BorderLayout.CENTER);
	}

	private JSeparator getSeperatorForMenuPanel() {
		JSeparator seperator = new JSeparator();

		seperator.setMaximumSize(new Dimension(menuPanel.getPreferredSize().width + 50, (int) Math.round(FRAME_HEIGHT * 0.01)));
		seperator.setMinimumSize(new Dimension(menuPanel.getPreferredSize().width + 50, (int) Math.round(FRAME_HEIGHT * 0.01)));
		seperator.setPreferredSize(new Dimension(menuPanel.getPreferredSize().width + 50,(int) Math.round(FRAME_HEIGHT * 0.01)));
		return seperator;
	}

	private Filler getBoxFiller() {
		Dimension minSize = new Dimension(0, 20);
		Dimension prefSize = new Dimension(0, 20);
		Dimension maxSize = new Dimension(0, 20);
		return new Box.Filler(minSize, prefSize, maxSize);
	}
	private void addButtonsTPanel(List<JButton> collection) {
		// add buttons to Jpanel
		for (JComponent btn : collection) {
			menuPanel.add(getBoxFiller());
			menuPanel.add(btn);
		}
	}

	private void setButtonsDimensionAndAlignment(List<JButton> collection) {
		setButtonsDimensionAndAlignment(collection, 30);
	}

	private void setButtonsDimensionAndAlignment(List<JButton> collection, int height) {
		for (JComponent btn : collection){
			//btn.setMinimumSize(new Dimension((int)Math.round(MAIN_PANEL_BTN_WIDTH*0.8), btn.getPreferredSize().height + height));
			//btn.setMaximumSize(new Dimension((int)Math.round(MAIN_PANEL_BTN_WIDTH*0.8), btn.getPreferredSize().height + height));
			//btn.setPreferredSize(new Dimension((int)Math.round(MAIN_PANEL_BTN_WIDTH*0.8), btn.getPreferredSize().height + height));
			((JButton)btn).setIconTextGap(ICON_TEXT_GAP);
			btn.setAlignmentX(Component.CENTER_ALIGNMENT);
		}
	}

	void addPlayListener(ActionListener listener) {
		btnPlay.addActionListener(listener);
	}
}
