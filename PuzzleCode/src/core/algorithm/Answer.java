package core.algorithm;

/**
 * This class holds a puzzle answer. 
 * @author rommguy
 *
 */
public class Answer {
	private String answerSt;
	private int entityId;
	private String additionalInformation;
	public final int length;
	
	
	public Answer(String answer, int entityId, String additionalInformation){
		this.answerSt = answer;
		this.entityId = entityId;
		this.additionalInformation = additionalInformation;
		this.length = answer.length();
	}
	
	public Answer(String answer, int entityId){
		this(answer, entityId, "");
	}
	
	public String getAnswerString(){
		return this.answerSt;
	}
	
	public int getEntityId(){
		return this.entityId;
	}
	
	public String getAdditionalInformation(){
		return this.additionalInformation;
	}
}
