package puzzleAlgorithm;

import java.util.List;

public class BoardSolution {
	
	private int template;
	
	private boolean isFound;
	public boolean isFound() {
		return isFound;
	}
	
	public int getTemplateNumber()  {
		return template;
	}

	private PuzzleSquare[][] board;
	
	public PuzzleSquare[][] getBoard() {
		return board;
	}

	public List<PuzzleDefinition> getDefinitions() {
		return definitions;
	}

	private List<PuzzleDefinition> definitions;
	
	public BoardSolution(PuzzleSquare[][] board, List<PuzzleDefinition> definitions, boolean isFound, int templateNum) {
		this.board = board;
		this.definitions = definitions;
		this.isFound =true;
	}
}
