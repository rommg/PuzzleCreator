package db.utils;

import java.sql.SQLException;
import java.util.List;

import parsing.YagoFileHandler;

import db.DBConnection;

public class KnowledgeManagement {

	public static void deleteEntityDefinition(int entityId, int definitionId) throws SQLException {
		DBConnection.deleteEntityDefinition(entityId, definitionId);
	}

	public static void deleteHint(int hintId)  throws SQLException {
		DBConnection.deleteHint(hintId);
	}

	public static void deleteEntity(int entityId)  throws SQLException {
		DBConnection.deleteEntity(entityId);
	}

	/*
	 * assumption: User definition is one of the topics
	 * 			   topic is not null, definition is not null and entityId is valid (exist in DB)
	 * return definition id
	 */
	public static int[] addDefinitionToEntitiy(int entityId, String entity, int definitionId, String definition, List<Integer> topics)  throws SQLException {
		if (definitionId == -1){
			definitionId = DBConnection.addDefinition(definition); //INSERT INTO definitions (yago_type, definition) VALUES ("userDefinition", definition); get the id
			DBConnection.setTopicsToDefinition(definitionId, topics); //for(Int topicId: topics){INSERT INTO definitions_topics (definition_id, topics_id) VALUSE (definitionId, topicId);}
		}
		if (entityId == -1){
			entityId = DBConnection.addEntity(entity); //INSERT INTO entities (name) VALUES (entity); get the id
			if (!YagoFileHandler.containsNonEnglishChars(entity)) { // subject is of a relevant type and English letters only
				String answer = entity.replaceAll(" ", "");
				StringBuffer additionalInfo = new StringBuffer();

				String[] entityNameDivided = entity.split(" ");
				if (entityNameDivided.length > 1) { // create additional information if there word count in entity > 1
					additionalInfo.append("(");
					for (int i = 0; i<entityNameDivided.length; ++i) {
						additionalInfo.append(entityNameDivided[i].length());
						if (i == entityNameDivided.length - 1) //last word in entity
							additionalInfo.append(")");
						else 
							additionalInfo.append(",");
					}
				}

				DBConnection.addAnswer(answer, answer.length(), additionalInfo.toString(), entityId);
			}
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
	public static int addHint(int entityId, String hint)  throws SQLException {
		int predicateId = DBConnection.addPredicate(hint); //INSER INTO predicates (yago_predicate, subject_str) VALUES ("<user_hint>"+hintNumber, hint); get the id		
		int hintId = DBConnection.addHint(entityId, predicateId); //INSER INTO hints (predicate_id, entity_id, is_entity_subject) VALUES (predicateId, entityId, true); get the id
		return hintId;
	}
}
