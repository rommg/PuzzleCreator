package gui;

import Utils.AlgorithmUtils;
import puzzleAlgorithm.AlgorithmRunner;
import puzzleAlgorithm.BoardSolution;

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
}
