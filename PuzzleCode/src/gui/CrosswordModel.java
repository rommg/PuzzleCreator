package gui;

import javax.swing.JPanel;

import puzzleAlgorithm.AlgorithmWorker;
import puzzleAlgorithm.BoardSolution;
import puzzleAlgorithm.PuzzleDefinition;
import utils.Logger;

public class CrosswordModel {



	static int calculateScore(long timeElapsed, Integer usedHints) {
		System.out.println((int) Math.round((Math.sqrt(timeElapsed) - (usedHints))));
		return 0;
	}
	
	static int test() {
		calculateScore(360000, 20);
		calculateScore(360000, 25);
		calculateScore(360000, 30);
		calculateScore(360000, 40);

		calculateScore(360000, 50);
		calculateScore(500000, 50);
		calculateScore(600000, 50);
		calculateScore(600000, 30);
		calculateScore(600000, 20);
		calculateScore(0, 0);

		
		return 0;

		
	}
}
