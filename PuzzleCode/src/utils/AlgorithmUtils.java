package utils;

import java.util.List;

import puzzleAlgorithm.AlgorithmRunner;
import puzzleAlgorithm.BoardSolution;
import puzzleAlgorithm.PuzzleDefinition;
import puzzleAlgorithm.PuzzleSquare;

public class AlgorithmUtils {
	
	
	public static BoardSolution createPuzzle(int difficulty, int[] topics){
		//TODO remove condition and mock topics
		if (topics == null){
			topics = new int[3];
			topics[0] = 3;
			topics[1] = 4;
			topics[2] = 5;
		}
		
		return AlgorithmRunner.runAlgorithm(topics,difficulty);
		
	}
	
	public static void drawBoard(PuzzleSquare[][] board, List<PuzzleDefinition> definitions){
		//TODO
		return;
	}
	

}
