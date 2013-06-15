package core.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.SwingWorker;

import core.Logger;
import core.PuzzleCreator;

import db.utils.DBUtils;
import ui.CrosswordView;
import ui.MainView;
import ui.Utils;
import ui.WaitView;

/**
 * This worker executes the board creation algorithm. The return value is the finished board. <br>
 * To know if an exception was thrown during the creation, use the result.getResultException() method. 
 * If the board was successfully created, the method will return null, else it will return an exception
 *
 */
public class AlgorithmWorker extends SwingWorker<BoardSolution, String> {
	// System.getProperty("file.separator")
	protected PuzzleSquare[][] board;
	protected List<PuzzleDefinition> definitions;
	private List<PuzzleDefinition> unSolved;
	protected List<Answer> answers;
	protected Set<Integer> usedEntities;
	private boolean success = false;
	long start;
	int template;

	private int[] topicsIds;
	private int difficulty;
	private WaitView view; // parent window which activated this thread

	public AlgorithmWorker(WaitView view, int[] topics, int difficulty) {
		this.topicsIds = topics;
		this.difficulty = difficulty;
		this.view = view;
		this.answers = new ArrayList<Answer>();
		this.unSolved = new ArrayList<PuzzleDefinition>();
		this.definitions = new ArrayList<PuzzleDefinition>();
		this.usedEntities = new HashSet<Integer>();
		this.template = 1;
		this.start = 0;

	}

	@Override
	protected BoardSolution doInBackground() {
		BoardSolution result = null;
		try {
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

			publish("Retrieving possible answers from DataBase...");
			answers = DBUtils.getPossibleAnswers(this.topicsIds, 11);
			Logger.writeToLog("Number of answers = " + answers.size());

			publish("Creating puzzle board...");
			createBoardFromTemplateFile(size, template);
			Collections.sort(definitions);
			printBoard();
			printTopics();
			printBoardStatus();
			Logger.writeToLog("Optimizing board");
			optimizeBoard();
			printBoardStatus();
			publish("Sorting answers on board...");
			start = System.currentTimeMillis();
			while (System.currentTimeMillis() - start < 60000) {
				success = fillBoard(System.currentTimeMillis());
				if (!success && (template == 1)) {
					template = 2;
					Logger.writeErrorToLog("impossible data for template 1");

					createBoardFromTemplateFile(size, template);
					Collections.sort(definitions);
					Logger.writeToLog("Optimizing board");
					optimizeBoard();

				} else if (!success && (template == 2)) {
					Logger.writeErrorToLog("impossible data for template 2");
					publish("failed to create Puzzle");
					break;

				} else {
					Logger.writeToLog("success");
					publish("Retrieving hints and definitions from DataBase...");
					DBUtils.setHintsAndDefinitions(definitions);
					result = new BoardSolution(board, definitions, true, null);
					printResults();
					publish("Finished!");
					return result;
				}
			}
			result = new BoardSolution(null, null, false, new Exception(
					"not enough answers. Please choose another topic"));
		} catch (Exception ex) {
			result = new BoardSolution(board, definitions, true, ex);
			Logger.writeErrorToLog("exception thrown in algorithm "
					+ ex.getMessage());
			Logger.writeErrorToLog("" + ex.getStackTrace());
		}
		return result;
	}

	/**
	 * This method is called when the algorithm is finished
	 * The result is retrieved by calling get()
	 * If an exception was thrown during board creation, it is passed as a member of result.
	 * 
	 */
	@Override
	protected void done() {
		try {
			BoardSolution result = get();
			if (result.getResultException() != null) {
				Exception ex = result.getResultException();
				if (ex instanceof SQLException) {
					Utils.showDBConnectionErrorMessage();
				}
				if (ex instanceof IOException) {
					Utils.showMessageAndRestart("Could not find board templates. Verify their locations and restart.");
				}
			} else {
				CrosswordView crosswordView = (CrosswordView) CrosswordView
						.start(result);
				MainView.getView().setCrosswordView(crosswordView); // adds
																	// JPanel to
																	// MainView
																	// card
				if (success)
					view.setGoBtn(true);
				else {
					// not enough answers, refer to prepaere game view
					Utils.showMessage("Not enough information. Choose more topics and try again.");
					MainView.getView().showPrepareView();
				}
			}
		} catch (Exception ex) {
			Logger.writeErrorToLog("algorithm was interrupted before board was finished");

		}
	}

	@Override
	public void process(List<String> messages) {
		view.setProgressMessage(messages.get(messages.size() - 1));
	}

	private boolean fillBoard(long runStart) {
		Deque<BoardState> stack = new ArrayDeque<BoardState>();
		boolean solved = false;
		unSolved.addAll(definitions);
		Collections.sort(unSolved);

		outerLoop: while (!solved) {
			if (System.currentTimeMillis() - runStart > 30000) {
				return false;
			}
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
				int index = (int) Math.floor(Math.random()
						* possibleAnswers.size());
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
					if (!board[column][row].checkLetter(currentAnswer
							.getAnswerString().charAt(letterIndex), false)) {
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

	private void optimizeBoard() {
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
	private boolean pushBoardState(Deque<BoardState> stack,
			PuzzleDefinition lastDef, Answer currentAnswer) {
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
				Logger.writeErrorToLog("unknow direction '"
						+ def.getDirection() + "'");
				return false;
			}
		}

		stack.push(bs);
		return true;
	}

	private boolean popBoardState(Deque<BoardState> stack) {
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

	private void updateUnSolved() {
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

	private void insertAnswer(PuzzleDefinition def, Answer currentAnswer) {

		int row = def.getBeginRow();
		int column = def.getBeginColumn();
		for (int letterIndex = 0; letterIndex < currentAnswer.length; letterIndex++) {
			char direction = def.getDirection();
			board[column][row].checkLetter(currentAnswer.getAnswerString()
					.charAt(letterIndex), true);
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
	private boolean insertDefinition(int beginRow, int beginCol, int length,
			char direction, int textRow, int textCol) {
		PuzzleDefinition def = new PuzzleDefinition(textRow, textCol, beginRow,
				beginCol, length, direction, this);
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

	private void printResults() {
		printBoard();
		for (PuzzleDefinition def : definitions) {
			Logger.writeToLog("def of length " + def.getLength()
					+ " answer is :" + def.getAnswer().getAnswerString());
		}

	}

	private void printBoard() {
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

	private void printBoardStatus() {
		int counter = 0;
		Set<Integer> lengths = new HashSet<Integer>();
		for (PuzzleDefinition def : definitions) {
			if (!lengths.contains(def.getLength())) {
				Logger.writeToLog("def length: " + def.getLength()
						+ " num of answers :" + def.getPossibleAnswers().size());
				counter += def.getPossibleAnswers().size();
				lengths.add(def.getLength());
			}
		}
		Logger.writeToLog("Total number of possible answers = " + counter);
	}

	private void printTopics() {
		String topicsString = "Topics Ids: ";
		for (int i = 0; i < topicsIds.length; i++) {
			topicsString += topicsIds[i] + " ,";
		}
		Logger.writeToLog(topicsString);

	}

	/**
	 * This method creates new puzzle squares and new puzzle definitions for the
	 * board, according to the template number
	 * 
	 * @param size
	 * @param templateNum
	 * @return
	 */
	private boolean createBoardFromTemplateFile(int size, int templateNum)
			throws IOException {
		board = new PuzzleSquare[size][size];
		this.unSolved.clear();
		this.definitions.clear();
		this.usedEntities.clear();
		String fileName = "" + size + "x" + size + "_" + templateNum + ".tmp";
		File templateFile = new File(PuzzleCreator.appDir + "templates",
				fileName);

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
				textRowValue = line.substring(textRowIndex + 8,
						textRowIndex + 10);
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

		return true;
	}

}
