package db.utils;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parsing.YagoFileHandler;

import core.Logger;





public class DBConnector {

	public static void initialize() throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
	}

	public static Connection getConnection(String schemaName) throws SQLException {
		return DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/" + schemaName,
				"root",
				"Wilkof21");
	}

	public static void closeConnection(Connection con) throws SQLException {
		con.close();
	}

	public static List<Map<String,Object>> executeQuery(String schemaName, String sql) {
		Connection conn = null;
		try {
			conn = getConnection(schemaName);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Statement stmt = null;
		ResultSet rs = null;
		List<Map<String,Object>> returnList = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			returnList = mapResultSet(rs);
		}
		catch (SQLException e) {
			e.getMessage();
		}
		finally {
			 if (rs != null) {
		            try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		        if (stmt != null) {
		            try {
						stmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		        if (conn != null) {
		        	try {
						closeConnection(conn);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		}
		
        return returnList;
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

	public static void executeSqlScript(String schemaName, String sql) { 

	}

	public static String buildImportSql(String importedFile, String tableTo) {
		String fixedPath = YagoFileHandler.getFilteredTsvFileDestDir().replace("\\", "\\\\");
		return "LOAD DATA LOCAL INFILE '" + fixedPath + importedFile + ".TSV' " +
		"INTO TABLE " + tableTo + " " +
		"fields terminated by '\\t' " +
		"lines terminated by '\\n' " +
		"(yago_id,subject,predicate,object,value);";
	}


	public static int createTable(String schemaname, String tablename)  {
		String sql = buildCreateTableSql(tablename);
		try {
			Connection conn = getConnection(schemaname);
			Statement stmt = conn.createStatement();
			stmt.addBatch("DROP TABLE IF EXISTS " + tablename);
			stmt.addBatch(sql);
			stmt.executeBatch();
			stmt.close();
			closeConnection(conn);
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

	public static int createSchema(String schemaName) {
		return executeSql(schemaName, "CREATE SCHEMA IF NOT EXISTS " + schemaName  + " CHARACTER SET utf8 COLLATE utf8_general_ci;");
	}
	public static int executeSql(String schemaname, String sql) {
		try {
			Connection conn = getConnection(schemaname);
			Statement stmt = conn.createStatement();
			stmt.execute(sql);	
			stmt.close();
			closeConnection(conn);
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
