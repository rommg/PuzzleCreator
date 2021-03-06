package db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import core.Logger;
import core.PuzzleCreator;


public class DBConnection {

	private static final String EMPTY_STRING = "";

	private static Connection getConnection() throws SQLException{
		Connection conn = null;

		try {
			conn = PuzzleCreator.connectionPool.getConnection();
		} catch (SQLException e) {
			Logger.writeErrorToLog("DBConnection failed to get connection from pool " + e.getMessage());
			throw e;
		}
		return conn;
	}

	private static void freeConnection(Connection conn) {
		if (conn != null) {
			if (PuzzleCreator.connectionPool.returnConnection(conn)) {
				Logger.writeToLog("DBConnection freeConnection: Successfully free the connection");
			} else {
				Logger.writeErrorToLog("DBConnection freeConnection: Failed to free the connection");
			}
		}
	}

	/**
	 * This method executes a SQL query : pools a free connection, creates
	 * statement and uses its executeQuery(String query) function. When finished
	 * it closes the resources and return the connection back to the connection
	 * pool.
	 * 
	 * @param sqlQuery
	 *            - the string query that need to be execute.
	 * @return List of Map<String, Object>> where String is the attribute and
	 *         Object is the data, the list is null if there's an SQL Exception.
	 */
	public static List<Map<String, Object>> executeQuery(String sqlQuery) throws SQLException {
		Connection conn = getConnection();

		Statement stmt = null;
		ResultSet rs = null;
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			returnList = mapResultSet(rs);
		} catch (SQLException e) {
			Logger.writeErrorToLog("DBConnection executeQuery: " + e.getMessage());
			throw new SQLException("SQL error", e);
		} finally {
			safelyClose(rs, stmt, conn, null);
		}

		return returnList;
	}

	/**
	 * This method executes a SQL update statement : pools a free connection,
	 * creates statement and uses its executeUpdate(String query) function. When
	 * finished it closes the resources and return the connection back to the
	 * connection pool.
	 * 
	 * @param sqlUpdate
	 *            - the update statement that need to be execute.
	 * @return an integer - the numbers of rows that were updated (DML) or 0
	 *         (DDL), -1 on failure.
	 */
	public static int executeUpdate(String sqlUpdate) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = null;
		int result = -1;
		try {
			stmt = conn.createStatement();
			result = stmt.executeUpdate(sqlUpdate);
		} catch (SQLException e) {
			Logger.writeErrorToLog("DBConnection executeUpdate: " + e.getMessage());
			throw new SQLException("SQL error", e);
		} finally {
			safelyClose(null, stmt, conn, null);
		}

		return result;
	}

	public static void deleteEntityDefinition(int entityId, int definitionId) throws SQLException{
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		String sqlQuery = "DELETE FROM entities_definitions WHERE entity_id = ? AND definition_id = ?;";
		try {
			pstmt = conn.prepareStatement(sqlQuery);
			pstmt.setInt(1, entityId);
			pstmt.setInt(2, definitionId);
			pstmt.executeUpdate();
			Logger.writeToLog("deleteEntityDefinition deleted entity " +entityId+ " and definition "+definitionId);
		} catch (SQLException e){
			Logger.writeErrorToLog("deleteEntityDefinition failed deletion from entities_definition. " + e.getMessage());
			throw new SQLException("SQL error", e);
		}
		finally {
			safelyClose(null, null, conn, pstmt);
		}		
	}


	public static void deleteHint(int hint_id) throws SQLException{
		Connection conn = getConnection();
		PreparedStatement pstmt = null;

		String sqlQuery = "DELETE FROM hints WHERE id = ?;";
		try {
			pstmt = conn.prepareStatement(sqlQuery);
			pstmt.setInt(1, hint_id);
			pstmt.executeUpdate();
			Logger.writeToLog("deleteHint deleted hint " + hint_id);
		} catch (SQLException e){
			Logger.writeErrorToLog("deleteHint failed deletion from hints. " + e.getMessage());
			throw new SQLException("SQL error", e);
		}
		finally {
			safelyClose(null, null, conn, pstmt);
		}	
	}


	public static void deleteEntity(int entityId) throws SQLException{
		Connection conn = getConnection();
		PreparedStatement pstmt = null;

		String sqlQuery1 = "DELETE FROM entities_definitions WHERE entity_id = ?;";
		String sqlQuery2 = "DELETE FROM answers WHERE entity_id = ?;";
		String sqlQuery3 = "DELETE FROM hints WHERE entity_id = ?;";
		String sqlQuery4 = "DELETE FROM entities WHERE id = ?;";
		try {
			pstmt = conn.prepareStatement(sqlQuery1);
			pstmt.setInt(1, entityId);
			pstmt.executeUpdate();
			safelyClose(null, null, null, pstmt);
			Logger.writeToLog("deleteEntity deleted entity " +entityId+ " from entities_definitions" );

			pstmt = conn.prepareStatement(sqlQuery2);
			pstmt.setInt(1, entityId);
			pstmt.executeUpdate();
			safelyClose(null, null, null, pstmt);
			Logger.writeToLog("deleteEntity deleted entity " +entityId+ " from answers" );

			pstmt = conn.prepareStatement(sqlQuery3);
			pstmt.setInt(1, entityId);
			pstmt.executeUpdate();
			safelyClose(null, null, null, pstmt);
			Logger.writeToLog("deleteEntity deleted entity " +entityId+ " from hints" );

			pstmt = conn.prepareStatement(sqlQuery4);
			pstmt.setInt(1, entityId);
			pstmt.executeUpdate();
			Logger.writeToLog("deleteEntity deleted entity " +entityId+ " from entities" );

		} catch (SQLException e){
			Logger.writeErrorToLog("deleteEntity failed deletion. " + e.getMessage());
			throw new SQLException("SQL error", e);
		}
		finally {
			safelyClose(null, null, conn, pstmt);
		}	
	}


	/**
	 * Delete from "hints" table all the rows with ID that included in the
	 * "hintIdToDelete" list. The method executes all DELETE updates as a single
	 * transaction.
	 * 
	 * @param hintIdToDelete
	 *            - List of Integers indicating rows to be deleted from the
	 *            table.
	 * @return 1 on success, 0 if commit failed but rollback succeed and -1 if
	 *         both commit and rollback failed.
	 */
	public static int excuteDeleteHintsByIds(Set<Integer> hintIdToDelete) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = null;
		int result = -1;

		try {
			conn.setAutoCommit(false);
			stmt = conn.createStatement();

			for (Integer hintId : hintIdToDelete) {
				stmt.addBatch("DELETE FROM hints WHERE id=" + hintId + ";");
			}
			stmt.executeBatch();
			conn.commit();
			Logger.writeToLog("Commited DELETE FROM hints Successfully");
			result = 1;

		} catch (SQLException e) {
			Logger.writeErrorToLog("excuteDeleteHintsByIds failed deletion, try rollback : " + e.getMessage());
			try {
				// try rolling back
				conn.rollback();
				Logger.writeToLog("excuteDeleteHintsByIds Rollback Successfully");
				result = 0;
				throw new SQLException("excuteDeleteHintsByIds failed deletion, and rolled back",e);
			} catch (SQLException e2) {
				Logger.writeErrorToLog("excuteDeleteHintsByIds failed when rollbacking - " + e2.getMessage());
				throw new SQLException("roll back failed", e2);
			}
		} finally {
			safelySetAutoCommit(conn);
			safelyClose(null, stmt, conn, null);
		}
		return result;
	}


	public static int addDefinition(String definition) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int maxDefinitionId = getMaxDefinitionId();
		int id = -1;
		String USER_DEF = "<userDefinition>" + maxDefinitionId;

		String sqlQuery = "INSERT INTO definitions (yago_type, definition) VALUES (? , ?);";
		try {
			pstmt = conn.prepareStatement(sqlQuery, new String[] { "ID" });
			pstmt.setString(1, USER_DEF);
			pstmt.setString(2, definition);
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			rs.next();
			id = rs.getInt(1);
			Logger.writeToLog("addDefinition inserted into definitions: " + definition + " ID is " + id);

		} catch (SQLException e){
			Logger.writeErrorToLog("addDefinition failed insertion to definitions. " + e.getMessage());
			throw new SQLException("SQL error", e);
		}
		finally {
			safelyClose(rs, null, conn, pstmt);
		}	

		return id;
	}


	private static int getMaxDefinitionId() throws SQLException{
		String sqlQuery = "SELECT max(id) as max_id FROM definitions;";
		List<Map<String,Object>> rs = executeQuery(sqlQuery);
		if (rs.size() == 0){
			throw new SQLException ("Error: definitions table seems to be empty");
		}
		return (Integer)(rs.get(0).get("max_id"));
	}

	public static void setTopicsToDefinition(int definitionId,	List<Integer> topics) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		String sqlQuery = "INSERT INTO definitions_topics (definition_id, topic_id) VALUES (?, ?);";
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sqlQuery);
			for (Integer topicId: topics) {
				pstmt.setInt(1, definitionId);
				pstmt.setInt(2, topicId);
				pstmt.addBatch();
			}

			pstmt.executeBatch();
			conn.commit();
			Logger.writeToLog("setTopicsToDefinition inserted topics_id to definition_topics where definition_id = "+definitionId);

		} catch (SQLException e) {
			Logger.writeErrorToLog("setTopicsToDefinition failed insertion to definitions_topics. " + e.getMessage());
			throw new SQLException("SQL error", e);
		}
		finally {
			safelySetAutoCommit(conn);
			safelyClose(null, null, conn, pstmt);
		}		
	}


	public static void setNewDefinition(int entityId, int definitionId) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement pstmt = null;

		String sqlQuery = "INSERT INTO entities_definitions (entity_id, definition_id) VALUES (?, ?);";
		try {
			pstmt = conn.prepareStatement(sqlQuery);
			pstmt.setInt(1, entityId);
			pstmt.setInt(2, definitionId);
			pstmt.executeUpdate();
			Logger.writeToLog("setNewDefinition inserted into entities_definitions: entityId " 
					+entityId+ " definitionId "+definitionId);

		} catch (SQLException e){
			Logger.writeErrorToLog("setNewDefinition failed insertion to entities_definitions. " + e.getMessage());
			throw new SQLException("SQL error", e);
		}
		finally {
			safelyClose(null, null, conn, pstmt);
		}		
	}


	public static int addEntity(String entity) throws SQLException{
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int id = -1;

		String sqlQuery = "INSERT INTO entities (name) VALUES (?);";
		try {
			pstmt = conn.prepareStatement(sqlQuery, new String[] { "ID" });
			pstmt.setString(1, entity);
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			rs.next();
			id = rs.getInt(1);
			Logger.writeToLog("addEntity inserted into entities: " + entity + " ID is " + id);

		} catch (SQLException e){
			Logger.writeErrorToLog("addEntity failed insertion to entities. " + e.getMessage());
			throw new SQLException("SQL error", e);
		}
		finally {
			safelyClose(rs, null, conn, pstmt);
		}	

		return id;
	}


	public static int addPredicate(String hint) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int maxPredicateId = getMaxPredicateId();
		String USER_HINT = "<user_hint>" + maxPredicateId;
		int id = -1;

		if (conn == null) {
			ui.Utils.showDBConnectionErrorMessage();
			return id;
		}
		String sqlQuery = "INSERT INTO predicates (yago_predicate, subject_str) VALUES (? , ?);";
		try {
			pstmt = conn.prepareStatement(sqlQuery, new String[] { "ID" });
			pstmt.setString(1, USER_HINT);
			pstmt.setString(2, hint);
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			rs.next();
			id = rs.getInt(1);
			Logger.writeToLog("addPredicate inserted into predicates: " + hint + " ID is " + id);

		} catch (SQLException e){
			Logger.writeErrorToLog("addPredicate failed insertion to predicates. " + e.getMessage());
			throw new SQLException("SQL error", e);
		}
		finally {
			safelyClose(rs, null, conn, pstmt);
		}	
		return id;
	}

	private static int getMaxPredicateId() throws SQLException{
		String sqlQuery = "SELECT max(id) as max_id FROM predicates;";
		List<Map<String,Object>> rs = executeQuery(sqlQuery);
		if (rs.size() == 0){
			throw new SQLException ("Error: predicates table seems to be empty");
		}
		return (Integer)(rs.get(0).get("max_id"));
	}

	public static int addHint(int entityId, int predicateId) throws SQLException{
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int id = -1;
		String sqlQuery = "INSERT INTO hints (predicate_id, entity_id, is_entity_subject) VALUES (?, ?, true);";
		try {
			pstmt = conn.prepareStatement(sqlQuery, new String[] { "ID" });
			pstmt.setInt(1, predicateId);
			pstmt.setInt(2, entityId);
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			rs.next();
			id = rs.getInt(1);
			Logger.writeToLog("addHint inserted into hints: entitiy_id = " + entityId + " . auto-ID is " + id);

		} catch (SQLException e){
			Logger.writeErrorToLog("addHint failed insertion to hints. " + e.getMessage());
			throw new SQLException("SQL error", e);
		}
		finally {
			safelyClose(rs, null, conn, pstmt);
		}	
		return id;
	}


	private static List<Map<String, Object>> mapResultSet(ResultSet rs) throws SQLException {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> row = null;

		ResultSetMetaData metaData = rs.getMetaData();
		Integer columnCount = metaData.getColumnCount();

		while (rs.next()) {
			row = new HashMap<String, Object>();
			for (int i = 1; i <= columnCount; i++) {
				row.put(metaData.getColumnName(i), rs.getObject(i));
			}
			resultList.add(row);
		}
		return resultList;
	}

	/**
	 * 
	 * The method reads a SQL script file and execute each SQL statement.
	 * 
	 * @param sqlScriptFileName
	 *            - file path of the SQL script file. Comments in the script
	 *            must appear after "--" in a new line.
	 * @throws SQLException
	 *             - if roll-backing didn't succeed
	 */
	public static void executeSqlScript(String sqlScriptFileName) throws SQLException, IOException {
		String str = new String();
		StringBuffer strBuffer = new StringBuffer();
		Connection conn = getConnection();
		conn.setAutoCommit(false);
		Statement stmt = conn.createStatement();
		Logger.writeToLog("Start to execute SQL script file: " + sqlScriptFileName);

		try {
			InputStreamReader in = new InputStreamReader(DBConnection.class.getClassLoader().getResourceAsStream("resources/" + sqlScriptFileName));
			BufferedReader bufferedReader = new BufferedReader(in);

			while ((str = bufferedReader.readLine()) != null) {
				if (str.startsWith("--")) {
					continue;
				}
				int pathIndex = str.indexOf("???");
				if (pathIndex >= 0) {
					String lineStart = str.substring(0, pathIndex);
					String lineEnd = str.substring(pathIndex + 3);
					str = lineStart + PuzzleCreator.loadFilesDir + lineEnd;
					int slashIndex = str.indexOf(System.getProperty("file.separator"));
					while (slashIndex >= 0 && (System.getProperty("file.separator").compareTo("/") !=0)) {
						lineStart = str.substring(0, slashIndex);
						lineEnd = str.substring(slashIndex+1);
						str = lineStart + "/" +  lineEnd;
						slashIndex = str.indexOf(System.getProperty("file.separator"));
					}
				}

				strBuffer.append(str);
			}
			bufferedReader.close();

			// Use ";" as a delimiter for each request
			String[] instruction = strBuffer.toString().split(";");

			for (int i = 0; i < instruction.length; i++) {
				if (!instruction[i].trim().equals(EMPTY_STRING)) {
					stmt.executeUpdate(instruction[i]);
					Logger.writeToLog(instruction[i] + ";");
				}
			}
			conn.commit();
			Logger.writeToLog("Commited transaction Successfully");
		} catch (SQLException sqlE) {
			Logger.writeErrorToLog("Transaction is not complete: " + sqlE.getMessage());
			try {
				// try rolling back
				conn.rollback();
				Logger.writeToLog("Rollback Successfully");

				throw new SQLException("failed to execute script - sql error", sqlE);
			} catch (SQLException sqlE2) {
				Logger.writeErrorToLog("failed when rollbacking - " + sqlE2.getMessage());

				throw new SQLException("failed roll back in executeSQLScripit", sqlE2);
			}
		} catch (IOException e) {

			Logger.writeErrorToLog("DBConnection executeSqlScript: " + e.getMessage());
			throw new IOException("IOException when opening script file", e);
		} finally {
			safelySetAutoCommit(conn);
			safelyClose(null, stmt, conn, null);
		}

	}

	/**
	 * Attempts to close all the given resources.
	 * 
	 * @param resources
	 *            , any of which may be null.
	 */
	private static void safelyClose(ResultSet rs, Statement stmt, Connection conn, PreparedStatement pstmt)  {

		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				Logger.writeErrorToLog("DBConnection safelyClose ResultSet: " + e.getMessage());
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				Logger.writeErrorToLog("DBConnection safelyClose Statement:" + e.getMessage());
			}
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				Logger.writeErrorToLog("DBConnection safelyClose PreparedStatement:" + e.getMessage());
			}
		}
		if (conn != null) {
			freeConnection(conn);
		}

	}

	/**
	 * Attempts to set the connection back to auto-commit, writing errors to
	 * log.
	 */
	private static void safelySetAutoCommit(Connection conn) throws SQLException{
		try {
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			Logger.writeErrorToLog("failed to set auto commit" + e.getMessage());
			throw new SQLException("failed to set auto commit", e);
		}
	}



	public static void insreatIntoYagoTables(String yagoTypes_tsv, String sql, int numOfArguments) throws SQLException, IOException{
		String line = new String();
		Connection conn = null;
		PreparedStatement pStmt = null;
		//		int numOfArguments = 5;
		try{
			conn = getConnection();
			conn.setAutoCommit(false);
			//			String sql = "INSERT INTO yago_type (subject, predicate, object, answer, additional_information) VALUES (?,?,?,?,?);";
			pStmt = conn.prepareStatement(sql);

			FileReader fr = new FileReader(new File(yagoTypes_tsv));
			BufferedReader bufferedReader = new BufferedReader(fr);

			while ((line = bufferedReader.readLine()) != null) {
				String[] values = line.split("\t");
				for (int i = 0; i < values.length; i++) {
					pStmt.setString(i+1, values[i]);	
				}
				if (values.length < numOfArguments ){
					for (int i = values.length; i < numOfArguments; i++) {
						pStmt.setString(i+1, EMPTY_STRING);		
					}
				}
				pStmt.addBatch();
			}
			bufferedReader.close();
			fr.close();
			pStmt.executeBatch();
			conn.commit();
		} catch (SQLException sqlE) {
			Logger.writeErrorToLog("Transaction is not complete: " + sqlE.getMessage());
			try {
				// try rolling back
				conn.rollback();
				Logger.writeToLog("Rollback Successfully");
				throw new SQLException("Failed and rollback ",sqlE);
			} catch (SQLException sqlE2) {
				Logger.writeErrorToLog("failed when rollbacking - " + sqlE2.getMessage());
				throw new SQLException("rollback failed",sqlE2);
			}
		} catch (IOException ioE) {
			Logger.writeErrorToLog("Failed to read file: " + yagoTypes_tsv + ioE.getMessage());
			throw new IOException("Failed to read file yagoTypes_tsv" + yagoTypes_tsv, ioE);
		} finally {
			safelySetAutoCommit(conn);
			safelyClose(null, pStmt, conn, null);
		}
	}


	public static void insreatIntoYagoType(String yagoTypes_tsv) throws SQLException, IOException{
		int numOfArguments = 5;
		String sql = "INSERT INTO yago_type (subject, predicate, object, answer, additional_information) VALUES (?,?,?,?,?);";
		insreatIntoYagoTables(yagoTypes_tsv, sql, numOfArguments);
	}

	public static void insreatIntoYagoFact(String yagoFacts_tsv) throws SQLException, IOException{
		int numOfArguments = 4;
		String sql = "INSERT INTO yago_fact (subject, predicate, object, is_subject) VALUES (?,?,?,?);";
		insreatIntoYagoTables(yagoFacts_tsv, sql, numOfArguments);
	}


	public static void insreatIntoYagoLiteralFacts(String yagoLiteralFacts_tsv) throws SQLException, IOException{
		int numOfArguments = 3;
		String sql = "INSERT INTO yago_literal_fact (subject, predicate, object) VALUES (?,?,?);";
		insreatIntoYagoTables(yagoLiteralFacts_tsv, sql, numOfArguments);
	}


	public static void insreatIntoYagoHumanAnswers(String yagoHumanAnswers_tsv) throws SQLException, IOException{
		int numOfArguments = 3;
		String sql = "INSERT INTO temp_answers (entity, answer, additional_information) VALUES (?,?,?);";
		insreatIntoYagoTables(yagoHumanAnswers_tsv, sql, numOfArguments);
	}

	
	public static void test(){
		java.util.Date end = null;
		java.util.Date start = null;
		try{
			start = Calendar.getInstance().getTime();
			System.out.println("Starts at:" + start);
			
			insreatIntoYagoType("c://Users//kleins//tau//db//git//PuzzleCreator//temp_yago_files//filtered_tsv_files//yagoTypes.tsv");
			end = Calendar.getInstance().getTime();
			System.out.println("end yagoTypes" );
			System.out.println("total time:" + (end.getTime()-start.getTime())/60000);
			
			insreatIntoYagoFact("c://Users//kleins//tau//db//git//PuzzleCreator//temp_yago_files//filtered_tsv_files//yagoFacts.tsv");
			end = Calendar.getInstance().getTime();
			System.out.println("end yagoFacts" );
			System.out.println("total time:" + (end.getTime()-start.getTime())/60000);
			
			insreatIntoYagoLiteralFacts("c://Users//kleins//tau//db//git//PuzzleCreator//temp_yago_files//filtered_tsv_files//yagoLiteralFacts.tsv");
			end = Calendar.getInstance().getTime();
			System.out.println("end yagoLiteralFacts" );
			System.out.println("total time:" + (end.getTime()-start.getTime())/60000);
			
			insreatIntoYagoHumanAnswers("c://Users//kleins//tau//db//git//PuzzleCreator//temp_yago_files//filtered_tsv_files//yagoHumanAnswers.tsv");
			end = Calendar.getInstance().getTime();
			System.out.println("End at: " + end);
			System.out.println("total time:" + (end.getTime()-start.getTime())/60000);
			
		}
		catch(Exception e){
			end = Calendar.getInstance().getTime();
			System.out.println("Error at:" + end);
			System.out.println("total time:" + (end.getTime()-start.getTime())/60000);
			e.printStackTrace();
			
		}
	}

	public static void addAnswer(String answer, int length, String additionalInfo, long entityId) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sqlQuery = "INSERT INTO answers (answer, length, additional_information, entity_id) VALUES (?, ?, ?, ?);";
		try {
			pstmt = conn.prepareStatement(sqlQuery, new String[] { "ID" });
			pstmt.setString(1, answer);
			pstmt.setInt(2, length);
			pstmt.setString(3, additionalInfo);
			pstmt.setLong(4, entityId);
			pstmt.executeUpdate();

		} catch (SQLException e){
			throw new SQLException("SQL error", e);
		}
		finally {
			safelyClose(rs, null, conn, pstmt);
		}	
	}


}
