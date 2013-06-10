package massiveImport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import connectionPool.DBConnection;

public class HintsHandler {
	
	private static Set<Integer> hintIdToDelete = new TreeSet<Integer>();

	private static final int MAXIMUM_NUMBER_OF_HINTS_PER_ENTITY = 10;

	public static void setMaximumTemHintsForEachEntity(){
		Set<Integer> entitiesIds = getAllEntitiesIds();
		for (Integer entityId : entitiesIds) {
			if(getNumOfHints(entityId) > 10){
				addHintsToRemoveList(entityId, getNumOfHints(entityId)-MAXIMUM_NUMBER_OF_HINTS_PER_ENTITY);
			}
		}
		
//		DBConnection.excuteDeleteHintsByIds(hintIdToDelete);
		
//		String idsToDelete = hintIdToDelete.toString().replace('[', '(').replace(']', ')');
//		String sql = "DELETE FROM hints WHERE id IN " + idsToDelete + ";";
//		DBConnection.executeQuery(sql);
//		DBConnection.executeQuery("commit;");
	}

	private static void addHintsToRemoveList(Integer entityId, long numOfhintsToDelete) {
		String sql = "SELECT id, predicate_id FROM hints WHERE entity_id = " + entityId + ";";
		List<Map<String,Object>> rs = DBConnection.executeQuery(sql);
		Map<Integer, List<Integer>> hints = new HashMap<Integer, List<Integer>>();
		for (Map<String, Object> row : rs) {
			int predicate_id = (Integer)row.get("predicate_id");
			int hintId = (Integer)row.get("id");
			if (hints.containsKey(predicate_id)){
				hints.get(predicate_id).add(hintId);
			}
			else{
				List<Integer> toAdd = new ArrayList<Integer>();
				toAdd.add(hintId);
				hints.put(predicate_id, toAdd);
			}
		}
		int deleted = 0;
		Set<Integer> predicates = hints.keySet();
		int minNumOfHintsPerPredicate = 1;
		while (deleted < numOfhintsToDelete){
			for (Integer predicate : predicates) {
				if (hints.get(predicate).size() > minNumOfHintsPerPredicate){
					int rand = (int)Math.random() * hints.get(predicate).size();
					hintIdToDelete.add(hints.get(predicate).get(rand));
					hints.get(predicate).remove(rand);
					deleted++;
					break;
				}
			}
			if (allSizeOne(hints)){
				minNumOfHintsPerPredicate = 0;
			}
		}
	}

	private static boolean allSizeOne(Map<Integer, List<Integer>> hints) {
		Set<Integer> keys = hints.keySet();
		for (Integer k : keys) {
			if (hints.get(k).size() != 1){
				return false;
			}
		}
		return true;
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
		setMaximumTemHintsForEachEntity();
		Set<Integer> entities = getAllEntitiesIds();
		for (Integer entityId : entities) {
			if (getNumOfHints(entityId) > 10){
				System.out.println("entity id: " + entityId + " has more than 10 hints (" + getNumOfHints(entityId) +")");
			}
		}
	}
}
