package puzzleAlgorithm;

public class Answer {
	private String answerSt;
	private int entityId;
	public final int length;
	
	public Answer(String answer, int entityId){
		this.answerSt = answer;
		this.entityId = entityId;
		this.length = answer.length();
	}
	
	public String getAnswerString(){
		return this.answerSt;
	}
	
	public int getEntityId(){
		return this.entityId;
	}
}
