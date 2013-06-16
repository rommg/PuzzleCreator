package db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class HintsHandler {

	private static Set<Integer> hintIdToDelete = new TreeSet<Integer>();

	private static final int MAXIMUM_NUMBER_OF_HINTS_PER_ENTITY = 10;

	public static void setMaximumTemHintsForEachEntity() throws SQLException{
		Map<Integer, Long> entitiesIdsWithMoreThen10Hints = getAllEntitiesIdsWithMoreThen10Hints();
		Set<Integer> entitiesIds = entitiesIdsWithMoreThen10Hints.keySet();
		for (Integer entityId : entitiesIds) {
			addHintsToRemoveList(entityId, entitiesIdsWithMoreThen10Hints.get(entityId)-MAXIMUM_NUMBER_OF_HINTS_PER_ENTITY);
		}
		DBConnection.excuteDeleteHintsByIds(hintIdToDelete);
	}

	private static void addHintsToRemoveList(Integer entityId, long numOfhintsToDelete) throws SQLException{
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

	private static Map<Integer, Long> getAllEntitiesIdsWithMoreThen10Hints() throws SQLException{
		String sql = "select entity_id, count(*) as num_of_hints from hints group by entity_id having num_of_hints > 10;" ;
		List<Map<String,Object>> rs = DBConnection.executeQuery(sql);
		Map<Integer, Long> ret = new HashMap<Integer, Long>();
		for (Map<String,Object> row : rs) {
			ret.put((Integer)row.get("entity_id"), (Long)row.get("num_of_hints"));
		}
		return ret;
	}

	public static void test() throws SQLException{
		setMaximumTemHintsForEachEntity();
		
		Map<Integer, Long> entities = getAllEntitiesIdsWithMoreThen10Hints();
		Set<Integer> keys = entities.keySet();
		if (keys.size() != 0){
			System.out.println("ERROR: exists entities with more then 10 hints, entities id: " + keys.toString());
		}
	}
}
