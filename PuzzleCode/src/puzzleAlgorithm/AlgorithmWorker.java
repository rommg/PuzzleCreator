package puzzleAlgorithm;

import gui.MainView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import main.PuzzleCreator;

import utils.DBUtils;
import utils.Logger;

public class AlgorithmWorker extends SwingWorker<BoardSolution, String> {
	// System.getProperty("file.separator")
	protected static PuzzleSquare[][] board;
	protected static List<PuzzleDefinition> definitions = new ArrayList<PuzzleDefinition>();
	private static List<PuzzleDefinition> unSolved = new ArrayList<PuzzleDefinition>();
	protected static List<Answer> answers = new ArrayList<Answer>();
	protected static Set<Integer> usedEntities = new HashSet<Integer>();

	private int[] topics;
	private int difficulty;

	public AlgorithmWorker(int[] topics, int difficulty) {
		this.topics = topics;
		this.difficulty = difficulty;
	}

	@Override
	protected BoardSolution doInBackground() throws Exception {
		int[] topics = { 3, 4, 5 };
		BoardSolution result = null;
		int size = 13;
		switch (difficulty) {
		case 0:
			size = 8;
			break;
		case 1:
			size = 11;
			break;
		case 2:
			size = 13;
			break;
		default:
			break;
		}
		
		publish("Retrieving possible answers from DataBase");
		// TODO remove use of mock function after tests
//		createMockAnswers();
		answers = DBUtils.getPossibleAnswers(topics, 10);
		Logger.writeToLog("Number of answers = " + answers.size());
		
		publish("Creating puzzle board");
		createBoardFromTemplateFile(size, 1);
		Collections.sort(definitions);
		printBoard();
		printBoardStatus();
		optimizeBoard();
		printBoardStatus();
		publish("Sorting answers on board");
		if (!fillBoard()) {
			Logger.writeErrorToLog("impossible data");
			result = new BoardSolution(null, null, false);
		} else {
			Logger.writeToLog("success");
			publish("Retrieving hints and definitions from DataBase");
			DBUtils.setHintsAndDefinitions(definitions);
			result = new BoardSolution(board, definitions, true);
			printResults();
			publish("finished");
		}
		return result;
	}

	@Override
	protected void done() {
		try {
			MainView.view.showCrosswordview(get());
		} catch (InterruptedException e) {
			Logger.writeErrorToLog("InterruptedException in algorithm worker:");
			Logger.writeErrorToLog("" + e.getStackTrace());
		} catch (ExecutionException e) {
			Logger.writeErrorToLog("ExecutionException in algorithm worker:");
			Logger.writeErrorToLog("" + e.getStackTrace());
		}
	}
	
	@Override
	public void process(List<String> messages){
		//TODO show the messages on screen while the board is built
	}

	/**
	 * 
	 * @param topics
	 *            - an array of id numbers of puzzle topics
	 * @param difficulty
	 *            - integer for easy, medium or hard
	 * @return
	 */
	public static BoardSolution runAlgorithm(int[] topics, int difficulty) {
		BoardSolution result = null;
		int size = 13;
		// TODO remove use of mock function after tests
		// createMockAnswers();
		answers = DBUtils.getPossibleAnswers(topics, 8);

		Logger.writeToLog("Number of answers = " + answers.size());
		createBoardFromTemplateFile(size, 2);
		Collections.sort(definitions);
		printBoard();
		printBoardStatus();
		optimizeBoard();
		printBoardStatus();

		if (!fillBoard()) {
			Logger.writeErrorToLog("impossible data");
			result = new BoardSolution(null, null, false);
		} else {
			Logger.writeToLog("success");
			DBUtils.setHintsAndDefinitions(definitions);
			result = new BoardSolution(board, definitions, true);
			// AlgorithmUtils.drawBoard(board, definitions);
			printResults();
		}
		return result;
	}

	private static boolean fillBoard() {
		Deque<BoardState> stack = new ArrayDeque<BoardState>();
		boolean solved = false;
		unSolved.addAll(definitions);
		Collections.sort(unSolved);

		outerLoop: while (!solved) {

			PuzzleDefinition def = unSolved.get(0);
			List<Answer> possibleAnswers = def.getPossibleAnswers();
			innerLoop: while (!def.isSolved()) {
				if (possibleAnswers.size() == 0) {
					if (!popBoardState(stack)) {
						break outerLoop;
					}
					optimizeBoard();
					continue outerLoop;
				}
				int index = (int) Math.floor(Math.random() * possibleAnswers.size());
				Answer currentAnswer = possibleAnswers.get(index);

				/*
				 * check if this answers's entity was already used TODO The
				 * possible answers should be updated by entity id when the
				 * problematic answer is assigned
				 */
				if (usedEntities.contains(currentAnswer.getEntityId())) {
					possibleAnswers.remove(currentAnswer);
					continue innerLoop;
				}

				int row = def.getBeginRow();
				int column = def.getBeginColumn();
				char direction = def.getDirection();
				for (int letterIndex = 0; letterIndex < currentAnswer.length; letterIndex++) {
					if (!board[column][row].checkLetter(currentAnswer.getAnswerString().charAt(letterIndex), false)) {
						possibleAnswers.remove(currentAnswer);
						continue innerLoop;
					}
					switch (direction) {
					case 'r':
						column++;
						break;
					case 'd':
						row++;
						break;
					default:
						return false;
					}
				}

				// fill currentAnswer to the board
				pushBoardState(stack, def, currentAnswer);
				insertAnswer(def, currentAnswer);
			}

			if (!def.isSolved()) {
				if (!popBoardState(stack)) {
					break;
				}
				continue;
			}

			unSolved.remove(def);
			updateUnSolved();

			if (stack.size() == definitions.size())
				solved = true;

		}

		return solved;
	}

	private static void optimizeBoard() {
		int size = board[0].length;
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				board[col][row].optimizeSquare();
			}
		}

		for (PuzzleDefinition def : definitions) {
			def.optimizeDefinition();
		}
	}

	/**
	 * This method creates a board state, and pushes it to the stack The board
	 * state includes a cloned board, with all squares cloned, and cloned
	 * definitions
	 * 
	 * @param stack
	 */
	private static boolean pushBoardState(Deque<BoardState> stack, PuzzleDefinition lastDef, Answer currentAnswer) {
		int size = board[0].length;
		BoardState bs = new BoardState(size);
		List<PuzzleDefinition> clonedDefinitions = bs.getDefinitions();
		PuzzleSquare[][] clonedBoard = bs.getBoard();

		Set<Integer> stateUsedEntities = bs.getUsedEntites();
		for (int entityId : usedEntities) {
			stateUsedEntities.add(entityId);
		}

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				clonedBoard[col][row] = board[col][row].cloneSquare();
			}
		}

		for (PuzzleDefinition def : definitions) {
			if (def == lastDef) {
				PuzzleDefinition clonedLastDef = lastDef.cloneDefinition();
				clonedLastDef.setAnswer(currentAnswer);
				bs.setLastDef(clonedLastDef);
				clonedDefinitions.add(clonedLastDef);
			} else {
				clonedDefinitions.add(def.cloneDefinition());
			}
		}

		for (PuzzleDefinition def : clonedDefinitions) {
			int beginRow = def.getBeginRow();
			int beginCol = def.getBeginColumn();
			int length = def.getLength();

			switch (def.getDirection()) {
			case 'r':
				for (int col = beginCol; col < beginCol + length; col++) {
					clonedBoard[col][beginRow].addDefinition(def);
				}
				break;
			case 'd':
				for (int row = beginRow; row < beginRow + length; row++) {
					clonedBoard[beginCol][row].addDefinition(def);
				}
				break;
			default:
				Logger.writeErrorToLog("unknow direction '" + def.getDirection() + "'");
				return false;
			}
		}

		stack.push(bs);
		return true;
	}

	private static boolean popBoardState(Deque<BoardState> stack) {
		if (stack.size() == 0) {
			return false;
		}
		BoardState bs = stack.pop();
		int size = board[0].length;

		// reset board letters
		for (int col = 0; col < size; col++) {
			for (int row = 0; row < size; row++) {
				board[col][row] = bs.getBoard()[col][row];
			}
		}

		// remove the assigned answer from the last definition solved in this
		// state
		PuzzleDefinition lastDef = bs.getLastDef();
		if (!lastDef.getPossibleAnswers().remove(lastDef.getAnswer())) {
			return false;
		}
		lastDef.setAnswer(new Answer("", -1));

		// reset definitions and used entities
		definitions.clear();
		definitions.addAll(bs.getDefinitions());
		usedEntities.clear();
		usedEntities.addAll(bs.getUsedEntites());

		unSolved.clear();
		for (PuzzleDefinition def : definitions) {
			if (!def.isSolved()) {
				unSolved.add(def);
			}
		}
		Collections.sort(unSolved);
		return true;
	}

	private static void updateUnSolved() {
		List<PuzzleDefinition> newUnSolved = new ArrayList<PuzzleDefinition>();
		for (PuzzleDefinition def : unSolved) {
			if (!def.isSolved()) {
				newUnSolved.add(def);
			}
		}
		if (unSolved.size() != newUnSolved.size()) {
			unSolved.clear();
			unSolved.addAll(newUnSolved);
		}
		Collections.sort(unSolved);
	}

	private static void insertAnswer(PuzzleDefinition def, Answer currentAnswer) {

		int row = def.getBeginRow();
		int column = def.getBeginColumn();
		for (int letterIndex = 0; letterIndex < currentAnswer.length; letterIndex++) {
			char direction = def.getDirection();
			board[column][row].checkLetter(currentAnswer.getAnswerString().charAt(letterIndex), true);
			switch (direction) {
			case 'r':
				column++;
				break;
			case 'd':
				row++;
				break;
			}
		}

		def.setAnswer(currentAnswer);
		def.markSolved();
		usedEntities.add(currentAnswer.getEntityId());
		// printBoard();

	}

	
	/**
	 * Create a new definition with the function params Insert definition to
	 * board definitions collection For each relevant square - add the
	 * definition to it's definitions
	 * 
	 * 
	 * @return
	 */
	private static boolean insertDefinition(int beginRow, int beginCol, int length, char direction, int textRow,
			int textCol) {
		PuzzleDefinition def = new PuzzleDefinition(textRow, textCol, beginRow, beginCol, length, direction);
		definitions.add(def);

		switch (direction) {
		case 'r':
			for (int col = beginCol; col < beginCol + length; col++) {
				board[col][beginRow].addDefinition(def);
			}
			break;
		case 'd':
			for (int row = beginRow; row < beginRow + length; row++) {
				board[beginCol][row].addDefinition(def);
			}
			break;
		default:
			Logger.writeErrorToLog("unknow direction '" + direction + "'");
			return false;
		}

		return true;
	}



	private static void printResults() {
		printBoard();
		for (PuzzleDefinition def : definitions) {
			Logger.writeToLog("def of length " + def.getLength() + " answer is :" + def.getAnswer().getAnswerString());
		}

	}

	private static void printBoard() {
		Logger.writeToLog("Printing board:");
		for (int row = 0; row < board[0].length; row++) {
			String rowSt = "";
			for (int column = 0; column < board[0].length; column++) {
				PuzzleSquare square = board[column][row];
				if (!square.isLetter()) {
					rowSt += " # ";
				} else {
					if (square.getLetter() == 0) {
						rowSt += " - ";
					} else {
						rowSt += " " + square.getLetter() + " ";
					}
				}
			}
			Logger.writeToLog(rowSt);
			Logger.writeToLog("");

		}

	}

	private static void printBoardStatus() {
		int counter = 0;
		for (PuzzleDefinition def : definitions) {
			Logger.writeToLog("def length: " + def.getLength() + " num of answers :" + def.getPossibleAnswers().size());
			counter += def.getPossibleAnswers().size();
		}
		Logger.writeToLog("Total number of possible answers = " + counter);
	}

	private static void createMockAnswers() {
		int entityId = 0;
		Answer ans = new Answer("oboe", entityId++);
		answers.add(ans);
		ans = new Answer("callow", entityId++);
		answers.add(ans);
		ans = new Answer("eject", entityId++);
		answers.add(ans);
		ans = new Answer("arid", entityId++);
		answers.add(ans);
		ans = new Answer("noise", entityId++);
		answers.add(ans);
		ans = new Answer("tic", entityId++);
		answers.add(ans);
		ans = new Answer("one", entityId++);
		answers.add(ans);
		ans = new Answer("anew", entityId++);
		answers.add(ans);
		ans = new Answer("trance", entityId++);
		answers.add(ans);
		ans = new Answer("odd", entityId++);
		answers.add(ans);
		ans = new Answer("ensue", entityId++);
		answers.add(ans);
		ans = new Answer("gum", entityId++);
		answers.add(ans);
		ans = new Answer("idle", entityId++);
		answers.add(ans);
		ans = new Answer("den", entityId++);
		answers.add(ans);
		ans = new Answer("age", entityId++);
		answers.add(ans);
		ans = new Answer("epic", entityId++);
		answers.add(ans);
		ans = new Answer("profess", entityId++);
		answers.add(ans);
		ans = new Answer("liar", entityId++);
		answers.add(ans);
		ans = new Answer("tropic", entityId++);
		answers.add(ans);
		ans = new Answer("cud", entityId++);
		answers.add(ans);
		ans = new Answer("ebb", entityId++);
		answers.add(ans);
		ans = new Answer("unit", entityId++);
		answers.add(ans);
		ans = new Answer("fade", entityId++);
		answers.add(ans);
		ans = new Answer("leap", entityId++);
		answers.add(ans);
		ans = new Answer("new", entityId++);
		answers.add(ans);
		ans = new Answer("din", entityId++);
		answers.add(ans);
		ans = new Answer("fee", entityId++);
		answers.add(ans);
		ans = new Answer("teem", entityId++);
		answers.add(ans);
		ans = new Answer("watt", entityId++);
		answers.add(ans);
		ans = new Answer("abroad", entityId++);
		answers.add(ans);
		ans = new Answer("arable", entityId++);
		answers.add(ans);
		ans = new Answer("indigo", entityId++);
		answers.add(ans);
		ans = new Answer("bee", entityId++);
		answers.add(ans);
		ans = new Answer("geese", entityId++);
		answers.add(ans);
		ans = new Answer("deft", entityId++);
		answers.add(ans);
		ans = new Answer("jewel", entityId++);
		answers.add(ans);
		ans = new Answer("erupt", entityId++);
		answers.add(ans);
		ans = new Answer("ace", entityId++);
		answers.add(ans);
		ans = new Answer("nelson", entityId++);
		answers.add(ans);
		ans = new Answer("act", entityId++);
		answers.add(ans);
		ans = new Answer("spine", entityId++);
		answers.add(ans);
		ans = new Answer("altitude", entityId++);
		answers.add(ans);
		ans = new Answer("item", entityId++);
		answers.add(ans);
		ans = new Answer("creep", entityId++);
		answers.add(ans);
		ans = new Answer("boa", entityId++);
		answers.add(ans);
		ans = new Answer("nil", entityId++);
		answers.add(ans);
		ans = new Answer("wrong", entityId++);
		answers.add(ans);
		ans = new Answer("cicada", entityId++);
		answers.add(ans);
		ans = new Answer("incur", entityId++);
		answers.add(ans);
		ans = new Answer("audit", entityId++);
		answers.add(ans);
		ans = new Answer("redeem", entityId++);
		answers.add(ans);
		ans = new Answer("ardent", entityId++);
		answers.add(ans);

	}

	private static void outputBoard() {
		File templateFile = new File(PuzzleCreator.appDir, "13x13_1.tmp");
		try {
			templateFile.createNewFile();
			FileWriter out = new FileWriter(templateFile, true);
			BufferedWriter bout = new BufferedWriter(out);
			String boardData = "boardsize:" + board.length;
			bout.write(boardData);
			bout.newLine();
			boardData = "";
			// write squares

			for (int row = 0; row < board.length; row++) {
				for (int column = 0; column < board.length; column++) {
					boardData = "Puzzle square: column:" + column + " row:" + row + " emptySquare:"
							+ board[column][row].isLetter();
					bout.write(boardData);
					bout.newLine();
				}
			}

			bout.newLine();
			boardData = "Definitions:";
			bout.write(boardData);
			bout.newLine();

			for (PuzzleDefinition def : definitions) {
				boardData = "Definition: row:" + def.getBeginRow() + " column:" + def.getBeginColumn() + " length:"
						+ def.getLength() + " direction:" + def.getDirection() + " textRow:" + def.getTextRow()
						+ " textCol:" + def.getTextCol();
				bout.write(boardData);
				bout.newLine();
			}

			bout.close();
			out.close();

		} catch (IOException ex) {
			Logger.writeErrorToLog("failed to create");
		}
	}

	private static boolean createBoardFromTemplateFile(int size, int templateNum) {
		board = new PuzzleSquare[size][size];
		String fileName = "" + size + "x" + size + "_" + templateNum + ".tmp";
		File templateFile = new File(PuzzleCreator.appDir + System.getProperty("file.separator") + "templates",
				fileName);
		try {
			FileReader in = new FileReader(templateFile);
			BufferedReader bin = new BufferedReader(in);
			bin.readLine();
			String line = bin.readLine();
			int row;
			int column;
			boolean emptySquare;
			int columnIndex;
			int rowIndex;
			int emptySquareIndex;
			String columnValue;
			String rowValue;
			while (line.startsWith("Puzzle")) {
				columnIndex = line.indexOf("column:");
				rowIndex = line.indexOf("row:");
				emptySquareIndex = line.indexOf("emptySquare:");
				if (rowIndex - columnIndex == 10) {
					columnValue = line.substring(columnIndex + 7, columnIndex + 9);
				} else {
					columnValue = "" + line.charAt(columnIndex + 7);
				}

				if (emptySquareIndex - rowIndex == 7) {
					rowValue = line.substring(rowIndex + 4, rowIndex + 6);
				} else {
					rowValue = "" + line.charAt(rowIndex + 4);
				}

				emptySquare = line.endsWith("true");

				column = Integer.parseInt(columnValue);
				row = Integer.parseInt(rowValue);
				board[column][row] = new PuzzleSquare(emptySquare, column, row);
				line = bin.readLine();
			}

			line = bin.readLine();
			int lengthIndex;
			String lengthValue;
			int length;
			int directionIndex;
			char direction;
			int textRowIndex;
			String textRowValue;
			int textRow;
			int textColIndex;
			String textColValue;
			int textCol;

			while (line != null) {
				rowIndex = line.indexOf("row:");
				columnIndex = line.indexOf("column:");
				lengthIndex = line.indexOf("length:");
				directionIndex = line.indexOf("direction:");
				textRowIndex = line.indexOf("textRow:");
				textColIndex = line.indexOf("textCol:");

				if (columnIndex - rowIndex == 7) {
					rowValue = line.substring(rowIndex + 4, rowIndex + 6);
				} else {
					rowValue = "" + line.charAt(rowIndex + 4);
				}

				if (lengthIndex - columnIndex == 10) {
					columnValue = line.substring(columnIndex + 7, columnIndex + 9);
				} else {
					columnValue = "" + line.charAt(columnIndex + 7);
				}

				if (directionIndex - lengthIndex == 10) {
					lengthValue = line.substring(lengthIndex + 7, lengthIndex + 9);
				} else {
					lengthValue = "" + line.charAt(lengthIndex + 7);
				}

				direction = line.charAt(directionIndex + 10);

				if (textColIndex - textRowIndex == 11) {
					textRowValue = line.substring(textRowIndex + 8, textRowIndex + 10);
				} else {
					textRowValue = "" + line.charAt(textRowIndex + 8);
				}

				textColValue = line.substring(textColIndex + 8);
				row = Integer.parseInt(rowValue);
				column = Integer.parseInt(columnValue);
				length = Integer.parseInt(lengthValue);
				textRow = Integer.parseInt(textRowValue);
				textCol = Integer.parseInt(textColValue);

				insertDefinition(row, column, length, direction, textRow, textCol);
				line = bin.readLine();
			}

			bin.close();
			in.close();
		} catch (IOException ex) {
			Logger.writeErrorToLog("failed to read template file : " + fileName);
			return false;
		}
		return true;
	}

}
