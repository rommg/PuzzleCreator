package gui;

import javax.swing.JPanel;

import puzzleAlgorithm.AlgorithmWorker;
import puzzleAlgorithm.BoardSolution;
import puzzleAlgorithm.PuzzleDefinition;
import utils.Logger;

public class CrosswordModel {



	static int calculateScore(long timeElapsed, Integer usedHints) {
		double TimeScore = 1000 * Math.pow(0.9, 30000 - timeElapsed);
		double ManipulatorScore = 1 * Math.pow(0.9, 0 - usedHints);

		double score = TimeScore + ManipulatorScore;
		return (int) Math.round(score);
		//Score = sqrt(TimeScore * ManipulatorScore);

	}
}
