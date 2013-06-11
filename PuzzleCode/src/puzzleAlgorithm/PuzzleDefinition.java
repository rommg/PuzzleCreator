package puzzleAlgorithm;

import java.util.ArrayList;
import java.util.List;

public class PuzzleDefinition implements Comparable<PuzzleDefinition> {

	private int textRow;
	private int textColumn;
	private int beginRow;
	private int beginColumn;
	private int length;
	private List<Answer> possibleAnswers;
	private boolean solved;
	private Answer answer;
	private char[] letters;
	private String definition;
	private List<String> hints;
	AlgorithmWorker worker;
	
	/**
	 * right = 'r' , down = 'd'
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
	public PuzzleDefinition(int textRow, int textCol, int beginRow, int beginColumn, int length, char direction, AlgorithmWorker worker) {
		this(textRow, textCol, beginRow, beginColumn, length, direction, new ArrayList<Answer>(), worker);

		this.possibleAnswers = new ArrayList<Answer>();
		for (Answer ans : worker.answers) {
			if (ans.length == length) {
				possibleAnswers.add(ans);
			}
		}
	}

	public PuzzleDefinition(int textRow, int textCol, int beginRow, int beginColumn, int length, char direction, List<Answer> answers, AlgorithmWorker worker) {
		this.textRow = textRow;
		this.textColumn = textCol;
		this.beginRow = beginRow;
		this.beginColumn = beginColumn;
		this.length = length;
		this.direction = direction;
		this.solved = false;
		this.answer = new Answer("", -1, "");
		this.possibleAnswers = new ArrayList<Answer>();
		possibleAnswers.addAll(answers);
		this.hints = new ArrayList<String>();
		this.worker = worker;
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
		PuzzleDefinition cloned = new PuzzleDefinition(textRow, textColumn, beginRow, beginColumn, length, direction, possibleAnswers, worker);
		if (solved) {
			cloned.markSolved();
		}
		if (answer.getAnswerString().length() > 0) {
			cloned.setAnswer(answer);
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

		List<Answer> newPossibleAnswers = new ArrayList<Answer>();
		for (Answer answer : possibleAnswers) {
			if (answer.getAnswerString().charAt(letterIndex) == c) {
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

	public List<Answer> getPossibleAnswers() {
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
			int rowDiff = beginRow - other.getBeginRow();
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
		List<Answer> tempPossibleAnswers = new ArrayList<Answer>();
		tempPossibleAnswers.addAll(possibleAnswers);
		try {
		for (Answer answer : tempPossibleAnswers){
			row = getBeginRow();
			column = getBeginColumn();
			for (int letterIndex = 0; letterIndex < this.length; letterIndex++) {
				if (!(worker.board[column][row].isPossibleLetter(answer.getAnswerString().charAt(letterIndex)))) {
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

	public Answer getAnswer() {
		return answer;
	}
	

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public List<String> getHints() {
		return hints;
	}

	public void setHints(List<String> hints) {
		if (hints == null){
			this.hints.clear();
		}
		this.hints = hints;
	}
	
	public int getEntityId(){
		return this.answer.getEntityId();
	}
	
	public int getTextRow(){
		return textRow;
	}
	
	public int getTextCol(){
		return textColumn;
	}

	public String getAdditionalInformation() {
		return this.answer.getAdditionalInformation();
	}

}
