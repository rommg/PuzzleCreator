package puzzleAlgorithm;

import java.util.List;

public class BoardSolution {
	
	private boolean isFound;
	private PuzzleSquare[][] board;
	private List<PuzzleDefinition> definitions;
	private Exception resultException;
	
	
	public BoardSolution(PuzzleSquare[][] board, List<PuzzleDefinition> definitions, boolean isFound, Exception ex) {
		this.board = board;
		this.definitions = definitions;
		this.isFound =true;
		this.resultException = ex;
	}
	
	public boolean isFound() {
		return isFound;
	}

	
	public PuzzleSquare[][] getBoard() {
		return board;
	}

	public List<PuzzleDefinition> getDefinitions() {
		return definitions;
	}
	
	public Exception getResultException(){
		return this.resultException;
	}

	
}
