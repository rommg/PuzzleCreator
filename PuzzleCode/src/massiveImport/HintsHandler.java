package massiveImport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import connectionPool.DBConnection;

public class HintsHandler {

	private static final int MAXIMUM_NUMBER_OF_HINTS_PER_ENTITY = 10;

	public static void setMaximumTemHintsForEachEntity(){
		Set<Integer> entitiesIds = getAllEntitiesIds();
		for (Integer entityId : entitiesIds) {
			if(getNumOfHints(entityId) > 10){
				removeHints(entityId, getNumOfHints(entityId)-MAXIMUM_NUMBER_OF_HINTS_PER_ENTITY);
			}
		}
	}

	private static void removeHints(Integer entityId, long numOfhintsToDelete) {
		String sql = "SELECT count(predicate_id) AS num_of_predicates FROM hints WHERE entity_id = " + entityId + ";";
		List<Map<String,Object>> rs = DBConnection.executeQuery(sql);
		int numOfPredicate = (Integer)rs.get(0).get("num_of_predicates");
		// TODO: saleet
	}

	private static long getNumOfHints(Integer entityId) {
		String sql = "SELECT (SELECT count(*) FROM entities_definitions WHERE entity_id = " + entityId + ") + " +
				"(SELECT count(*) FROM hints WHERE entity_id = " + entityId + ") AS num_of_hints " +
				"FROM dual;";
		List<Map<String,Object>> rs = DBConnection.executeQuery(sql);
		long numOfHints = (Long)rs.get(0).get("num_of_hints") - 1;
		return numOfHints;
	}

	private static Set<Integer> getAllEntitiesIds() {
		String sql = "SELECT id FROM entities;" ;
		List<Map<String,Object>> rs = DBConnection.executeQuery(sql);
		Set<Integer> ret = new TreeSet<Integer>();
		for (Map<String,Object> row : rs) {
			ret.add((Integer)row.get("id"));
		}
		return ret;
	}

	public static void test(){
		Set<Integer> entities = getAllEntitiesIds();
		for (Integer entityId : entities) {
			if (getNumOfHints(entityId) > 10){
				System.out.println("entity id: " + entityId + " has more than 10 hints (" + getNumOfHints(entityId) +")");
			}
		}
	}
}
