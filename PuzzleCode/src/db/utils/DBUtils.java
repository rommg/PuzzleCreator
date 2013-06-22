package db.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import core.algorithm.Answer;
import core.algorithm.PuzzleDefinition;
import db.DBConnection;

public class DBUtils {

	public static List<Answer> getPossibleAnswers(int[] topics, int maxLength) throws SQLException {
		StringBuffer topicsCondition = new StringBuffer("(");
		boolean first = true;
		for (int topic : topics) {
			if (first) {
				first = false;
			} else {
				topicsCondition.append(" or ");
			}
			topicsCondition.append("definitions_topics.topic_id = ");
			topicsCondition.append(topic);
		}
		topicsCondition.append(")");

		String maxLenghtCondition = "answers.length <= " + maxLength
				+ " AND answers.length >= 2";

		String sqlQuery = "select distinct answers.id, answer, answers.entity_id as entity_id, additional_information "
				+ "from answers, entities_definitions, definitions_topics "
				+ "where "
				+ "answers.entity_id = entities_definitions.entity_id and "
				+ "entities_definitions.definition_id = definitions_topics.definition_id and "
				+ topicsCondition + " and " + maxLenghtCondition + ";";

		List<Map<String, Object>> rs = DBConnection.executeQuery(sqlQuery);
		List<Answer> answers = new ArrayList<Answer>();
		for (Map<String, Object> row : rs) {
			String answer = row.get("answer").toString();
			int entity_id = Integer.parseInt(row.get("entity_id").toString());
			String additionalInfo = row.get("additional_information")
					.toString();
			Answer toAdd = new Answer(answer, entity_id, additionalInfo);
			answers.add(toAdd);
		}
		return answers;
	}

	public static boolean setHintsAndDefinitions(
			List<PuzzleDefinition> pDefinitions)  throws SQLException {
		StringBuffer entitiesIds = new StringBuffer("(");
		for (PuzzleDefinition puzzleDefinition : pDefinitions) {
			entitiesIds.append(puzzleDefinition.getEntityId());
			entitiesIds.append(", ");
		}
		entitiesIds.deleteCharAt(entitiesIds.lastIndexOf(","));
		entitiesIds.append(")");

		Map<Integer, List<String>> definitions = getDefinitions(entitiesIds
				.toString());
		Map<Integer, List<String>> hints = getHints(entitiesIds.toString());

		for (PuzzleDefinition puzzleDefinition : pDefinitions) {
			int entityId = puzzleDefinition.getEntityId();
			int rand = (int) (Math.random() * definitions.get(entityId).size());
			puzzleDefinition.setDefinition(definitions.get(entityId).get(rand));
			definitions.get(entityId).remove(rand);
			if (definitions.get(entityId).size() != 0) {
				if (hints.keySet().contains(entityId)) {
					hints.get(entityId).addAll(definitions.get(entityId));
				} else {
					hints.put(entityId, definitions.get(entityId));
				}
			}
			if (hints.keySet().contains(entityId)) {
				removeHintsIfMoreThen10(hints, entityId);
			}
			puzzleDefinition.setHints(hints.get(entityId));
		}
		return true;
	}

	private static void removeHintsIfMoreThen10(
			Map<Integer, List<String>> hints, Integer entityId) {
		int numOfHints = hints.get(entityId).size();
		while (numOfHints > 10) {
			numOfHints = hints.get(entityId).size();
			int rand = (int) (Math.random() * numOfHints);
			hints.get(entityId).remove(rand);
		}
	}

	private static Map<Integer, List<String>> getHints(String entityIds) throws SQLException {
		String sqlHintsQuery = "select entity_id, yago_hint, is_entity_subject, subject_str, object_str "
				+ "from hints h, predicates p "
				+ "where h.predicate_id = p.id and "
				+ "entity_id in "
				+ entityIds + ";";

		List<Map<String, Object>> hintsRs = DBConnection
				.executeQuery(sqlHintsQuery);

		Map<Integer, List<String>> hints = new HashMap<Integer, List<String>>();

		for (Map<String, Object> row : hintsRs) {
			int entityId = Integer.parseInt(row.get("entity_id").toString());
			String yagoHint = getProperName(row.get("yago_hint").toString());
			boolean isEntitySubject = Boolean.parseBoolean(row.get(
					"is_entity_subject").toString());
			String hintStr = (isEntitySubject ? row.get("subject_str")
					.toString() : row.get("object_str").toString());
			hintStr = hintStr.replace("?", yagoHint);
			if (hints.keySet().contains(entityId)) {
				hints.get(entityId).add(hintStr);
			} else {
				List<String> hintsList = new ArrayList<String>();
				hintsList.add(hintStr);
				hints.put(entityId, hintsList);
			}
		}

		return hints;
	}

	private static Map<Integer, List<String>> getDefinitions(String entityIds) throws SQLException {
		String sqlDefinitionsQuery = "select entities.id as entity_id, definitions.definition as definition "
				+ "from entities, entities_definitions, definitions "
				+ "where entities.id = entities_definitions.entity_id and "
				+ "entities_definitions.definition_id = definitions.id and "
				+ "entities.id in " + entityIds + ";";
		List<Map<String, Object>> definitionsRs = DBConnection
				.executeQuery(sqlDefinitionsQuery);
		Map<Integer, List<String>> definitions = new HashMap<Integer, List<String>>();

		for (Map<String, Object> row : definitionsRs) {
			int entityId = Integer.parseInt(row.get("id").toString());
			String definition = row.get("definition").toString();
			if (definitions.keySet().contains(entityId)) {
				definitions.get(entityId).add(definition);
			} else {
				List<String> definitionList = new ArrayList<String>();
				definitionList.add(definition);
				definitions.put(entityId, definitionList);
			}
		}
		return definitions;
	}

	private static String getProperName(String name) {
		String ret = name;
		int greaterIndex = name.indexOf('<');
		int lessIndex = name.indexOf('>'); 
		if (!(greaterIndex == -1) && !(lessIndex == -1)){
			ret = name.substring(greaterIndex + 1, lessIndex);
		}
		ret = ret.replace('_', ' ');
		return ret;
	}

	/**
	 * GUI SQLs
	 */
	public static Map<String, Integer> getAllTopicIDsAndNames() throws SQLException  {
		String sql = "select topics.id,topics.name " + "from topics";

		Map<String, Integer> retMap = new HashMap<String, Integer>();
		List<Map<String, Object>> results = DBConnection.executeQuery(sql);
		for (Map<String, Object> result : results) {
			retMap.put(result.get("name").toString(),
					(Integer) result.get("id"));
		}
		return retMap;
	}

	public static Map<String, Integer> getDefinitionsByEntityID(int entityID) throws SQLException {
		String sql = "SELECT definitions.id, definitions.definition "
				+ "FROM definitions, entities_definitions, entities "
				+ "WHERE entities.id = entities_definitions.entity_id AND "
				+ "definitions.id = entities_definitions.definition_id "
				+ "AND entities_definitions.entity_id IN "
				+ createINString(Collections.singletonList(entityID)) + ";";
		List<Map<String, Object>> resultSet = DBConnection.executeQuery(sql);

		Map<String, Integer> retMap = new HashMap<String, Integer>();

		for (Map<String, Object> map : resultSet) {
			retMap.put(map.get("definition").toString(),
					(Integer) map.get("id"));
		}
		return retMap;
	}

	public static Map<String, Integer> getAllEntities() throws SQLException {
		String sql = "SELECT entities.id, entities.name " + "FROM entities;";

		Map<String, Integer> retMap = new HashMap<String, Integer>();

		List<Map<String, Object>> results = DBConnection.executeQuery(sql);

		for (Map<String, Object> result : results) {
			retMap.put(getProperName(result.get("name").toString()),
					(Integer) result.get("id"));
		}
		return retMap;
	}

	public static Map<Integer, String> getHintsByEntityID(int entityID) throws SQLException{
		String sqlHintsQuery = "select h.id as hint_id, yago_hint, is_entity_subject, subject_str, object_str "
				+ "from hints h, predicates p "
				+ "where h.predicate_id = p.id and "
				+ "entity_id = "
				+ entityID + ";";
		List<Map<String, Object>> hintsRs = DBConnection.executeQuery(sqlHintsQuery);
		Map<Integer, String> hints = new HashMap<Integer, String>();

		for (Map<String, Object> row : hintsRs) {
			int hintId = Integer.parseInt(row.get("id").toString());
			String yagoHint = getProperName(row.get("yago_hint").toString());
			boolean isEntitySubject = Boolean.parseBoolean(row.get("is_entity_subject").toString());
			String hintStr = (isEntitySubject ? row.get("subject_str").toString() : row.get("object_str").toString());
			hintStr = hintStr.replace("?", yagoHint);
			hints.put(hintId, hintStr);
		}
		return hints;
	}

	/**
	 * map of definition name to definition ID
	 * 
	 * @return
	 */
	public static Map<String, Integer> getAllDefinitions() throws SQLException {
		String sql = "SELECT definitions.id,definitions.definition "
				+ "FROM definitions";
		Map<String, Integer> map = new HashMap<String, Integer>();

		List<Map<String, Object>> queryMap = DBConnection.executeQuery(sql);
		for (Map<String, Object> row : queryMap) {
			map.put((String) row.get("definition"), (Integer) row.get("id"));
		}

		return map;
	}

	/**
	 * This Map works because Topic name is unique
	 * 
	 * @param definitionIDs
	 * @return
	 */
	public static Map<String, Integer> getTopicsByDefinitionID(int definitionID) throws SQLException {
		String sql = "SELECT topics.id, topics.name "
				+ "FROM topics, definitions_topics "
				+ "WHERE (topics.id = definitions_topics.topic_id) AND (definitions_topics.definition_id = "
				+ definitionID + ");";

		List<Map<String, Object>> lst = DBConnection.executeQuery(sql);
		Map<String, Integer> rows = new HashMap<String, Integer>();

		if (lst == null)
			return null;

		for (Map<String, Object> item : lst) {
			rows.put((String) item.get("name"), (Integer) item.get("id"));
		}

		return rows;
	}

	public static String[][] getTenBestScores() throws SQLException {
		String sql = "SELECT best_scores.user_name, best_scores.score, best_scores.date "
				+ "FROM best_scores "
				+ "ORDER BY best_scores.score DESC "
				+ "LIMIT 10;";

		List<Map<String, Object>> map = DBConnection.executeQuery(sql);
		String[][] returnArray = new String[map.size()][3]; // each of the 10
		// cells is a tuple
		// [name,score,date]

		int index = 0;
		for (Map<String, Object> row : map) {
			returnArray[index][0] = row.get("user_name").toString();
			returnArray[index][1] = row.get("score").toString();
			returnArray[index][2] = row.get("date").toString();

			index++;
		}
		return returnArray;

	}

	public static boolean addBestScore(String name, int score) throws SQLException {
		String sql = "INSERT into best_scores (user_name, score,date) VALUES ('"
				+ name + "'," + score + "," + "date(now()));";
		return (DBConnection.executeUpdate(sql) < 1);
	}

	private static String createINString(List<?> lst) {
		StringBuilder strBlder = new StringBuilder();
		strBlder.append('(');
		for (Object obj : lst) {
			strBlder.append(obj.toString());
			strBlder.append(',');
		}

		strBlder.deleteCharAt(strBlder.length() - 1); // delete last ','
		strBlder.append(')');

		return strBlder.toString();
	}

	public static String[] getTriviaQuestion() throws SQLException {
		String sqlQuery = "SELECT a.answer, a.additional_information, d.definition "
				+ "FROM entities e, answers a, definitions d, entities_definitions ed "
				+ "WHERE a.entity_id = e.id AND a.length < 10 AND a.length > 3 AND e.id = ed.entity_id AND ed.definition_id = d.id "
				+ "ORDER BY RAND() LIMIT 1;";
		List<Map<String, Object>> rs = DBConnection.executeQuery(sqlQuery);
		for (int i = 0; i < 20 && rs.size() == 0; i++) {
			if (rs.size() == 0) {
				rs = DBConnection.executeQuery(sqlQuery);
			}		
		}
		if (rs.size() == 0){
			throw new SQLException("answers table is empty");
		}
		String[] ret = new String[2];
		ret[0] = (String) rs.get(0).get("answer");
		ret[1] = (String) rs.get(0).get("definition") + " "
				+ (String) rs.get(0).get("additional_information");

		return ret;
	}

}
