package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import massiveImport.YagoFileHandler;

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
	
	/**
	 * GUI SQLs
	 */
	public static Map<String,Integer> getAllTopicIDsAndNames() {
		String sql = "select topics.id,topics.name " +
				"from topics";
		
		Map<String,Integer> retMap = new HashMap<String, Integer>();
		List<Map<String,Object>> results =  DBConnection.executeQuery(sql);
		for (Map<String,Object> result : results) {
			retMap.put(result.get("name").toString(), (Integer) result.get("id"));
		}
		return retMap;
	}
	
	public static Map<String, Integer> getDefinitionsByEntityID(int entityID){
		String sql = "SELECT definitions.id, definitions.definition " +
				"FROM definitions, entities_definitions, entities " +
				"WHERE entities.id = entities_definitions.entity_id AND " +
				"definitions.id = entities_definitions.definition_id " +
				"AND entities_definitions.entity_id IN " + createINString(Collections.singletonList(entityID)) + ";";
		List<Map<String, Object>> resultSet = DBConnection.executeQuery(sql);
		
		Map<String,Integer> retMap = new HashMap<String,Integer>();
		
		for (Map<String,Object> map : resultSet) {
			retMap.put( map.get("definition").toString(),(Integer) map.get("id"));
		}
		return retMap;
	}
	
	public static Map<String,Integer> getAllEntities() {
		String sql = "SELECT entities.id, entities.name " +
				"FROM entities;" ;
		
		Map<String,Integer> retMap = new HashMap<String,Integer>();
		
		List<Map<String,Object>> results = DBConnection.executeQuery(sql);
		
		for (Map<String,Object> result : results) {
			retMap.put(getProperName(result.get("name").toString()),(Integer)result.get("id"));
		}
		return retMap;
	}
	
	public static Map<Integer, List<String>> getHintsByEntityID(int entityID) {
		return getHints(createINString(Collections.singletonList(entityID)));
	}
	
	/**
	 * map of definition name to definition ID
	 * @return
	 */
	public static Map<String,Integer> getAllDefinitions() {
		String sql = "SELECT definitions.id,definitions.definition " +
				"FROM definitions";
		Map<String,Integer> map = new HashMap<String,Integer>();
	
		List<Map<String,Object>> queryMap = DBConnection.executeQuery(sql);
		for (Map<String,Object> row : queryMap) {
			map.put((String)row.get("definition"),(Integer)row.get("id"));
		}
		
		return map;
	}
	
	/**
	 * This Map works because Topic name is unique
	 * @param definitionIDs
	 * @return
	 */
	public static Map<String, Integer> getTopicByDefinitionIDs(List<Integer> definitionIDs) {
		String sql = "SELECT topics.id, topics.name " +
				"FROM topics, definitions_topics, definitions" +
				"WHERE (topics.id = definitions_topics.topic_id) " +
				"AND (definitions_topics.definition_id = definitions.definition) " +
				"AND (definitions.id IN " + createINString(definitionIDs) + ");";
		
		List<Map<String,Object>> lst = DBConnection.executeQuery(sql);
		Map<String,Integer> rows = new HashMap<String, Integer>();
		
		for (Map<String,Object> item : lst) {
			rows.put((String)item.get("name"),(Integer)item.get("id"));
		}
		
		return rows; 
	}
	
	public static String[][] getTenBestScores() {
		String sql = "SELECT best_scores.user_name, best_scores.score " +
				"FROM best_scores " +
				"ORDER BY best_scores.score DESC " +
				"LIMIT 10;";
		
		List<Map<String,Object>> lst = DBConnection.executeQuery(sql);
		String[][] returnArray  = new String[lst.size()][2]; // each of the 10 cells is a tuple [name,score]
		
		int index = 0;
		for (Map<String,Object> row : lst) {
			returnArray[index][0] = row.get("user_name").toString();
			returnArray[index][1] = row.get("score").toString();
			index++;
		}
		return returnArray;
				
	}
	
	private static String createINString(List<?> lst) {
		StringBuilder strBlder = new StringBuilder();
		strBlder.append('(');
		for (Object obj : lst) {
			strBlder.append(obj.toString());
			strBlder.append(',');
		}
		
		strBlder.deleteCharAt(strBlder.length()-1); // delete last ','
		strBlder.append(')');
		
		return strBlder.toString();
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

	public static String[] getTriviaQuestion(){
		String sqlQuery = "SELECT id FROM entities ORDER BY RAND() LIMIT 1;";
		List<Map<String,Object>> rs = DBConnection.executeQuery(sqlQuery);
		int randEntity = (Integer)rs.get(0).get("id");
		sqlQuery = "SELECT a.answer, d.definition, a.additional_information " +
				   "FROM entities e, answers a, definitions d, entities_definitions ed " +
				   "WHERE 	e.id = ed.entity_id AND " +
				   		   "ed.definition_id = d.id AND " +
				   		   "e.id = a.entity_id AND " +
				   		   "e.id = " + randEntity + " " +
				   		   "ORDER BY a.length " +
				   		   "LIMIT 1; ";
		rs = DBConnection.executeQuery(sqlQuery);
		String[] ret = new String[2];
		ret[0] = (String)rs.get(0).get("answer");
		ret[1] = (String)rs.get(0).get("definition") + " (" + (String)rs.get(0).get("additional_information") + ") ";
		
		return ret;
	}
	
	public static void deleteEntityDefinition(int entity_id, int definition_id){
		String sqlQuery = "DELETE FROM entities_definition WHERE entity_id = " + entity_id + " AND definition_id = " + definition_id + ";";
		DBConnection.executeQuery(sqlQuery);
	}
	
	public static void deleteHint(int hint_id){
		String sqlQuery = "DELETE FROM hints WHERE id = " + hint_id + ";";
		DBConnection.executeQuery(sqlQuery);
	}

	
	public static void addDefinitionToEntitiy(int entity_id, String definition){
		//TODO: saleet
	}
	
	public static void addHint(int entity_id, String hint){
		//TODO: saleet
	}

}
