package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import Utils.AlgorithmUtils;
import Utils.Logger;

import puzzleAlgorithm.AlgorithmRunner;
import puzzleAlgorithm.BoardSolution;
import puzzleAlgorithm.PuzzleDefinition;
import puzzleAlgorithm.PuzzleSquare;
import sun.applet.Main;
import sun.security.krb5.internal.PAEncTSEnc;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
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

	private TimerJLabel timer;
	private JButton btnPause;
	private boolean isPaused = false;
	private JPanel boardPanel;
	private Map<Integer, Map<Integer,List<PuzzleDefinition>>> boardDefs;
	private AbstractSquarePanel[][] boardPanelHolders;

	JPanel[][] getBoardPanelHolders() {
		return boardPanelHolders;
	}
	
	List<PuzzleDefinition> getDefinitions() {
		return definitions;
	}

	private int[][] boardDefCount;
	List<PuzzleDefinition> definitions;
	private JButton btnCheck;
	private int size;

	private List<JDefinitionLabel> definitionLabelList; //keeping all definition labels 
	private List<JSquareTextField> sqaureTextFieldList;


	static JPanel start() {
		CrosswordView view = new CrosswordView();
		@SuppressWarnings("unused")
		CrosswordController controller = new CrosswordController(null, view);
		return view;
	}
	/**
	 * Create the frame.
	 */
	public CrosswordView() {
		initialize();
		this.setVisible(true);
	}

	private void initialize() {
		
		setSizes();
		setLayout(new BorderLayout(0, 0));

		JPanel timerPanel = new JPanel();

		timer = new TimerJLabel();
		timerPanel.add(timer);
		timer.start();
		add(timerPanel, BorderLayout.NORTH);

		boardPanel = new JPanel();
		add(boardPanel, BorderLayout.CENTER);

		JPanel BtnPanel = new JPanel();
		add(BtnPanel, BorderLayout.SOUTH);

		btnCheck = new JButton("Check");

		btnPause = new JButton("Pause");
		btnPause.setPreferredSize(new Dimension(100, btnPause.getPreferredSize().height + 10));
		BtnPanel.add(btnPause);
		BtnPanel.add(btnCheck);

		JButton btnDone = new JButton("Done");
		BtnPanel.add(btnDone);

		CrosswordModel.getBoardSolutionAndDraw(this);

	}

	void setSizes() {
		MainView.view.frame.setMinimumSize(new Dimension(1300,1000));
		MainView.view.frame.setPreferredSize(new Dimension(1300,1000));
	}
	void drawBoard(PuzzleSquare[][] board, List<PuzzleDefinition> definitions) {
		this.definitions = definitions; // save a reference in crossview

		size = board.length;
		boardPanelHolders = new AbstractSquarePanel[size][size];
		boardDefCount = new int[size][size];
		boardDefs =  new HashMap<Integer, Map<Integer, List<PuzzleDefinition>>>(); // Map required because cannot make such an array
		definitionLabelList = new ArrayList<JDefinitionLabel>();
		sqaureTextFieldList = new ArrayList<JSquareTextField>();

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
					JSquareTextField txtLbl = new JSquareTextField();
					boardPanelHolders[i][j] = new RegularSquare(txtLbl, i, j);
					sqaureTextFieldList.add(txtLbl);
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
				boardPanel.add(boardPanelHolders[i][j]);
			}
		}

		addDefinitionSquareListenerToSquares(new DefinitionSquareListener()); // add dynamically the definitions listeners.

		boardPanel.repaint();	
	}

	private void initializeBoardDefCount(int size) {
		for (int i = 0; i<size; i++) {
			for (int j=0; j<size; j++) {
				boardDefCount[i][j] = 0;
			}
		}
	}
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

	private void drawArrows() {

	}

	private boolean isDefinitionTop(PuzzleDefinition def, int row) {
		return (def.getBeginRow() == row - 1);
	}

	private boolean isDefinitionBottom(PuzzleDefinition def, int row) {
		return (def.getBeginRow() == row + 1);
	}

	private JDefinitionLabel createDefinitionLabel(int i,int j, int defNum) {
		//JLabel lbl = new JLabel("<html><p>" + boardDefs.get(i).get(j).get(defNum) + "</p></html>");
		return new JDefinitionLabel( boardDefs.get(i).get(j).get(defNum)); 
	}

	private void colorDefinitionArea(PuzzleDefinition def, Color color,Color caretColor) {

		char direction = def.getDirection();
		switch (direction) {
		case 'r': {
			for (int col = def.getBeginColumn(); col<def.getBeginColumn() + def.getAnswer().length; col++) {
				boardPanelHolders[def.getBeginRow()][col].getComponent(0).setBackground(color);
				((JSquareTextField)boardPanelHolders[def.getBeginRow()][col].getComponent(0)).setCaretColor(caretColor);
			}
			break;
		}
		case 'l':{
			for (int col = def.getBeginColumn(); col>def.getBeginColumn() - def.getAnswer().length; col--) {
				boardPanelHolders[def.getBeginRow()][col].getComponent(0).setBackground(color);
				((JSquareTextField)boardPanelHolders[def.getBeginRow()][col].getComponent(0)).setCaretColor(caretColor);
			}
			break;
		}
		case 'u': {
			for (int row = def.getBeginRow(); row>def.getBeginRow() - def.getAnswer().length; row--) {
				boardPanelHolders[row][def.getBeginColumn()].getComponent(0).setBackground(color);
				((JSquareTextField)boardPanelHolders[row][def.getBeginColumn()].getComponent(0)).setCaretColor(caretColor);
			}
			break;
		}
		case 'd': {
			for (int row = def.getBeginRow(); row<def.getBeginRow() + def.getAnswer().length; row++) {
				boardPanelHolders[row][def.getBeginColumn()].getComponent(0).setBackground(color);
				((JSquareTextField)boardPanelHolders[row][def.getBeginColumn()].getComponent(0)).setCaretColor(caretColor);
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

	class DefinitionSquareListener extends MouseAdapter { // had to put it here because definitions List does not exist at view & controller initialize, and didnt want to have controller refernce in this class
		private Color COLOR = Color.BLUE;
		private Color BACKGROUNDCOLOR = Color.BLACK;
		private Color origBackgroundColor;

		@Override
		public void mouseEntered(MouseEvent e) {
			JDefinitionLabel lbl =(JDefinitionLabel) e.getSource();
			origBackgroundColor = lbl.getBackground();
			lbl.setBackground(BACKGROUNDCOLOR);
			addColorDefinitionArea(lbl.getDef(), COLOR);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			JDefinitionLabel lbl =(JDefinitionLabel) e.getSource();
			setBackground(origBackgroundColor);
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

	void addCheckListener(ActionListener listener) {
		btnCheck.addActionListener(listener);
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
			enableComponents(boardPanel, false);
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
			enableComponents(boardPanel, true);
			btnPause.setText("Pause");
			addDefinitionSquareListenerToSquares(new DefinitionSquareListener()); // return the definition square listener to all definition squares
			boardPanel.setEnabled(true);
		}
	}

	void enableComponents(Container container, boolean enable) {
		Component[] components = container.getComponents();
		for (Component component : components) {
			component.setEnabled(enable);
			if (component instanceof Container) {
				enableComponents((Container)component, enable);
			}
		}
	}

	void addPauseListener(ActionListener listener) {
		btnPause.addActionListener(listener);
	}
}
