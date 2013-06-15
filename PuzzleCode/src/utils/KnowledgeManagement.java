package utils;

import java.util.List;

import connectionPool.DBConnection;

public class KnowledgeManagement {
	
	static private int hintNumber = 0;
	static private int defenitionNumber = 0;
	
	public static void deleteEntityDefinition(int entityId, int definitionId){
		DBConnection.deleteEntityDefinition(entityId, definitionId);
	}

	public static void deleteHint(int hintId){
		DBConnection.deleteHint(hintId);
	}
	
	public static void deleteEntity(int entityId){
		DBConnection.deleteEntity(entityId);
	}

	/*
	 * assumption: User definition is one of the topics
	 * 			   topic is not null, definition is not null and entityId is valid (exist in DB)
	 * return definition id
	 */
	public static int addDefinitionToEntitiy(int entityId, String definition, List<Integer> topics){
		defenitionNumber++;
		int definitionId = DBConnection.addDefinition(defenitionNumber, definition); //INSERT INTO definitions (yago_type, definition) VALUES ("userDefinition", definition); get the id
		DBConnection.setTopicsToDefinition(definitionId, topics); //for(Int topicId: topics){INSERT INTO definitions_topics (definition_id, topics_id) VALUSE (definitionId, topicId);}
		DBConnection.setNewDefinition(entityId, definitionId); // INSER INTO entities_definitions (entity_id, definition_id) VALUES (entityId, definitionId);
		return definitionId;
	}
	
	
	/*
	 * assumption: User definition is one of the topics
	 * 			   topic is not null, definition is not null, entity is not null
	 * return :
	 * int[0] is the entityId 
	 * int[1] is the definition id
	 */
	public static int[] addDefinitionToEntitiy(String entity, String definition, List<Integer> topics){
		int entityId = DBConnection.addEntity(entity); //INSERT INTO entities (name) VALUES (entity); get the id
		int definitionId = addDefinitionToEntitiy(entityId, definition, topics);
		int[] ret = {entityId, definitionId};
		return ret;
	}

	/*
	 * return hint id
	 * assumption: hint is not null 
	 */
	public static int addHint(int entityId, String hint){
		hintNumber++;
		int predicateId = DBConnection.addPredicate(hintNumber, hint); //INSER INTO predicates (yago_predicate, subject_str) VALUES ("<user_hint>"+hintNumber, hint); get the id
		int hintId = DBConnection.addHint(entityId, predicateId); //INSER INTO hints (predicate_id, entity_id, is_entity_subject) VALUES (predicateId, entityId, true); get the id
		return hintId;
	}
}
