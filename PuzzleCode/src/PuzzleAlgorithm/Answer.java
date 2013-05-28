package puzzleAlgorithm;

public class Answer {
	public String answerSt;
	public String entityName;
	
	public Answer(String answer, String entityName){
		this.answerSt = answer;
		this.entityName = entityName;
	}
	
	public String getAnswerString(){
		return this.answerSt;
	}
	
	public String getEntityName(){
		return this.entityName;
	}
}
