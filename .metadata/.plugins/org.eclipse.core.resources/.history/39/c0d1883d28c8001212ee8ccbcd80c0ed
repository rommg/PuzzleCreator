package gui;

import java.util.List;

import puzzleAlgorithm.PuzzleSquare;

public class PrepareGameModel {

	void goBtnClicked() {
		//PuzzleSquare[][] board = model.getBoard(this); // This is in separate Thread
		// in the meantime, answer question in WaitingView
		MainView.mainView.showWaitView();
	}
	
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
			//GuiAlgorithmConnector.createPuzzle(difficulty); // send request for board
		}
		return board;
	}
}
