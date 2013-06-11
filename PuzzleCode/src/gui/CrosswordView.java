package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.text.Position.Bias;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import com.mysql.jdbc.Util;

import net.sf.sevenzipjbinding.ExtractAskMode;


import puzzleAlgorithm.AlgorithmWorker;
import puzzleAlgorithm.BoardSolution;
import puzzleAlgorithm.PuzzleDefinition;
import puzzleAlgorithm.PuzzleSquare;
import utils.DBUtils;
import utils.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.BorderLayout;


public class CrosswordView extends JPanel {

	private boolean wasShown = false;
	private boolean isPaused = false;
	private int size;

	private TimerJLabel timer;
	private JButton btnPause;
	private JPanel boardPanel;
	private Map<Integer, Map<Integer,List<PuzzleDefinition>>> boardDefs; // maps (i,j) - > square Definition
	private String[][] bestScores;
	private AbstractSquarePanel[][] boardPanelHolders;
	private HintCounterLabel hintCounterLabel;

	private JButton btnCheck;
	private int[][] boardDefCount; 


	private List<JDefinitionLabel> definitionLabelList; //keeping all definition labels 
	private List<SquareTextField> sqaureTextFieldList; //keeping all non-definition text labels
	private List<HintPopupMenu> hintPopupMenuList;
	private Map<Integer, List<int[]>> plainSquaresList;


	//Sizes to difficulty
	Map<Integer,Integer> sizesToDifficulty;
	private final int EASY_SIZE = 8; 
	private final int MEDIUM_SIZE = 11;
	private final int HARD_SIZE = 13;

	//CrosswordView dimensions
	private final int PANEL_WIDTH = 1300;
	private final int PANEL_HEIGHT = 1000;

	//getters & setters 

	List<PuzzleDefinition> getDefinitions() {
		return definitions;
	}

	List<PuzzleDefinition> definitions;
	private JButton btnSurrender;

	public static JPanel start(BoardSolution solution) {
		CrosswordView view = new CrosswordView(solution);
		return view;
	}
	/**
	 * Create the frame.
	 */
	private CrosswordView(BoardSolution solution) {
		initialize();
		intializePlainSquareList();
		drawBoard(solution.getBoard(), solution.getDefinitions()); // draws board
		getHighScores(); // query DB for 10 best scores

	}

	private void initialize() {

		sizesToDifficulty = new HashMap<Integer,Integer>();
		sizesToDifficulty.put(EASY_SIZE, 0);
		sizesToDifficulty.put(MEDIUM_SIZE, 1);
		sizesToDifficulty.put(HARD_SIZE, 2);

		setLayout(new BorderLayout(0, 0));

		// set statistics panel
		JPanel statsPanel = new JPanel();
		statsPanel.setLayout( new FlowLayout(FlowLayout.CENTER, 20, 0));
		timer = new TimerJLabel();
		timer.start();
		statsPanel.add(timer);
		hintCounterLabel = new HintCounterLabel();
		hintCounterLabel.setFont(timer.getFont());
		statsPanel.add(hintCounterLabel);
		add(statsPanel, BorderLayout.NORTH);

		boardPanel = new JPanel();
		add(boardPanel, BorderLayout.CENTER);

		JPanel BtnPanel = new JPanel();
		add(BtnPanel, BorderLayout.SOUTH);

		btnCheck = new JButton("Check my Answers!", new ImageIcon(CrosswordView.class.getResource("/resources/check_medium.png")));
		btnCheck.setFont(btnCheck.getFont().deriveFont(15f));
		btnCheck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int score = CrosswordModel.calculateScore(timer.calcElapsedMilli(), Integer.parseInt(hintCounterLabel.getText()));
				if (isCorrect()) {
					btnCheck.setBackground(Color.GREEN);
					String message = "Congratulations!";
					if (isHighScore(score)) {
						String name = JOptionPane.showInputDialog(CrosswordView.this, "<html><center>" + message + " You scored " + score + " points! <br> Enter your name for fame and glory.</html>");
						//DBUtils.saveNewScore(name, score);
					}
					else 	
						JOptionPane.showMessageDialog(CrosswordView.this, "<html><center> " + message + " You scored " + score + " points! <br> Play again and make a high score!</html>");
				}
				else {
					JOptionPane.showMessageDialog(CrosswordView.this, "<html><center> We know you rock,<br> but something in your answers is WRONG.</html>");

				}
			}
		});

		btnPause = new JButton("Pause", new ImageIcon(CrosswordView.class.getResource("/resources/pause_btn.png")));
		btnPause.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				pauseBtnClicked();
			}
		});

		btnPause.setFont(btnCheck.getFont().deriveFont(15f));

		btnSurrender = new JButton(new ImageIcon(CrosswordView.class.getResource("/resources/surrender.png")));
		btnSurrender.setToolTipText("Surrender Game");
		btnSurrender.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				surrenderBtnClicked();
			}
		});

		JButton btnBack = new JButton(new ImageIcon(CrosswordView.class.getResource("/resources/back_small.png")));
		btnBack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MainView.getView().showWelcomeView();
			}
		});

		BtnPanel.add(btnBack);
		BtnPanel.add(btnPause);
		BtnPanel.add(btnCheck);
		BtnPanel.add(btnSurrender);
		
		wasShown = true;
	}

	void setSizes() {
		MainView.getView().getFrame().setMinimumSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
		MainView.getView().getFrame().setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
	}

	private void getHighScores() {
		bestScores = DBUtils.getTenBestScores();	
	}

	void drawBoard(PuzzleSquare[][] board, List<PuzzleDefinition> definitions) {
		this.definitions = definitions; // save a reference in crossview

		size = board.length;
		boardPanelHolders = new AbstractSquarePanel[size][size];
		boardDefCount = new int[size][size];
		boardDefs =  new HashMap<Integer, Map<Integer, List<PuzzleDefinition>>>(); // Map required because cannot make such an array
		definitionLabelList = new ArrayList<JDefinitionLabel>();
		sqaureTextFieldList = new ArrayList<SquareTextField>();

		initializeCellToDefMap(size); 

		initializeBoardDefCount(size);

		//count number of definitions in each square
		for (PuzzleDefinition definition : definitions) {
			int row = definition.getTextRow();
			int col = definition.getTextCol();
			boardDefCount[row][col]++; 
			boardDefs.get(row).get(col).add(definition); // map i,j to list of definitions (up to two)
		}

		//place definitions in cells
		for (int i = 0; i<size; i++) {
			for (int j=0; j<size; j++) {
				switch (boardDefCount[i][j]) {
				case 0 : { // regular square
					AbstractSquarePanel square;
					if (isPlainSquare(i, j, MEDIUM_SIZE)) { // special empty square, relevant in some templates
						square = new PlainSquare(i,j);
					}
					else  {
					SquareTextField txtLbl = new SquareTextField();
					txtLbl.addKeyListener(new CrosswordKeyListener());
					sqaureTextFieldList.add(txtLbl);
					square =  new TextInputSquare(txtLbl, i, j); 
					}
					boardPanelHolders[i][j] =square;
					break;
				}
				case 1:  {
					//definition square with one definition
					JDefinitionLabel lbl = createDefinitionLabel(i, j, 0);
					OneDefinitionSquare defSquare = new OneDefinitionSquare(i, j);
					defSquare.addTop(lbl);
					boardPanelHolders[i][j] = defSquare;
					definitionLabelList.add(lbl);
					break;
				}
				case 2: { // definition square with two definitions
					// randomly assign lbl1,lbl2 with the two definitions that would occupy the DefinitionSqaure
					JDefinitionLabel lbl1 = createDefinitionLabel(i, j,0);
					definitionLabelList.add(lbl1);
					JDefinitionLabel lbl2 = createDefinitionLabel(i, j,1);
					definitionLabelList.add(lbl2);

					TwoDefinitionSquare defSquare = new TwoDefinitionSquare(i, j);
					boardPanelHolders[i][j] = defSquare;

					// place definitions according to where the arrows would be

					if (isDefinitionTop(lbl1.getDef(), i)) {  
						defSquare.addTop(lbl1); //definition #1 is cell top
						defSquare.addBottom(lbl2); //definition #2 is cell bottom
					}
					else {
						if (isDefinitionBottom(lbl1.getDef(), i)) {
							defSquare.addTop(lbl2);
							defSquare.addBottom(lbl1);
						}
						else {
							if (isDefinitionTop(lbl2.getDef(), i)) {
								defSquare.addTop(lbl2);
								defSquare.addBottom(lbl1);
							}
							else {
								if (isDefinitionBottom(lbl2.getDef(), i)) {
									defSquare.addTop(lbl1);
									defSquare.addBottom(lbl2);
								}
								else { // random
									defSquare.addTop(lbl1);
									defSquare.addBottom(lbl2);
								}
							}
						}
					}
					break;
				}
				default : {
					Logger.writeErrorToLog("Invalid sqaure to draw: Definition sqaure may have up to 2 definitions.");
				}
				}
			}
		}

		boardPanel.setLayout(new GridLayout(size, size));

		//add panels to boardPanel in right order
		for (int i = 0; i<size; i++){
			for (int j=0; j<size; j++) { 
				AbstractSquarePanel square = boardPanelHolders[i][j];
				if (square == null)
					square = new PlainSquare(i,j); // for templates that need filler squares
				boardPanel.add(square);
			}
		}

		// add definitions square listeners (for coloring, mostly)
		addDefinitionSquareListenerToSquares(new JDefinitionLabelListener()); 

		addPopupMenusToDefinitions();

		boardPanel.repaint();
		
		boardPanel.addComponentListener(new ComponentAdapter() {
	        public void componentHidden ( ComponentEvent e )
	        {
	          //if (wasShown) {
	        	  System.out.println("baaaaaaaaa");
	      //    }
	          wasShown = true; // want to ignore the first time in this method
	        }
		});
	}

	/*
	 * upon intialization, add HintPopups to all definitions
	 */
	private void addPopupMenusToDefinitions() {
		hintPopupMenuList = new ArrayList<HintPopupMenu>();

		//add popups to definitionLabels
		for (JDefinitionLabel lbl : definitionLabelList) {
			HintPopupMenu popup = new HintPopupMenu(lbl, lbl.getDef().getEntityId(), hintCounterLabel);
			hintPopupMenuList.add(popup);

			lbl.add(popup);
			lbl.setComponentPopupMenu(popup);
		}
	}

	private void initializeBoardDefCount(int size) {
		for (int i = 0; i<size; i++) {
			for (int j=0; j<size; j++) {
				boardDefCount[i][j] = 0;
			}
		}
	}
	
	private void intializePlainSquareList() {
		plainSquaresList = new HashMap<Integer, List<int[]>>();
		plainSquaresList.put(MEDIUM_SIZE, new ArrayList<int[]>());
		plainSquaresList.put(MEDIUM_SIZE, new ArrayList<int[]>());
		plainSquaresList.put(HARD_SIZE, new ArrayList<int[]>());
		plainSquaresList.get(MEDIUM_SIZE).add(new int[]{10,0});
	}
	
	private boolean isPlainSquare(int row, int col, int template) {
		for (int[] tuple : plainSquaresList.get(template)) {
			if (tuple[0] == row && tuple[1] == col)
				return true;
		}
		return false;

	}

	/*
	 * technical need to map (i,j) - > Definition
	 */
	private void initializeCellToDefMap(int size) {
		for (int i = 0; i<size; i++) {
			boardDefs.put(i, new HashMap<Integer, List<PuzzleDefinition>>());
		}

		for (int i = 0; i<size; i++) {
			for (int j=0; j<size; j++) {
				boardDefs.get(i).put(j, new ArrayList<PuzzleDefinition>());
			}
		}
	}

	private boolean isDefinitionTop(PuzzleDefinition def, int row) {
		return (def.getBeginRow() == row - 1);
	}

	private boolean isDefinitionBottom(PuzzleDefinition def, int row) {
		return (def.getBeginRow() == row + 1);
	}

	private JDefinitionLabel createDefinitionLabel(int i,int j, int defNum) {

//		return new JDefinitionLabel( boardDefs.get(i).get(j).get(defNum), sizesToDifficulty.get(size)); 
		return new JDefinitionLabel( boardDefs.get(i).get(j).get(defNum)); 

	}

	private void colorDefinitionArea(PuzzleDefinition def, Color color,Color caretColor) {

		char direction = def.getDirection();
		switch (direction) {
		case 'r': {
			for (int col = def.getBeginColumn(); col<def.getBeginColumn() + def.getAnswer().length; col++) {
				boardPanelHolders[def.getBeginRow()][col].getComponent(0).setBackground(color);
				((SquareTextField)boardPanelHolders[def.getBeginRow()][col].getComponent(0)).setCaretColor(caretColor);
			}
			break;
		}
		case 'l':{
			for (int col = def.getBeginColumn(); col>def.getBeginColumn() - def.getAnswer().length; col--) {
				boardPanelHolders[def.getBeginRow()][col].getComponent(0).setBackground(color);
				((SquareTextField)boardPanelHolders[def.getBeginRow()][col].getComponent(0)).setCaretColor(caretColor);
			}
			break;
		}
		case 'u': {
			for (int row = def.getBeginRow(); row>def.getBeginRow() - def.getAnswer().length; row--) {
				boardPanelHolders[row][def.getBeginColumn()].getComponent(0).setBackground(color);
				((SquareTextField)boardPanelHolders[row][def.getBeginColumn()].getComponent(0)).setCaretColor(caretColor);
			}
			break;
		}
		case 'd': {
			for (int row = def.getBeginRow(); row<def.getBeginRow() + def.getAnswer().length; row++) {
				boardPanelHolders[row][def.getBeginColumn()].getComponent(0).setBackground(color);
				((SquareTextField)boardPanelHolders[row][def.getBeginColumn()].getComponent(0)).setCaretColor(caretColor);
			}
			break;
		}
		default: {
			Logger.writeErrorToLog("Invalid direction in puzzle definition");
		}
		}
	}

	void unColorDefinitionArea(PuzzleDefinition def) {
		colorDefinitionArea(def, Color.WHITE, Color.BLACK);
	}

	void addColorDefinitionArea(PuzzleDefinition def, Color color) {
		colorDefinitionArea(def, color, color);
	}

	class JDefinitionLabelListener extends MouseAdapter { // had to put it here because definitions List does not exist at view & controller initialize, and didnt want to have controller refernce in this class
		private Color COLOR = Color.BLUE;
		private Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
		private Color origBackgroundColor;

		@Override
		public void mouseEntered(MouseEvent e) {
			JDefinitionLabel lbl =(JDefinitionLabel) e.getSource();
			origBackgroundColor = lbl.getBackground();
			lbl.setOpaque(true);
			lbl.setBackground(BACKGROUND_COLOR);
			addColorDefinitionArea(lbl.getDef(), COLOR);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			JDefinitionLabel lbl =(JDefinitionLabel) e.getSource();
			lbl.setOpaque(false);
			lbl.setBackground(origBackgroundColor);

			unColorDefinitionArea(lbl.getDef());
		}
	}

	void addDefinitionSquareListenerToSquares(MouseListener listener) {
		for (JDefinitionLabel lbl : definitionLabelList) {
			lbl.addMouseListener(listener);
		}
	}

	void remove(MouseListener listener) {
		for (PuzzleDefinition definition : definitions) {
			JPanel square = boardPanelHolders[definition.getTextRow()][definition.getTextCol()];
			for (Component comp : square.getComponents()) {
				if (comp.getListeners(MouseListener.class).length < 1) {
					comp.addMouseListener(listener);
				}
			}
		}
	}

	void notifyCorrectness(boolean isCorrect) { // show to user that he was correct or wrong
		if (isCorrect) {
			btnCheck.setBackground(Color.GREEN);
		}
		else {
			btnCheck.setBackground(Color.RED);
		}
	}
	void pause() {
		if (!isPaused) { // release => pause
			timer.pause();
			isPaused = true;
			Utils.enableComponents(boardPanel, false);
			btnPause.setText("Resume");
			for (JDefinitionLabel lbl : definitionLabelList) {
				for (MouseListener listener : lbl.getMouseListeners()) {
					lbl.removeMouseListener(listener);
				}
				for (MouseMotionListener listener : lbl.getMouseMotionListeners()) {
					lbl.removeMouseMotionListener(listener);
				}
			}
			boardPanel.setEnabled(false);	
		}
		else { // pause => release
			timer.resume();
			isPaused = false;
			boardPanel.setEnabled(true);
			Utils.enableComponents(boardPanel, true);
			btnPause.setText("Pause");
			addDefinitionSquareListenerToSquares(new JDefinitionLabelListener()); // return the definition square listener to all definition squares
			boardPanel.setEnabled(true);
		}
	}

	/**
	 * inner class for hint usage counter
	 * @author yonatan
	 *
	 */
	class HintCounterLabel extends JLabel{
		private int counter = 0;

		void updateCounter() {
			counter++;
			//build string to represent counter
			String text = new Integer(counter / 10).toString() + new Integer(counter % 10).toString();
			//update counter text
			this.setText(text); 
		}
		HintCounterLabel() {
			super(new ImageIcon(CrosswordView.class.getResource("/resources/tip_big.png")),SwingConstants.LEADING);
			this.setText("00");
		}
	}

	private boolean isCorrect() {

		boolean result = true;
		char[] answer;

		for (PuzzleDefinition def : this.getDefinitions()) { // iterate each definition to check if the letters in its domain are correct
			int answerLength =  def.getAnswer().length;
			int indexInAnswer = 0;
			String squareString;

			answer = def.getAnswer().getAnswerString().toLowerCase().toCharArray(); // turn correct answer to char array
			char direction = def.getDirection();
			switch (direction) { // iterate the specific definition domain
			case 'r': {
				for (int col = def.getBeginColumn(); col<def.getBeginColumn() + answerLength; col++) {
					// compare the letter in the JSqaureTextField with the correct letter
					squareString = ((SquareTextField)boardPanelHolders[def.getBeginRow()][col].getComponent(0)).getText().trim();

					if (squareString.length() < 1)
						return false;
					char letter = squareString.toLowerCase().charAt(0); 					
					result &= (letter == answer[indexInAnswer++]);
				}
				break;
			}
			case 'l':{
				for (int col = def.getBeginColumn(); col>def.getBeginColumn() - answerLength; col--) {
					squareString = ((SquareTextField)boardPanelHolders[def.getBeginRow()][col].getComponent(0)).getText().trim();

					if (squareString.length() < 1)
						return false;
					char letter = squareString.toLowerCase().charAt(0); 
					result &= (letter == answer[indexInAnswer++]);
				}
				break;
			}
			case 'u': {
				for (int row = def.getBeginRow(); row>def.getBeginRow() - answerLength; row--) {
					squareString  = ((SquareTextField)boardPanelHolders[row][def.getBeginColumn()].getComponent(0)).getText().trim();

					if (squareString.length() < 1)
						return false;
					char letter = squareString.toLowerCase().charAt(0); 
					result &= (letter == answer[indexInAnswer++]);
				}
				break;
			}
			case 'd': {
				for (int row = def.getBeginRow(); row<def.getBeginRow() + answerLength; row++) {
					squareString  = ((SquareTextField)boardPanelHolders[row][def.getBeginColumn()].getComponent(0)).getText().trim();

					if (squareString.length() < 1)
						return false;
					char letter = squareString.toLowerCase().charAt(0); 
					result &= (letter == answer[indexInAnswer++]);
				}
				break;
			}
			default: {
				Logger.writeErrorToLog("Invalid direction in puzzle definition");
			}
			}
		}
		return result;
	}

	private void writeCorrectLetters() {
		char[] answer;
		int	indexInAnswer;

		for (PuzzleDefinition def : this.getDefinitions()) { // iterate each definition to check if the letters in its domain are correct
			indexInAnswer = 0;
			answer = def.getAnswer().getAnswerString().toCharArray(); // turn correct answer to char array
			char direction = def.getDirection();
			switch (direction) { // iterate the specific definition domain
			case 'r': {
				for (int col = def.getBeginColumn(); col<def.getBeginColumn() + def.getAnswer().length; col++) {
					((SquareTextField)boardPanelHolders[def.getBeginRow()][col].getComponent(0)).setText(Character.toString(answer[indexInAnswer++])); // there should be only one letter 

				}
				break;
			}
			case 'l':{
				for (int col = def.getBeginColumn(); col>def.getBeginColumn() - def.getAnswer().length; col--) {
					((SquareTextField)boardPanelHolders[def.getBeginRow()][col].getComponent(0)).setText(Character.toString(answer[indexInAnswer++])); 
				}
				break;
			}
			case 'u': {
				for (int row = def.getBeginRow(); row>def.getBeginRow() - def.getAnswer().length; row--) {
					((SquareTextField)boardPanelHolders[row][def.getBeginColumn()].getComponent(0)).setText(Character.toString(answer[indexInAnswer++])); 
				}
				break;
			}
			case 'd': {
				for (int row = def.getBeginRow(); row<def.getBeginRow() + def.getAnswer().length; row++) {
					((SquareTextField)boardPanelHolders[row][def.getBeginColumn()].getComponent(0)).setText(Character.toString(answer[indexInAnswer++])); 
				}
				break;
			}
			default: {
				Logger.writeErrorToLog("Invalid direction in puzzle definition");
			}
			}
		}
		return;
	}

	private boolean isHighScore(int score) {
		if (bestScores.length < 10)
			return true;
		for (int i = 0; i<bestScores.length; i++) {
			if (score > Integer.parseInt(bestScores[i][1]))
				return true;
		}
		return false;
	}

	private void pauseBtnClicked() {
		this.pause();
	}

	private void surrenderBtnClicked() {
		writeCorrectLetters();
		btnSurrender.setEnabled(false);
		btnCheck.setEnabled(false);
		btnPause.setEnabled(false);
		timer.killTimer();
		Utils.enableComponents(boardPanel, false);
	}

	/**
	 * arrow traversal listener
	 * @author yonatan
	 *
	 */
	private class CrosswordKeyListener extends KeyAdapter{

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getSource() instanceof SquareTextField) {
				TextInputSquare square = (TextInputSquare) ((Component) e.getSource()).getParent(); // parent of SquareTextField is a RegularSquare
				int row = square.getRow();
				int col = square.getCol();
				int newRow, newCol;
				AbstractSquarePanel newSquare = null;
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					newRow = (row == 0) ? size - 1 : row - 1;
					newSquare = (AbstractSquarePanel) boardPanelHolders[newRow][col];
					break;
				case KeyEvent.VK_DOWN:
					newRow = (row == (size -1)) ? 0 : row + 1;
					newSquare = (AbstractSquarePanel) boardPanelHolders[newRow][col];
					break;
				case KeyEvent.VK_LEFT:
					newCol = (col == 0) ? size - 1 : col -1;
					newSquare = (AbstractSquarePanel) boardPanelHolders[row][newCol]; 
					break;
				case KeyEvent.VK_RIGHT:
					newCol = (col == (size - 1)) ? 0 : col + 1;
					newSquare = (AbstractSquarePanel) boardPanelHolders[row][newCol];
					break;
				default:
					return;
				}
				newSquare.getComponent(0).requestFocus();
			}
		}
	}
}
