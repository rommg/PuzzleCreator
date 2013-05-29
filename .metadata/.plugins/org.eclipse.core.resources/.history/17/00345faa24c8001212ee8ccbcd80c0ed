package puzzleAlgorithm;

import java.util.ArrayList;
import java.util.List;


public class PuzzleSquare {

	private boolean isLetter;
	private List<PuzzleDefinition> definitions;
	private int column;
	private int row;
	private char letter;
	List<Character> possibleLetters = new ArrayList<Character>();
	
	
	public PuzzleSquare(boolean emptySquare, int column, int row){
		this.isLetter = emptySquare;
		this.definitions = new ArrayList<PuzzleDefinition>();
		this.letter = 0;
		this.column = column;
		this.row = row;
	}


	public PuzzleSquare cloneSquare(){
		PuzzleSquare cloned = new PuzzleSquare(isLetter, column, row);
		if (this.letter != 0){
			cloned.setLetter(letter);
		}
		return cloned;
	}
	public int getRow() {
		return row;
	}


	public void setRow(int row) {
		this.row = row;
	}
	
	public List<PuzzleDefinition> getDefinitions(){
		return this.definitions;
	}
	
	
	public boolean isLetter(){
		return isLetter;
	}
	
	
	public char getLetter(){
		return this.letter;
		
	}


	public int getColumn() {
		return column;
	}


	public void setColumn(int column) {
		this.column = column;
	}
	
	public void addDefinition(PuzzleDefinition def){
		this.definitions.add(def);
	}
	
	public boolean setLetter(char c){
		this.letter = c;
		return true;
	}

	public boolean checkLetter(char c, boolean setLetter){
		
		if (this.letter != 0 && this.letter != c){
			return false;
		}
		
		if (this.letter == c){
			return true;
		}
		
		for (PuzzleDefinition def : definitions){
			if (!def.checkLetter(c, row, column, setLetter)){
				return false;
			}
		}
		
		if (setLetter){
			this.letter = c;
		}
		
		return true;
	}
	
	public boolean optimizeSquare(){
		for (char c = 'a'; c <='z'; c++){
			if (this.checkLetter(c, false)){
				addPossibleLetter(c);
			}
		}
		return true;
	}
	
	public boolean addPossibleLetter(char c){
		return this.possibleLetters.add(c);
	}
	
	public boolean isPossibleLetter(char letter){
		return possibleLetters.contains(letter);
	}


}
