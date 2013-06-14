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

	private static Connection getConnection() throws SQLException {
		return PuzzleCreator.connectionPool.getConnection();
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
	 * This method executes a SQL query : pools a free connection,
	 * creates statement and uses its executeQuery(String query) function.
	 * When finished it closes the resources and return the connection
	 * back to the connection pool.
	 * @param sqlQuery - the string query that need to be execute.
	 * @return List of Map<String, Object>> where String is the attribute and Object is the data,
	 * the list is null if there's an SQL Exception.
	 */
	public static List<Map<String,Object>> executeQuery(String sqlQuery) {
		Connection conn = null;
		try {
			conn = getConnection();
		} catch (SQLException e) {
			Logger.writeErrorToLog("DBConnection failed to get connection from pool " + e.getMessage());
		}
		Statement stmt = null;
		ResultSet rs = null;
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			returnList = mapResultSet(rs);
		}
		catch (SQLException e) {
			Logger.writeErrorToLog("DBConnection executeQuery: " + e.getMessage());
		}
		finally {
			safelyClose(rs, stmt, conn);
		}		
		
		return returnList;
	}

	/**
	 * This method executes a SQL update statement : pools a free connection,
	 * creates statement and uses its executeUpdate(String query) function.
	 * When finished it closes the resources and return the connection
	 * back to the connection pool.
	 * @param sqlUpdate - the update statement that need to be execute.
	 * @return an integer - the numbers of rows that were updated (DML) or 0 (DDL),
	 * -1 on failure. 
	 */
	public static int executeUpdate(String sqlUpdate) {
		Connection conn = null;
		Statement stmt = null;
		int result = -1;
		
		try {
			conn = getConnection();
		} catch (SQLException e) {
			Logger.writeErrorToLog("DBConnection failed to get connection from pool " + e.getMessage());
		}
	
		try {
			stmt = conn.createStatement();
			result = stmt.executeUpdate(sqlUpdate);
		} catch (SQLException e) {
			Logger.writeErrorToLog("DBConnection executeUpdate: " + e.getMessage());
		}
		finally {
			safelyClose(null, stmt, conn);
		}
		
		return result;
	}
	
	/**
	 * Delete from "hints" table all the rows with ID that included in 
	 * the "hintIdToDelete" list. 
	 * The method executes all DELETE updates as a single transaction.
	 * @param hintIdToDelete - List of Integers indicating rows to be deleted
	 * from the table.
	 * @return 1 on success, 0 if commit failed but rollback succeed and
	 * -1 if both commit and rollback failed.
	 */
	public static int excuteDeleteHintsByIds(Set<Integer> hintIdToDelete) {
		Connection conn = null;
		Statement stmt = null;
		int result = -1;
		
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			
			for (Integer hintId : hintIdToDelete) {
				stmt.addBatch( "DELETE FROM hints WHERE id=" + hintId + ";" );
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
		}
		finally {
			safelySetAutoCommit(conn);
			safelyClose(null, stmt, conn);			
		}
		return result;
	}
	
	private static List<Map<String,Object>> mapResultSet(ResultSet rs) throws SQLException {
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
	 * @param sqlScriptPath - file path of the SQL script file. 
	 * Comments in the script must appear after "--" in a new line. 
	 * @throws SQLException - if roll-backing didn't succeed 
	 */
	public static void executeSqlScript(String sqlScriptPath) throws SQLException
	{
		String str = new String();
		StringBuffer strBuffer = new StringBuffer();
		Connection conn = getConnection();
		conn.setAutoCommit(false);
		Statement stmt = conn.createStatement();
		Logger.writeToLog("Start to execute SQL script file: " + sqlScriptPath);

		try
		{
			FileReader fr = new FileReader(new File(sqlScriptPath));
			BufferedReader bufferedReader = new BufferedReader(fr);

			while((str = bufferedReader.readLine()) != null) {
				if(str.startsWith("--")) {
					continue;
				}
				strBuffer.append(str);
			}
			bufferedReader.close();

			// Use ";" as a delimiter for each request
			String[] instruction = strBuffer.toString().split(";");

			for(int i = 0; i<instruction.length; i++) {
				if(!instruction[i].trim().equals("")) {
					stmt.executeUpdate(instruction[i]);
					Logger.writeToLog(instruction[i]+";");
				}
			}
			conn.commit();
			Logger.writeToLog("Commited transaction Successfully");
		}
		catch(SQLException sqlE) {
			Logger.writeErrorToLog("Transaction is not complete: " + sqlE.getMessage());
			try {
				// try rolling back
				conn.rollback();
				Logger.writeToLog("Rollback Successfully");
			} catch (SQLException sqlE2) {
				Logger.writeErrorToLog("failed when rollbacking - " + sqlE2.getMessage());
				throw new SQLException();
				// alert the calling method that committing the script had failed
			}
		}
		catch(Exception e) {
			Logger.writeErrorToLog("DBConnection executeSqlScript: " + e.getMessage());
		}
		finally {
			safelySetAutoCommit(conn);
			safelyClose(null, stmt, conn);
		}	

	}
	
	/**
	 * Attempts to close all the given resources.
	 * @param resources, any of which may be null.
	 */
	private static void safelyClose(ResultSet rs, Statement stmt, Connection conn) {
		
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
		if (conn != null) {
			freeConnection(conn);					 
		}
		
	}
	/**
	 * Attempts to set the connection back to auto-commit, writing errors to log.
	 */
	private static void safelySetAutoCommit(Connection conn) {
		try {
			conn.setAutoCommit(true);
		} catch (Exception e) {
			Logger.writeErrorToLog("failed to set auto commit" + e.getMessage());
		}
	}

	// Methods from DBConnector, not yet used here: //
	//////////////////////////////////////////////////

	private static String buildCreateTableSql(String tablename) {
		return "CREATE TABLE "+ tablename + " " +
		"(yago_id varchar(50), " +
		"subject varchar(50), "+ 
		"predicate varchar(50), " + 
		"object varchar(250), " +
		"value float, " +
		"id int NOT NULL AUTO_INCREMENT, " + 
		"PRIMARY KEY(id));";

	}

	@SuppressWarnings("unused")
	private static String buildImportSql(String importedFile, String tableTo) {
		String fixedPath = YagoFileHandler.getFilteredTsvFileDestDir().replace("\\", "\\\\");
		return "LOAD DATA LOCAL INFILE '" + fixedPath + importedFile + ".TSV' " +
		"INTO TABLE " + tableTo + " " +
		"fields terminated by '\\t' " +
		"lines terminated by '\\n' " +
		"(yago_id,subject,predicate,object,value);";
	}


	@SuppressWarnings("unused")
	private static int createTable(String tablename)  {
		String sql = buildCreateTableSql(tablename);
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.addBatch("DROP TABLE IF EXISTS " + tablename);
			stmt.addBatch(sql);
			stmt.executeBatch();
			stmt.close();
			freeConnection(conn);
		}
		catch (SQLSyntaxErrorException e) {
			Logger.writeErrorToLog("Executing: " + sql + " failed: Wrong syntax." );
			return 0;
		}
		catch (SQLException e) {
			Logger.writeErrorToLog(e.getMessage());
			return 0;
		}
		return 1;
	}

	@SuppressWarnings("unused")
	private static int createSchema(String schemaName) {
		return executeSql(schemaName, "CREATE SCHEMA IF NOT EXISTS " + schemaName  + " CHARACTER SET utf8 COLLATE utf8_general_ci;");
	}
	private static int executeSql(String schemaname, String sql) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.execute(sql);	
			stmt.close();
			freeConnection(conn);
		}
		catch (SQLSyntaxErrorException e) {
			Logger.writeErrorToLog("Executing: " + sql + " failed: Wrong syntax." );
			return 0;
		}
		catch (SQLException e){
			Logger.writeErrorToLog(e.getMessage());
			return 0;
		}
		return 1;
	}
}
