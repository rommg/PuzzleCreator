package gui;

import java.util.List;

import puzzleAlgorithm.PuzzleSquare;

public class PrepareGameModel {

	PuzzleSquare[][] getBoard(PrepareGameView view) {
		PuzzleSquare[][] board = null;
		List<String> selectedTopics = view.getUserSelectedTopics();
		if (selectedTopics.size() < 1) {
			// Dialog Box
			System.out.println("Error veze");
			return null;
		}
		else { 
			int difficulty = view.getDifficulty();
			//GuiAlgorithmConnector.createPuzzle(); // send request for board
		}
		return board;
	}
}
