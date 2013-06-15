package utils;

import java.util.List;

import connectionPool.DBConnection;

public class KnowledgeManagement {

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
	public static int[] addDefinitionToEntitiy(int entityId, String entity, int definitionId, String definition, List<Integer> topics){
		if (definitionId == -1){
			definitionId = DBConnection.addDefinition(definition); //INSERT INTO definitions (yago_type, definition) VALUES ("userDefinition", definition); get the id
			DBConnection.setTopicsToDefinition(definitionId, topics); //for(Int topicId: topics){INSERT INTO definitions_topics (definition_id, topics_id) VALUSE (definitionId, topicId);}
		}
		if (entityId == -1){
			entityId = DBConnection.addEntity(entity); //INSERT INTO entities (name) VALUES (entity); get the id
		}
		DBConnection.setNewDefinition(entityId, definitionId); // INSER INTO entities_definitions (entity_id, definition_id) VALUES (entityId, definitionId);
		int[] ret =  {entityId, definitionId};
		return ret;
	}

//TODO: remove
//	/*
//	 * assumption: User definition is one of the topics
//	 * 			   topic is not null, definition is not null, entity is not null
//	 * return :
//	 * int[0] is the entityId 
//	 * int[1] is the definition id
//	 */
//	public static int[] addDefinitionToEntitiy(String entity, int definitionId, String definition, List<Integer> topics){
//		int entityId = DBConnection.addEntity(entity); //INSERT INTO entities (name) VALUES (entity); get the id
//		int defId = addDefinitionToEntitiy(entityId, definitionId ,definition, topics);
//		int[] ret = {entityId, defId};
//		return ret;
//	}

	/*
	 * return hint id
	 * assumption: hint is not null 
	 */
	public static int addHint(int entityId, String hint){
		int predicateId = DBConnection.addPredicate(hint); //INSER INTO predicates (yago_predicate, subject_str) VALUES ("<user_hint>"+hintNumber, hint); get the id
		int hintId = DBConnection.addHint(entityId, predicateId); //INSER INTO hints (predicate_id, entity_id, is_entity_subject) VALUES (predicateId, entityId, true); get the id
		return hintId;
	}
}
