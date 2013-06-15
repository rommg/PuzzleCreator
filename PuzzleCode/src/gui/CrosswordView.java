package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;

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
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.BorderLayout;


public class CrosswordView extends JPanel {

	private boolean isPaused = false;
	private int size;

	private TimerJLabel timer;
	private JButton btnPause;
	private JPanel boardPanel;
	private Map<Integer, Map<Integer,List<PuzzleDefinition>>> boardDefs; // maps (i,j) - > square Definition
	private AbstractSquarePanel[][] boardPanelHolders;
	private HintCounterLabel hintCounterLabel;

	private JButton btnCheck;
	private int[][] boardDefCount; 

	private List<JDefinitionLabel> definitionLabelList; //keeping all definition labels 
	private List<SquareTextField> sqaureTextFieldList; //keeping all non-definition text labels
	private Map<JDefinitionLabel, HintPopupMenu> definitionToHintPopupMap;

	//CrosswordView dimensions
	private final int EASY_PANEL_WIDTH =(int) Math.round(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width * 0.7);
	private final int EASY_PANEL_HEIGHT = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	private final int MED_PANEL_WIDTH = (int) Math.round(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width * 0.8);
	private final int MED_PANEL_HEIGHT = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	private final int HARD_PANEL_WIDTH = (int) Math.round(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width * 0.95);
	private final int HARD_PANEL_HEIGHT =  GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;


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
		size = solution.getBoard().length;
		
		initialize();
		
		drawBoard(solution.getBoard(), solution.getDefinitions()); // draws board
	}

	private void initialize() {


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

		JButton btnArtificialWin = new JButton("CHEAT Solve");
		btnArtificialWin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {	
				writeCorrectLetters();
				btnCheck.setEnabled(true);
			}
		});
		BtnPanel.add(btnArtificialWin);
		
		btnCheck = new JButton("Check my Answers!", new ImageIcon(getClass().getResource("../resources/check_medium.png")));
		btnCheck.setFont(btnCheck.getFont().deriveFont(15f));
		btnCheck.setEnabled(false);
		btnCheck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int score = CrosswordModel.calculateScore(getDifficultyFromSize(), timer.calcElapsedMilli(), Integer.parseInt(hintCounterLabel.getText()));
				if (isCorrect()) {
					btnCheck.setBackground(Color.GREEN);
					String message = "Congratulations!";
					String[][] highScores = null;
					try {
						highScores = getHighScores(); // query DB for 10 best scores
					} catch (SQLException e1) {
						Utils.showErrorMessage("Oops! There was a DB error, we cannot save you high score.");
					}

					if (highScores != null && isHighScore(highScores, score)) {
						String name = JOptionPane.showInputDialog(CrosswordView.this, "<html><center>" + message + " You scored " + score + " points! <br> Enter your name for fame and glory.</html>");
						try {
							DBUtils.addBestScore(name, score);
						} catch (SQLException e) {
							Utils.showErrorMessage("Oops! There was a DB error, we cannot save you high score." );
						}
					}
					else 	
						JOptionPane.showMessageDialog(CrosswordView.this, "<html><center>" + message + " You scored " + score + " points! <br> Play again and make a high score!</html>");
					btnCheck.setEnabled(false);
					pause();
					btnSurrender.setEnabled(false);
				}
				else {
					JOptionPane.showMessageDialog(CrosswordView.this, "<html><center> We know you rock,<br> but something in your answers is WRONG.</html>");

				}
			}
		});

		btnPause = new JButton("Pause", new ImageIcon(getClass().getResource("../resources/pause_btn.png")));
		btnPause.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				pauseBtnClicked();
			}
		});

		btnPause.setFont(btnCheck.getFont().deriveFont(15f));

		btnSurrender = new JButton("Surrender", new ImageIcon(getClass().getResource("../resources/surrender.png")));
		btnSurrender.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				surrenderBtnClicked();
			}
		});

		JButton btnBack = new JButton(new ImageIcon(getClass().getResource("../resources/back_small.png")));
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
		
	}

	void setFrameSizeByBoardSize() {
		
		int width = 0;
		int height = 0;
		int difficulty = getDifficultyFromSize();
		switch (difficulty) {
		case 0:  {
			width = EASY_PANEL_WIDTH;
			height = EASY_PANEL_HEIGHT;
			break;
		}
		case 1: {
			width = MED_PANEL_WIDTH;
			height = MED_PANEL_HEIGHT;
			break;
		}
		case 2: {
			width = HARD_PANEL_WIDTH;
			height = HARD_PANEL_HEIGHT;
		}
		default: {
			Logger.writeErrorToLog("invalid board size, cannot resize window accordingly");
		}
		}
		
		MainView.getView().getFrame().setMinimumSize(new Dimension(width,height));
		MainView.getView().getFrame().setPreferredSize(new Dimension(width, height));
		
	}
	
	private int getDifficultyFromSize() {
		switch (size) {
		case 8:  {
			return 0;
		}
		case 11: {
			return 1;
		}
		case 13: {
			return 2;
		}
		default:
			return -1;
	}
	}

	private String[][] getHighScores() throws SQLException {
		return DBUtils.getTenBestScores();	
	}

	void drawBoard(PuzzleSquare[][] board, List<PuzzleDefinition> definitions) {
		this.definitions = definitions; // 

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
					if (isPlainSquare(board,j,i)) { // special empty square, relevant in some templates ([col][row]
						square = new PlainSquare(i,j);
					}
					else  {
						SquareTextField txtLbl = new SquareTextField();
						txtLbl.addKeyListener(new CrosswordKeyListener());
						sqaureTextFieldList.add(txtLbl);
						square =  new InputSquare(txtLbl, i, j); 
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

	}

	/*
	 * upon intialization, add HintPopups to all definitions
	 */
	private void addPopupMenusToDefinitions() {
		definitionToHintPopupMap = new HashMap<JDefinitionLabel, HintPopupMenu>();

		//add popups to definitionLabels
		for (JDefinitionLabel lbl : definitionLabelList) {
			HintPopupMenu popup = new HintPopupMenu(lbl, lbl.getDef().getEntityId(), hintCounterLabel);
			definitionToHintPopupMap.put(lbl, popup);

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

		return new JDefinitionLabel( boardDefs.get(i).get(j).get(defNum)); 

	}

	private boolean isPlainSquare(PuzzleSquare[][] board, int row, int col) {
		return (!board[row][col].isLetter() && boardDefCount[col][row] == 0);
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

	/**
	 * this listener contains coloring logic
	 * @author yonatan
	 *
	 */
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

	void pause() {
		if (!isPaused) { // release => pause
			timer.pause();
			isPaused = true;
			btnPause.setText("Resume");

			setBoardEnabled(false);

		}
		else { // pause => release
			timer.resume();
			isPaused = false;
			btnPause.setText("Pause");

			setBoardEnabled(true);


		}
	}
	private void setBoardEnabled(boolean enabled) {
		// make definitions disabled
		for (JDefinitionLabel lbl : definitionLabelList) {
			lbl.setEnabled(enabled);
			if (!enabled) // remove popup from label
				lbl.setComponentPopupMenu(null);
			else 
				lbl.setComponentPopupMenu(definitionToHintPopupMap.get(lbl));
		}

		// make text square disabled & uneditable
		for (SquareTextField field : sqaureTextFieldList) {
			field.setEditable(enabled);
			field.setEnabled(enabled);
		}

		boardPanel.setEnabled(enabled);	

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
			super();
			this.setText("00");
			super.setIcon(new ImageIcon(getClass().getResource("../resources/tip_big.png")));
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

	private boolean isHighScore(String[][] bestScores, int score) {
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
		setBoardEnabled(false);
	}

	/**
	 * Squares Key Listener
	 * @author yonatan
	 *
	 */
	private class CrosswordKeyListener extends KeyAdapter{

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getSource() instanceof SquareTextField) {
				InputSquare square = (InputSquare) ((Component) e.getSource()).getParent(); // parent of SquareTextField is a RegularSquare
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

				if(newSquare != null) // arrow key was pressed
					newSquare.getComponent(0).requestFocus();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (isDone())
				btnCheck.setEnabled(true);
			else 
				btnCheck.setEnabled(false);

		}


		private boolean isDone() {
			for (SquareTextField field : sqaureTextFieldList) {
				if (field.getText().isEmpty())
					return false;
			}
			return true;		
		}

	}
}
