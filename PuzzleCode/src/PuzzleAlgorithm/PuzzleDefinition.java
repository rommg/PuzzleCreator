package puzzleAlgorithm;

import java.util.ArrayList;
import java.util.List;

public class PuzzleDefinition implements Comparable<PuzzleDefinition> {

	private int beginRow;
	private int beginColumn;
	private int length;
	private List<String> possibleAnswers;
	private boolean solved;
	private String answer;
	private char[] letters;

	/**
	 * 0 is up, 1 right, 2 down, 3 left
	 */
	private char direction;

	/**
	 * Create a new puzzle definition
	 * 
	 * @param beginRow
	 * @param beginColumn
	 * @param length
	 * @param direction
	 *            : right = 'r' , down = 'd'
	 */
	public PuzzleDefinition(int beginRow, int beginColumn, int length, char direction) {
		this(beginRow, beginColumn, length, direction, new ArrayList<String>());

		this.possibleAnswers = new ArrayList<String>();
		for (String ans : AlgorithmRunner.answers) {
			if (ans.length() == length) {
				possibleAnswers.add(ans);
			}
		}
	}

	public PuzzleDefinition(int beginRow, int beginColumn, int length, char direction, List<String> answers) {
		this.beginRow = beginRow;
		this.beginColumn = beginColumn;
		this.length = length;
		this.direction = direction;
		this.solved = false;
		this.answer = "";
		this.possibleAnswers = new ArrayList<String>();
		possibleAnswers.addAll(answers);

		this.letters = new char[length];
		for (int i = 0; i < letters.length; i++) {
			letters[i] = 0;
		}

	}

	/**
	 * This method clones this puzzle definition. 
	 * Possible answers are copied to the cloned definition
	 * A new definition is returned - identical to this definition in every way
	 * @return
	 */
	public PuzzleDefinition cloneDefinition() {
		PuzzleDefinition cloned = new PuzzleDefinition(beginRow, beginColumn, length, direction, possibleAnswers);
		if (solved) {
			cloned.markSolved();
		}
		if (answer.length() > 0) {
			cloned.setAnswer(new String(answer));
		}
		return cloned;
	}

	public boolean checkLetter(char c, int row, int column, boolean setLetter) {
		int letterIndex = -1;
		switch (direction) {
		case 'r':
			letterIndex = column - beginColumn;
			break;
		case 'd':
			letterIndex = row - beginRow;
			break;
		}

		List<String> newPossibleAnswers = new ArrayList<String>();
		for (String answer : possibleAnswers) {
			if (answer.charAt(letterIndex) == c) {
				newPossibleAnswers.add(answer);
			}
		}

		if (newPossibleAnswers.size() == 0) {
			return false;
		}

		if (setLetter) {
			possibleAnswers = newPossibleAnswers;
			letters[letterIndex] = c;
			//checkSolved();
		}

		return true;
	}

	private void checkSolved() {
		String st = "";
		for (char c : letters) {
			if (c == 0) {
				return;
			} else {
				st += c;
			}
		}
		markSolved();
		this.answer = st;
	}

	public int getBeginRow() {
		return beginRow;
	}

	public int getBeginColumn() {
		return beginColumn;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public char getDirection() {
		return direction;
	}

	public void setDirection(char direction) {
		this.direction = direction;
	}

	public List<String> getPossibleAnswers() {
		return possibleAnswers;
	}

	@Override
	public int compareTo(PuzzleDefinition other) {
		if (possibleAnswers.size() != other.getPossibleAnswers().size()) {
			if (possibleAnswers.size() > other.getPossibleAnswers().size()) {
				return 1;
			} else {
				return -1;
			}
		} else if (length != other.getLength()) {
			if (length < other.getLength()) {
				return 1;
			} else {
				return -1;
			}
		} else if (direction != other.getDirection()) {
			return direction - other.getDirection();
		} else {
			int rowDiff = beginRow - other.getBeginColumn();
			int colDiff = beginColumn - other.getBeginColumn();
			if (rowDiff != 0) {
				return rowDiff;
			} else {
				return colDiff;
			}
		}

	}
	
	public boolean optimizeDefinition(){
		int row = getBeginRow();
		int column = getBeginColumn();
		List<String> tempPossibleAnswers = new ArrayList<String>();
		tempPossibleAnswers.addAll(possibleAnswers);
		try {
		for (String answer : tempPossibleAnswers){
			row = getBeginRow();
			column = getBeginColumn();
			for (int letterIndex = 0; letterIndex < this.length; letterIndex++) {
				if (!(AlgorithmRunner.board[column][row].isPossibleLetter(answer.charAt(letterIndex)))) {
					possibleAnswers.remove(answer);
				}
				switch (direction) {
				case 'r':
					column++;
					break;
				case 'd':
					row++;
					break;
				default:
					return false;
				}
			}
		}
		} catch (Exception ex){
			System.out.println("bla");
		}
		return true;
	}

	public boolean isSolved() {
		return solved;
	}

	public void markSolved() {
		this.solved = true;
	}
	
	public void markUnsolved(){
		this.solved = false;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

}
