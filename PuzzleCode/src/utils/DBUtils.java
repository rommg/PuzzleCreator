package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import connectionPool.DBConnection;
import puzzleAlgorithm.Answer;
import puzzleAlgorithm.PuzzleDefinition;

public class DBUtils {

	public static List<Answer> getPossibleAnswers(int[] topics, int maxLength) {
		StringBuffer topicsCondition = new StringBuffer("(");
		boolean first = true;
		for (int topic : topics) {
			if (first) {
				first = false;  
			}
			else{
				topicsCondition.append(" or ");
			}
			topicsCondition.append("definitions_topics.topic_id = ");
			topicsCondition.append(topic);
		}
		topicsCondition.append(")");
		
		String maxLenghtCondition = "answers.length < " + maxLength;
		
		String sqlQuery = "select answer, entity_id " +
						  "from answers, entities , entities_definitions, definitions, definitions_topics " +
						  "where " +
						  	"answers.entity_id = entities.id and" +
						  	"entities.id = entities_definitions.entity_id and" +
						  	"entities_definitions.definition_id = definitions.id and" +
						  	"definitions.id = definitions_topics.definition_id and" +
						  	topicsCondition  + " and " + maxLenghtCondition +  ";";
		
		List<Map<String,Object>> rs = DBConnection.executeQuery(sqlQuery);
		List<Answer> answers = new ArrayList<Answer>();
		for (Map<String,Object> row : rs) {
			String answer = row.get("answer").toString();
			int entity_id = Integer.parseInt(row.get("entity_id").toString());
			Answer toAdd = new Answer(answer, entity_id);
			answers.add(toAdd);
		}
		return answers;
	}

	public static boolean setHintsAndDefinitions(
			List<PuzzleDefinition> pDefinitions) {
		// TODO
		return false;
	}
}
