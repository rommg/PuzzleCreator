package gui;

import javax.swing.JPanel;

import Utils.AlgorithmUtils;
import Utils.Logger;
import puzzleAlgorithm.AlgorithmRunner;
import puzzleAlgorithm.BoardSolution;
import puzzleAlgorithm.PuzzleDefinition;

public class CrosswordModel {

	private static BoardSolution getBoard() {
		return AlgorithmUtils.createPuzzle(null, null); // right now no need to pass anything, only mockup
	}

	private static void draw(CrosswordView view, BoardSolution solution) {
		view.drawBoard(solution.getBoard(), solution.getDefinitions());
	}

	static void getBoardSolutionAndDraw(CrosswordView view) {
		BoardSolution solution = getBoard();
		if (solution.isFound())
			draw(view, solution);
	}

	static boolean isCorrect(CrosswordView view) {
		JPanel[][] boardPanelHolders = view.getBoardPanelHolders();
		boolean result = true;
		char[] answer;
		int	indexInAnswer;
		
		for (PuzzleDefinition def : view.getDefinitions()) { // iterate each definition to check if the letters in its domain are correct
			indexInAnswer = 0;
			answer = def.getAnswer().getAnswerString().toCharArray(); // turn correct answer to char array
			char direction = def.getDirection();
			switch (direction) { // iterate the specific definition domain
			case 'r': {
				for (int col = def.getBeginColumn(); col<def.getBeginColumn() + def.getAnswer().length; col++) {
					// compare the letter in the JSqaureTextField with the correct letter
					char letter = ((JSquareTextField)boardPanelHolders[def.getBeginRow()][col].getComponent(0)).getText().trim().charAt(0); // there should be only one letter 
					result &= (letter == answer[indexInAnswer]);
				}
				break;
			}
			case 'l':{
				for (int col = def.getBeginColumn(); col>def.getBeginColumn() - def.getAnswer().length; col--) {
					char letter = ((JSquareTextField)boardPanelHolders[def.getBeginRow()][col].getComponent(0)).getText().trim().charAt(0); 
					result &= (letter == answer[indexInAnswer]);
				}
				break;
			}
			case 'u': {
				for (int row = def.getBeginRow(); row>def.getBeginRow() - def.getAnswer().length; row--) {
					char letter = ((JSquareTextField)boardPanelHolders[row][def.getBeginColumn()].getComponent(0)).getText().trim().charAt(0); 
					result &= (letter == answer[indexInAnswer]);
				}
				break;
			}
			case 'd': {
				for (int row = def.getBeginRow(); row<def.getBeginRow() + def.getAnswer().length; row++) {
					char letter = ((JSquareTextField)boardPanelHolders[row][def.getBeginColumn()].getComponent(0)).getText().trim().charAt(0); 
					result &= (letter == answer[indexInAnswer]);
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
}
