package puzzleAlgorithm;

import gui.CrosswordView;
import gui.MainView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import utils.AlgorithmUtils;
import utils.DBUtils;
import utils.Logger;

public class AlgorithmRunner {
	// System.getProperty("file.separator")
	protected static PuzzleSquare[][] board;
	protected static List<PuzzleDefinition> definitions = new ArrayList<PuzzleDefinition>();
	private static List<PuzzleDefinition> unSolved = new ArrayList<PuzzleDefinition>();
	protected static List<Answer> answers = new ArrayList<Answer>();

	/**
	 * 
	 * @param topics - an array of id numbers of puzzle topics
	 * @param difficulty - integer for easy, medium or hard
	 * @return
	 */
	public static BoardSolution runAlgorithm(int[] topics, int difficulty) {
		BoardSolution result = null;

		//TODO remove use of mock function after tests and mock max length
		int maxLength = 8;
		//createMockAnswers();
		answers = DBUtils.getPossibleAnswers(topics, 8);
		
		Logger.writeToLog("Number of answers = " + answers.size());

		board = createBoard(13);
		insertDefinitions();
		Collections.sort(definitions);
		printBoard();
		printBoardStatus();
		optimizeBoard();
		printBoardStatus();

		if (!fillBoard()) {
			Logger.writeErrorToLog("impossible data");
			result = new BoardSolution(null,null,false);
		} else {
			DBUtils.setHintsAndDefinitions(definitions);
			result = new BoardSolution(board, definitions, true);
			//AlgorithmUtils.drawBoard(board, definitions);
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
		// TODO when poping from stack, remove the answer of lastDef from it's
		// answers
		int size = board[0].length;
		BoardState bs = new BoardState(size);
		List<PuzzleDefinition> clonedDefinitions = bs.getDefinitions();
		PuzzleSquare[][] clonedBoard = bs.getBoard();

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

		// reset definitions
		definitions.clear();
		definitions.addAll(bs.getDefinitions());
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
		// printBoard();

	}

	private static void insertDefinitions() {
		int row = 0;

		insertDefinition(row, 1, 4, 'd', 0, 0);

		insertDefinition(row, 3, 5, 'd', 0, 2);

		insertDefinition(row, 5, 3, 'd', 0, 6);

		insertDefinition(row, 8, 7, 'd', 0, 7);

		insertDefinition(row, 10, 3, 'd', 0, 9);

		insertDefinition(row, 12, 3, 'd', 0, 11);

		row = 1;

		insertDefinition(row, 0, 6, 'r', 0,0);

		insertDefinition(row, 4, 4, 'd');

		insertDefinition(row, 7, 6, 'r');

		insertDefinition(row, 7, 3, 'd');

		insertDefinition(row, 11, 4, 'd');

		row = 2;

		insertDefinition(row, 3, 6, 'r');

		insertDefinition(row, 6, 4, 'd');

		insertDefinition(row, 10, 3, 'r');

		row = 3;

		insertDefinition(row, 0, 5, 'r', 2, 0);

		insertDefinition(row, 2, 5, 'd');

		insertDefinition(row, 6, 4, 'r');

		insertDefinition(row, 9, 6, 'd');

		row = 4;

		insertDefinition(row, 2, 5, 'r');

		insertDefinition(row, 5, 5, 'd');

		insertDefinition(row, 8, 5, 'r');

		insertDefinition(row, 10, 4, 'd');

		insertDefinition(row, 12, 4, 'd');

		row = 5;

		insertDefinition(row, 0, 3, 'r', 4, 0);

		insertDefinition(row, 1, 6, 'd');

		insertDefinition(row, 5, 6, 'r');

		row = 6;

		insertDefinition(row, 1, 3, 'r');

		insertDefinition(row, 3, 3, 'd');

		insertDefinition(row, 8, 5, 'r');

		insertDefinition(row, 11, 3, 'd');

		row = 7;

		insertDefinition(row, 0, 8, 'r', 6, 0);

		insertDefinition(row, 4, 6, 'd');

		insertDefinition(row, 6, 3, 'd');

		insertDefinition(row, 7, 4, 'd');

		insertDefinition(row, 9, 4, 'r');

		row = 8;

		insertDefinition(row, 3, 5, 'r');

		row = 9;

		insertDefinition(row, 0, 3, 'r', 8, 0);

		insertDefinition(row, 2, 4, 'd');

		insertDefinition(row, 6, 3, 'r');

		insertDefinition(row, 8, 4, 'd');

		insertDefinition(row, 10, 4, 'd');

		insertDefinition(row, 12, 4, 'd');

		row = 10;

		insertDefinition(row, 1, 5, 'r');

		insertDefinition(row, 3, 3, 'd');

		insertDefinition(row, 5, 3, 'd');

		insertDefinition(row, 7, 6, 'r');

		insertDefinition(row, 9, 3, 'd');

		insertDefinition(row, 11, 3, 'd');

		row = 11;

		insertDefinition(row, 2, 5, 'r');

		insertDefinition(row, 8, 5, 'r');

		row = 12;

		insertDefinition(row, 0, 6, 'r', 11, 0);

		insertDefinition(row, 7, 6, 'r');

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

	/**
	 * This function calculates the location of the definition text, and than
	 * calls insertDefinition with the calculated params
	 * 
	 * @param beginRow
	 * @param beginCol
	 * @param length
	 * @param direction
	 * @return
	 */
	private static boolean insertDefinition(int beginRow, int beginCol, int length, char direction) {
		int textRow;
		int textCol;
		switch (direction) {
		case 'r':
			if (beginCol < 1) {
				Logger.writeErrorToLog("defintion text out of board. col=" + beginCol + " row=" + beginRow
						+ " direction=" + direction);
				return false;
			}
			textRow = beginRow;
			textCol = beginCol - 1;
			break;
		case 'd':
			if (beginRow < 1) {
				Logger.writeErrorToLog("defintion text out of board. col=" + beginCol + " row=" + beginRow
						+ " direction=" + direction);
				return false;
			}
			textRow = beginRow - 1;
			textCol = beginCol;
			break;
		default:
			Logger.writeErrorToLog("unknow direction '" + direction + "'");
			return false;
		}
		insertDefinition(beginRow, beginCol, length, direction, textRow, textCol);
		return true;
	}

	private static PuzzleSquare[][] createBoard(int size) {

		PuzzleSquare[][] board = new PuzzleSquare[size][size];
		// create specific example http://www.puzzlechoice.com/cw/Arrow03x.html
		int row = 0;
		for (int i = 0; i < 7; i++) {
			if (i % 2 == 0) {
				board[i][row] = new PuzzleSquare(false, i, row);
			} else {
				board[i][row] = new PuzzleSquare(true, i, row);
			}
		}
		for (int i = 7; i < size; i++) {
			if (i % 2 == 0) {
				board[i][row] = new PuzzleSquare(true, i, row);
			} else {
				board[i][row] = new PuzzleSquare(false, i, row);
			}
		}

		row = 1;

		for (int i = 0; i < size; i++) {
			if (i == 6) {
				board[i][row] = new PuzzleSquare(false, i, row);
			} else {
				board[i][row] = new PuzzleSquare(true, i, row);
			}
		}

		row = 2;

		for (int i = 0; i < size; i++) {
			if (i == 0 || i == 2 || i == 9) {
				board[i][row] = new PuzzleSquare(false, i, row);
			} else {
				board[i][row] = new PuzzleSquare(true, i, row);
			}
		}

		row = 3;

		for (int i = 0; i < size; i++) {
			if (i == 5 || i == 10 || i == 12) {
				board[i][row] = new PuzzleSquare(false, i, row);
			} else {
				board[i][row] = new PuzzleSquare(true, i, row);
			}
		}
		row = 4;

		for (int i = 0; i < size; i++) {
			if (i == 0 || i == 1 || i == 7) {
				board[i][row] = new PuzzleSquare(false, i, row);
			} else {
				board[i][row] = new PuzzleSquare(true, i, row);
			}
		}

		row = 5;

		for (int i = 0; i < size; i++) {
			if (i == 3 || i == 4 || i == 11) {
				board[i][row] = new PuzzleSquare(false, i, row);
			} else {
				board[i][row] = new PuzzleSquare(true, i, row);
			}
		}

		row = 6;

		for (int i = 0; i < size; i++) {
			if (i == 0 || i == 4 || i == 6 || i == 7) {
				board[i][row] = new PuzzleSquare(false, i, row);
			} else {
				board[i][row] = new PuzzleSquare(true, i, row);
			}
		}

		row = 7;

		for (int i = 0; i < size; i++) {
			if (i == 8) {
				board[i][row] = new PuzzleSquare(false, i, row);
			} else {
				board[i][row] = new PuzzleSquare(true, i, row);
			}
		}

		row = 8;

		for (int i = 0; i < size; i++) {
			if (i == 0 || i == 2 || i == 8 || i == 10 || i == 12) {
				board[i][row] = new PuzzleSquare(false, i, row);
			} else {
				board[i][row] = new PuzzleSquare(true, i, row);
			}
		}

		row = 9;

		for (int i = 0; i < size; i++) {
			if (i == 3 || i == 5 || i == 9 || i == 11) {
				board[i][row] = new PuzzleSquare(false, i, row);
			} else {
				board[i][row] = new PuzzleSquare(true, i, row);
			}
		}

		row = 10;

		for (int i = 0; i < size; i++) {
			if (i == 0 || i == 6) {
				board[i][row] = new PuzzleSquare(false, i, row);
			} else {
				board[i][row] = new PuzzleSquare(true, i, row);
			}
		}

		row = 11;

		for (int i = 0; i < size; i++) {
			if (i == 0 || i == 1 || i == 7) {
				board[i][row] = new PuzzleSquare(false, i, row);
			} else {
				board[i][row] = new PuzzleSquare(true, i, row);
			}
		}

		row = 12;

		for (int i = 0; i < size; i++) {
			if (i == 6) {
				board[i][row] = new PuzzleSquare(false, i, row);
			} else {
				board[i][row] = new PuzzleSquare(true, i, row);
			}
		}

		return board;
	}

	/**
	 * This method checks if param word contains only english letters
	 * 
	 * @param word
	 *            - must be in lower case only
	 * @return
	 */
	private static boolean checkWordLetters(String word) {
		for (int letterIndex = 0; letterIndex < word.length(); letterIndex++) {
			if (!(word.charAt(letterIndex) >= 'a' && word.charAt(letterIndex) <= 'z')) {
				return false;
			}
		}
		return true;
	}

	// private static boolean readAnswersFile(String path) {
	// Logger.writeToLog("Reading answers file");
	// answers = new ArrayList<String>();
	// try {
	// FileReader in = new FileReader(path);
	// BufferedReader bin = new BufferedReader(in);
	// String line = bin.readLine();
	// String first;
	// String second;
	// String full;
	//
	// while (line != null) {
	// if (line.indexOf('.') != -1 || line.indexOf('-') != -1) {
	// line = bin.readLine();
	// continue;
	// }
	// int index = line.indexOf('_');
	// if (index != -1) {
	// first = line.substring(0, index);
	// second = line.substring(index + 1);
	// full = first + second;
	// if (checkWordLetters(first.toLowerCase()))
	// answers.add(first.toLowerCase());
	// if (checkWordLetters(second.toLowerCase()))
	// answers.add(second.toLowerCase());
	// if (checkWordLetters(full.toLowerCase()))
	// answers.add(full.toLowerCase());
	// } else {
	// if (checkWordLetters(line.toLowerCase()))
	// answers.add(line.toLowerCase());
	// }
	// line = bin.readLine();
	// }
	//
	// bin.close();
	// in.close();
	//
	// } catch (Exception ex) {
	// System.out.println(ex.getMessage());
	// return false;
	// }
	// Logger.writeToLog("Finished reading answersr. Number of answers = " +
	// answers.size());
	// return true;
	// }

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

	private static void createMockAnswers(){
		Answer ans = new Answer("oboe", -1);
		answers.add(ans);
		ans = new Answer("callow", -1);
		answers.add(ans);
		ans = new Answer("eject", -1);
		answers.add(ans);
		ans = new Answer("arid", -1);
		answers.add(ans);
		ans = new Answer("noise", -1);
		answers.add(ans);
		ans = new Answer("tic", -1);
		answers.add(ans);
		ans = new Answer("one", -1);
		answers.add(ans);
		ans = new Answer("anew", -1);
		answers.add(ans);
		ans = new Answer("trance", -1);
		answers.add(ans);
		ans = new Answer("odd", -1);
		answers.add(ans);
		ans = new Answer("ensue", -1);
		answers.add(ans);
		ans = new Answer("gum", -1);
		answers.add(ans);
		ans = new Answer("idle", -1);
		answers.add(ans);
		ans = new Answer("den", -1);
		answers.add(ans);
		ans = new Answer("age", -1);
		answers.add(ans);
		ans = new Answer("epic", -1);
		answers.add(ans);
		ans = new Answer("profess", -1);
		answers.add(ans);
		ans = new Answer("liar", -1);
		answers.add(ans);
		ans = new Answer("tropic", -1);
		answers.add(ans);
		ans = new Answer("cud", -1);
		answers.add(ans);
		ans = new Answer("ebb", -1);
		answers.add(ans);
		ans = new Answer("unit", -1);
		answers.add(ans);
		ans = new Answer("fade", -1);
		answers.add(ans);
		ans = new Answer("leap", -1);
		answers.add(ans);
		ans = new Answer("new", -1);
		answers.add(ans);
		ans = new Answer("din", -1);
		answers.add(ans);
		ans = new Answer("fee", -1);
		answers.add(ans);
		ans = new Answer("teem", -1);
		answers.add(ans);
		ans = new Answer("watt", -1);
		answers.add(ans);
		ans = new Answer("abroad", -1);
		answers.add(ans);
		ans = new Answer("arable", -1);
		answers.add(ans);
		ans = new Answer("indigo", -1);
		answers.add(ans);
		ans = new Answer("bee", -1);
		answers.add(ans);
		ans = new Answer("geese", -1);
		answers.add(ans);
		ans = new Answer("deft", -1);
		answers.add(ans);
		ans = new Answer("jewel", -1);
		answers.add(ans);
		ans = new Answer("erupt", -1);
		answers.add(ans);
		ans = new Answer("ace", -1);
		answers.add(ans);
		ans = new Answer("nelson", -1);
		answers.add(ans);
		ans = new Answer("act", -1);
		answers.add(ans);
		ans = new Answer("spine", -1);
		answers.add(ans);
		ans = new Answer("altitude", -1);
		answers.add(ans);
		ans = new Answer("item", -1);
		answers.add(ans);
		ans = new Answer("creep", -1);
		answers.add(ans);
		ans = new Answer("boa", -1);
		answers.add(ans);
		ans = new Answer("nil", -1);
		answers.add(ans);
		ans = new Answer("wrong", -1);
		answers.add(ans);
		ans = new Answer("cicada", -1);
		answers.add(ans);
		ans = new Answer("incur", -1);
		answers.add(ans);
		ans = new Answer("audit", -1);
		answers.add(ans);
		ans = new Answer("redeem", -1);
		answers.add(ans);
		ans = new Answer("ardent", -1);
		answers.add(ans);
		
	}
}
