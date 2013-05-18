import java.sql.*;

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
	
	public static String buildImportSql(String importedFile, String tableTo) {
		String fixedPath = YagoFileHandler.FILTERED_TSV_FILE_DEST_DIR.replace("\\", "\\\\");
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
