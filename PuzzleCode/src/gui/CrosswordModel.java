package gui;

import javax.swing.JPanel;

import puzzleAlgorithm.AlgorithmWorker;
import puzzleAlgorithm.BoardSolution;
import puzzleAlgorithm.PuzzleDefinition;
import utils.Logger;

public class CrosswordModel {

	static boolean isCorrect(CrosswordView view) {

		JPanel[][] boardPanelHolders = view.getBoardPanelHolders();
		boolean result = true;
		char[] answer;

		for (PuzzleDefinition def : view.getDefinitions()) { // iterate each definition to check if the letters in its domain are correct
			int answerLength =  def.getAnswer().length;
			int indexInAnswer = 0;
			String squareString;

			answer = def.getAnswer().getAnswerString().toCharArray(); // turn correct answer to char array
			char direction = def.getDirection();
			switch (direction) { // iterate the specific definition domain
			case 'r': {
				for (int col = def.getBeginColumn(); col<def.getBeginColumn() + answerLength; col++) {
					// compare the letter in the JSqaureTextField with the correct letter
					squareString = ((SquareTextField)boardPanelHolders[def.getBeginRow()][col].getComponent(0)).getText().trim();

					if (squareString.length() < 1)
						return false;
					char letter = squareString.charAt(0); 					
					result &= (letter == answer[indexInAnswer++]);
				}
				break;
			}
			case 'l':{
				for (int col = def.getBeginColumn(); col>def.getBeginColumn() - answerLength; col--) {
					squareString = ((SquareTextField)boardPanelHolders[def.getBeginRow()][col].getComponent(0)).getText().trim();

					if (squareString.length() < 1)
						return false;
					char letter = squareString.charAt(0); 
					result &= (letter == answer[indexInAnswer++]);
				}
				break;
			}
			case 'u': {
				for (int row = def.getBeginRow(); row>def.getBeginRow() - answerLength; row--) {
					squareString  = ((SquareTextField)boardPanelHolders[row][def.getBeginColumn()].getComponent(0)).getText().trim();

					if (squareString.length() < 1)
						return false;
					char letter = squareString.charAt(0); 
					result &= (letter == answer[indexInAnswer++]);
				}
				break;
			}
			case 'd': {
				for (int row = def.getBeginRow(); row<def.getBeginRow() + answerLength; row++) {
					squareString  = ((SquareTextField)boardPanelHolders[row][def.getBeginColumn()].getComponent(0)).getText().trim();

					if (squareString.length() < 1)
						return false;
					char letter = squareString.charAt(0); 
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

	static void writeLettersCorrect(CrosswordView view) {
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

	static int calculateScore(long timeElapsed, Integer usedHints) {
		double TimeScore = 1000 * Math.pow(0.9, 30000 - timeElapsed);
		double ManipulatorScore = 1 * Math.pow(0.9, 0 - usedHints);

		double score = TimeScore + ManipulatorScore;
		return (int) Math.round(score);
		//Score = sqrt(TimeScore * ManipulatorScore);

	}
}
