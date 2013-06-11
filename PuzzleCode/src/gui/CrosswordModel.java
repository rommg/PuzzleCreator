package gui;

import javax.swing.JPanel;

import puzzleAlgorithm.AlgorithmWorker;
import puzzleAlgorithm.BoardSolution;
import puzzleAlgorithm.PuzzleDefinition;
import utils.Logger;

public class CrosswordModel {



	static int calculateScore(long timeElapsed, Integer usedHints) {
		return (int) Math.round((1/Math.sqrt(timeElapsed) - 2/(usedHints^2))*1000);
	}
	
//	static int test() {
//		calculateScore(360000, 20);
//		calculateScore(360000, 20);
//		calculateScore(360000, 20);
//		calculateScore(360000, 20);
//
//		calculateScore(360000, 20);
//
//		
//	}
}
