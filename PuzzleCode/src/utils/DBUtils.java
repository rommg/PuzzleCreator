package utils;

import java.util.ArrayList;
import java.util.HashMap;
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

		String maxLenghtCondition = "answers.length <= " + maxLength;

		String sqlQuery = "select answer, answers.entity_id as entity_id, additional_information " +
				"from answers, entities , entities_definitions, definitions, definitions_topics " +
				"where " +
				"answers.entity_id = entities.id and " +
				"entities.id = entities_definitions.entity_id and " +
				"entities_definitions.definition_id = definitions.id and " +
				"definitions.id = definitions_topics.definition_id and " +
				topicsCondition  + " and " + maxLenghtCondition +  ";";

		List<Map<String,Object>> rs = DBConnection.executeQuery(sqlQuery);
		List<Answer> answers = new ArrayList<Answer>();
		for (Map<String,Object> row : rs) {
			String answer = row.get("answer").toString();
			int entity_id = Integer.parseInt(row.get("entity_id").toString());
			String additionalInfo = row.get("additional_information").toString();
			Answer toAdd = new Answer(answer, entity_id, additionalInfo);
			answers.add(toAdd);
		}
		return answers;
	}

	public static boolean setHintsAndDefinitions(List<PuzzleDefinition> pDefinitions) {
		StringBuffer entitiesIds = new StringBuffer("(");
		for (PuzzleDefinition puzzleDefinition : pDefinitions) {
			entitiesIds.append(puzzleDefinition.getEntityId());
			entitiesIds.append(", ");
		}
		entitiesIds.deleteCharAt(entitiesIds.lastIndexOf(","));
		entitiesIds.append(")");



		Map<Integer, List<String>> definitions = getDefinitions(entitiesIds.toString());
		Map<Integer, List<String>> hints = getHints(entitiesIds.toString());

		for (PuzzleDefinition puzzleDefinition : pDefinitions) {
			int entityId = puzzleDefinition.getEntityId();
			int rand = (int)(Math.random() * definitions.get(entityId).size());
			try {
				puzzleDefinition.setDefinition(definitions.get(entityId).get(rand));
			} catch (Exception ex){
				System.out.println("bla bla");
			}
			definitions.get(entityId).remove(rand);
			if (definitions.get(entityId).size() != 0){
				if (hints.keySet().contains(entityId)){
					hints.get(entityId).addAll(definitions.get(entityId));
				}
				else{
					hints.put(entityId, definitions.get(entityId));
				}
			}
			puzzleDefinition.setHints(hints.get(entityId));
		}
		return true;
	}

	private static Map<Integer, List<String>> getHints(String entityIds) {
		String sqlHintsQuery = "select entity_id, yago_hint, is_entity_subject, subject_str, object_str " +
				"from hints h, predicates p " +
				"where h.predicate_id = p.id and " +
				"entity_id in " + entityIds +";";

		List<Map<String,Object>> hintsRs = DBConnection.executeQuery(sqlHintsQuery);

		Map<Integer, List<String>> hints = new HashMap<Integer, List<String>>();

		for (Map<String, Object> row : hintsRs) {
			int entityId = Integer.parseInt(row.get("entity_id").toString());
			String yagoHint = getProperName(row.get("yago_hint").toString());
			boolean isEntitySubject = Boolean.parseBoolean(row.get("is_entity_subject").toString());
			String hintStr = (isEntitySubject ? row.get("subject_str").toString() : row.get("object_str").toString());
			hintStr = hintStr.replace("?", yagoHint);
			if (hints.keySet().contains(entityId)){
				hints.get(entityId).add(hintStr);
			}
			else{
				List<String> hintsList = new ArrayList<String>();
				hintsList.add(hintStr); 
				hints.put(entityId, hintsList);
			}
		}

		return hints;
	}

	private static Map<Integer, List<String>> getDefinitions(String entityIds) {
		String sqlDefinitionsQuery = "select entities.id as entity_id, definitions.definition as definition " +
				"from entities, entities_definitions, definitions "  + 
				"where entities.id = entities_definitions.entity_id and " +
				"entities_definitions.definition_id = definitions.id and " +
				"entity_id in " + entityIds + ";";
		List<Map<String,Object>> definitionsRs = DBConnection.executeQuery(sqlDefinitionsQuery);
		Map<Integer, List<String>> definitions = new HashMap<Integer, List<String>>();

		for (Map<String, Object> row : definitionsRs) {
			int entityId = Integer.parseInt(row.get("id").toString());
			String definition = row.get("definition").toString();
			if (definitions.keySet().contains(entityId)){
				definitions.get(entityId).add(definition);
			}
			else{
				List<String> definitionList = new ArrayList<String>();
				definitionList.add(definition); 
				definitions.put(entityId, definitionList);
			}
		}
		return definitions;
	}

	private static String getProperName(String string) {
		String ret = string.substring(string.indexOf('<')+1, string.indexOf('>'));
		ret = ret.replace('_', ' ');
		return ret;
	}

	//TODO: remove!!!!
	public static void test(){
		int[] topics = {1};
		List<Answer> answers = DBUtils.getPossibleAnswers(topics, 10);
		System.out.println("Found " + answers.size() + " possible answers");
		for (Answer answer : answers) {
			if(answer.length > 10){
				System.out.println("ERROR: too long answer");
			}
		}

		List<PuzzleDefinition> pDefinitions = new ArrayList<PuzzleDefinition>();
		for (int i = 1; i < 32683; i++) {
			PuzzleDefinition pd = new PuzzleDefinition(0, 0, 0, 0, 0, 'R');
			pd.setAnswer(new Answer("", i));
			pDefinitions.add(pd);
		}
		setHintsAndDefinitions(pDefinitions);
		int maxNumOfHints = 0;
		int entitiesWithoutHints = 0;
		boolean error = false;
		for (PuzzleDefinition pd : pDefinitions) {
			if(pd.getDefinition() == null){
				System.out.println("ERROR: no definition for entity: " + pd.getEntityId());
				error = true;
			}
			if (pd.getHints() != null){
				if(maxNumOfHints < pd.getHints().size()){
					maxNumOfHints = pd.getHints().size();
					if (maxNumOfHints > 100){
						System.out.println("More than 100 hints: " + pd.getEntityId());
					}
				}
			}
			else{
				entitiesWithoutHints++;
			}
			//System.out.println("id: " + pd.getEntityId() + " def: " + pd.getDefinition() + " hints: " + pd.getHints());
		}
		System.out.println("Number of entities with no hint: " + entitiesWithoutHints);
		System.out.println("Maximum Number of hints: "  + maxNumOfHints);
		if (!error){
			System.out.println("All Entities have definitions!");
		}
	}

	//TODO: end to remove

	public String[] getTriviaQuestion(){
		//TODO saleet
		return null;
	}
}
