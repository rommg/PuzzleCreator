package core.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a square on the puzzle board.<br> 
 * The method check letter, is used to check if a specific letter can be assigned to this square.
 */
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
			cloned.letter= letter;
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

	/**
	 * The method checks if param c can be assigned to this puzzle square.<br>
	 * If setLetter is true, and the check is positive, the method assignes c to this square, affecting all definitions that uses this square.
	 * @param c
	 * @param setLetter
	 * @return true if c can be assigned to this square
	 */
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
