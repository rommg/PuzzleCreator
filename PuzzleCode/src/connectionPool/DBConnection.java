package connectionPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.Logger;
import main.*;
import massiveImport.YagoFileHandler;

public class DBConnection {

	private static Connection getConnection() {
		Connection conn = null;

		try {
			conn = PuzzleCreator.connectionPool.getConnection();
		} catch (SQLException e) {
			Logger.writeErrorToLog("DBConnection failed to get connection from pool " + e.getMessage());
			gui.Utils.showDBConnectionErrorMessage();
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
	public static List<Map<String, Object>> executeQuery(String sqlQuery) {
		Connection conn = getConnection();
		if (conn == null) {
			throw new RuntimeException();
		}

		Statement stmt = null;
		ResultSet rs = null;
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			returnList = mapResultSet(rs);
		} catch (SQLException e) {
			Logger.writeErrorToLog("DBConnection executeQuery: " + e.getMessage());
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
	public static int executeUpdate(String sqlUpdate) throws RuntimeException {
		Connection conn = getConnection();
		Statement stmt = null;
		int result = -1;

		if (conn == null) {
			throw new RuntimeException();
		}
		try {
			stmt = conn.createStatement();
			result = stmt.executeUpdate(sqlUpdate);
		} catch (SQLException e) {
			Logger.writeErrorToLog("DBConnection executeUpdate: " + e.getMessage());
		} finally {
			safelyClose(null, stmt, conn, null);
		}

		return result;
	}

	public static void deleteEntityDefinition(int entityId, int definitionId) {
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		if (conn == null) {
			gui.Utils.showDBConnectionErrorMessage();
			return;
		}

		String sqlQuery = "DELETE FROM entities_definitions WHERE entity_id = ? AND definition_id = ?;";
		try {
			pstmt = conn.prepareStatement(sqlQuery);
			pstmt.setInt(1, entityId);
			pstmt.setInt(2, definitionId);
			pstmt.executeUpdate();
			Logger.writeToLog("deleteEntityDefinition deleted entity " +entityId+ " and definition "+definitionId);
		} catch (SQLException e){
			Logger.writeErrorToLog("deleteEntityDefinition failed deletion from entities_definition. " + e.getMessage());
		}
		finally {
			safelyClose(null, null, conn, pstmt);
		}		
	}


	public static void deleteHint(int hint_id){
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		if (conn == null) {
			gui.Utils.showDBConnectionErrorMessage();
			return;
		}

		String sqlQuery = "DELETE FROM hints WHERE id = ?;";
		try {
			pstmt = conn.prepareStatement(sqlQuery);
			pstmt.setInt(1, hint_id);
			pstmt.executeUpdate();
			Logger.writeToLog("deleteHint deleted hint " + hint_id);
		} catch (SQLException e){
			Logger.writeErrorToLog("deleteHint failed deletion from hints. " + e.getMessage());
		}
		finally {
			safelyClose(null, null, conn, pstmt);
		}	
	}


	public static void deleteEntity(int entityId){
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		if (conn == null) {
			gui.Utils.showDBConnectionErrorMessage();
			return;
		}

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
	public static int excuteDeleteHintsByIds(Set<Integer> hintIdToDelete) {
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
			} catch (SQLException e2) {
				Logger.writeErrorToLog("excuteDeleteHintsByIds failed when rollbacking - " + e2.getMessage());
			}
		} finally {
			safelySetAutoCommit(conn);
			safelyClose(null, stmt, conn, null);
		}
		return result;
	}


	public static int addDefinition(String definition) {
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int maxDefinitionId = getMaxDefinitionId();
		int id = -1;
		String USER_DEF = "<userDefinition>" + maxDefinitionId;

		if (conn == null) {
			gui.Utils.showDBConnectionErrorMessage();
			return id;
		}

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
		}
		finally {
			safelyClose(rs, null, conn, pstmt);
		}	

		return id;
	}


	private static int getMaxDefinitionId() {
		String sqlQuery = "SELECT max(id) as max_id FROM definitions;";
		List<Map<String,Object>> rs = executeQuery(sqlQuery);
		if (rs.size() == 0){
			//TODO: ERROR
		}
		return (Integer)(rs.get(0).get("max_id"));
	}

	public static void setTopicsToDefinition(int definitionId,	List<Integer> topics) {
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		if (conn == null) {
			gui.Utils.showDBConnectionErrorMessage();
			return;
		}

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
		}
		finally {
			safelySetAutoCommit(conn);
			safelyClose(null, null, conn, pstmt);
		}		
	}


	public static void setNewDefinition(int entityId, int definitionId) {
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		if (conn == null) {
			gui.Utils.showDBConnectionErrorMessage();
			return;
		}

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
		}
		finally {
			safelyClose(null, null, conn, pstmt);
		}		
	}


	public static int addEntity(String entity) {
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int id = -1;

		if (conn == null) {
			gui.Utils.showDBConnectionErrorMessage();
			return id;
		}
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
		}
		finally {
			safelyClose(rs, null, conn, pstmt);
		}	

		return id;
	}


	public static int addPredicate(String hint) {
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int maxPredicateId = getMaxPredicateId();
		String USER_HINT = "<user_hint>" + maxPredicateId;
		int id = -1;

		if (conn == null) {
			gui.Utils.showDBConnectionErrorMessage();
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
		}
		finally {
			safelyClose(rs, null, conn, pstmt);
		}	
		return id;
	}

	private static int getMaxPredicateId() {
		String sqlQuery = "SELECT max(id) as max_id FROM predicates;";
		List<Map<String,Object>> rs = executeQuery(sqlQuery);
		if (rs.size() == 0){
			//TODO: ERROR
		}
		return (Integer)(rs.get(0).get("max_id"));
	}

	public static int addHint(int entityId, int predicateId) {
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int id = -1;

		if (conn == null) {
			gui.Utils.showDBConnectionErrorMessage();
			return id;
		}
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
		}
		finally {
			safelyClose(rs, null, conn, pstmt);
		}	
		return id;
	}
// TODO: remove
//	public static int getDefintionId(String definition){
//		Connection conn = getConnection();
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		int id = -1;
//
//		if (conn == null) {
//			gui.Utils.showDBConnectionErrorMessage();
//			return id;
//		}
//		String sqlQuery = "SELECT id FROM definitions WHERE defenition like ?;";
//		try {
//			pstmt = conn.prepareStatement(sqlQuery);
//			pstmt.setString(1, "\""+ definition + "\"");
//			pstmt.executeQuery();
//			rs = pstmt.getGeneratedKeys();
//			rs.next();
//			id =  rs.getInt(1);
//
//		} catch (SQLException e){
//			Logger.writeErrorToLog("addHint failed insertion to hints. " + e.getMessage());
//		}
//		finally {
//			safelyClose(rs, null, conn, pstmt);
//		}	
//		return id;
//	}

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
	 * @param sqlScriptPath
	 *            - file path of the SQL script file. Comments in the script
	 *            must appear after "--" in a new line.
	 * @throws SQLException
	 *             - if roll-backing didn't succeed
	 */
	public static void executeSqlScript(String sqlScriptPath) throws SQLException {
		String str = new String();
		StringBuffer strBuffer = new StringBuffer();
		Connection conn = getConnection();
		conn.setAutoCommit(false);
		Statement stmt = conn.createStatement();
		Logger.writeToLog("Start to execute SQL script file: " + sqlScriptPath);

		try {
			FileReader fr = new FileReader(new File(sqlScriptPath));
			BufferedReader bufferedReader = new BufferedReader(fr);

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
				if (!instruction[i].trim().equals("")) {
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
			} catch (SQLException sqlE2) {
				Logger.writeErrorToLog("failed when rollbacking - " + sqlE2.getMessage());
				throw new SQLException();
				// TODO: alert the calling method that committing the script had
				// failed
			}
		} catch (Exception e) {
			Logger.writeErrorToLog("DBConnection executeSqlScript: " + e.getMessage());
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
	private static void safelyClose(ResultSet rs, Statement stmt, Connection conn, PreparedStatement pstmt) {

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
	private static void safelySetAutoCommit(Connection conn) {
		try {
			conn.setAutoCommit(true);
		} catch (Exception e) {
			Logger.writeErrorToLog("failed to set auto commit" + e.getMessage());
		}
	}

	// Methods from DBConnector, not yet used here: //
	// ////////////////////////////////////////////////

	private static String buildCreateTableSql(String tablename) {
		return "CREATE TABLE " + tablename + " " + "(yago_id varchar(50), " + "subject varchar(50), "
				+ "predicate varchar(50), " + "object varchar(250), " + "value float, "
				+ "id int NOT NULL AUTO_INCREMENT, " + "PRIMARY KEY(id));";

	}

	@SuppressWarnings("unused")
	private static String buildImportSql(String importedFile, String tableTo) {
		String fixedPath = YagoFileHandler.getFilteredTsvFileDestDir().replace("\\", "\\\\");
		return "LOAD DATA LOCAL INFILE '" + fixedPath + importedFile + ".TSV' " + "INTO TABLE " + tableTo + " "
		+ "fields terminated by '\\t' " + "lines terminated by '\\n' "
		+ "(yago_id,subject,predicate,object,value);";
	}

	@SuppressWarnings("unused")
	private static int createTable(String tablename) {
		String sql = buildCreateTableSql(tablename);
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.addBatch("DROP TABLE IF EXISTS " + tablename);
			stmt.addBatch(sql);
			stmt.executeBatch();
			stmt.close();
			freeConnection(conn);
		} catch (SQLSyntaxErrorException e) {
			Logger.writeErrorToLog("Executing: " + sql + " failed: Wrong syntax.");
			return 0;
		} catch (SQLException e) {
			Logger.writeErrorToLog(e.getMessage());
			return 0;
		}
		return 1;
	}

	@SuppressWarnings("unused")
	private static int createSchema(String schemaName) {
		return executeSql(schemaName, "CREATE SCHEMA IF NOT EXISTS " + schemaName
				+ " CHARACTER SET utf8 COLLATE utf8_general_ci;");
	}

	private static int executeSql(String schemaname, String sql) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			stmt.close();
			freeConnection(conn);
		} catch (SQLSyntaxErrorException e) {
			Logger.writeErrorToLog("Executing: " + sql + " failed: Wrong syntax.");
			return 0;
		} catch (SQLException e) {
			Logger.writeErrorToLog(e.getMessage());
			return 0;
		}
		return 1;
	}
}
