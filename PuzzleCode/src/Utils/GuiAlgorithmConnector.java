package Utils;
import java.util.List;

import puzzleAlgorithm.PuzzleDefinition;
import puzzleAlgorithm.PuzzleSquare;


public interface GuiAlgorithmConnector {
	public PuzzleSquare[][] createPuzzle(String difficulty, List<String> topics);
	
	public void drawBoard(PuzzleSquare[][] board, List<PuzzleDefinition> definitions);
	
}
