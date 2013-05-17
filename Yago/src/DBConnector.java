import java.sql.*;

public class DBConnector {

	public static void initialize() throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/blah",
				"root",
				"Wilkof21");
	}

	public static void closeConnection(Connection con) throws SQLException {
		con.close();
	}

	public static int createTable(String sql)  {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);	
			stmt.close();
			closeConnection(conn);
		}
		catch (SQLException e) {
			return 0;
		}
		return 1;
	}

	public static int executeSql( String sql) {
		try {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.execute(sql);	
		stmt.close();
		closeConnection(conn);
		}
		catch (SQLException e){
			return 0;
		}
		return 1;
	}
}
