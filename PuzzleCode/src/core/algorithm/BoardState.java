package core.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BoardState {
	private  PuzzleSquare[][] board;
	private  List<PuzzleDefinition> definitions;
	private  List<PuzzleDefinition> unSolved;
	private PuzzleDefinition lastDef;
	private Set<Integer> usedEntities;
	
	
	public BoardState(int size){
		this.definitions = new ArrayList<PuzzleDefinition>();
		this.unSolved = new ArrayList<PuzzleDefinition>();
		this.board = new PuzzleSquare[size][size];
		this.usedEntities = new HashSet<Integer>();
	}
	
	
	public PuzzleSquare[][] getBoard() {
		return board;
	}


	public Set<Integer> getUsedEntites(){
		return usedEntities;
	}
	
	public List<PuzzleDefinition> getDefinitions() {
		return definitions;
	}


	public List<PuzzleDefinition> getUnSolved() {
		return unSolved;
	}


	public void setUnSolved(List<PuzzleDefinition> unSolved) {
		this.unSolved = unSolved;
	}


	public PuzzleDefinition getLastDef() {
		return lastDef;
	}


	public void setLastDef(PuzzleDefinition lastDef) {
		this.lastDef = lastDef;
	}
	
}
