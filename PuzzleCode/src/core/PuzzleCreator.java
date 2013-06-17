package core;

import java.sql.SQLException;
import ui.MainView;
import ui.Utils;
import db.ConnectionPool;

public class PuzzleCreator {

	/**
	 * appDir should end with file separator
	 */
	public static String appDir = "";
	public static String sqlDir = "";
	public static String loadFilesDir = "";
	public static String homeDir = System.getProperty("user.home");
	public static ConnectionPool connectionPool = null;

	// These parameters won't be hard coded, need to retrieve them from the user
	// through GUI
	public static String dbServerAddress = "localhost";
	public static String dbServerPort = "3305";
	public static String username = "DbMysql02";
	public static String schemaName = "DbMysql02";


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("Not enough arguments");
			return;
		}
		if (args.length == 3) {
			if (args[2].compareTo("true") == 0) {
				dbServerPort = "3305";
				username = "DbMysql02";
			}
		}
		
		MainView.start();
		
		String[] result = Utils.getCredentials();
		dbServerAddress = result[0];
		dbServerPort = result[1];
		username = result[2];

		appDir = args[0] + System.getProperty("file.separator");
	//	appDir = ui.Utils.getAppDir();
		String password = args[1];
		sqlDir = args[0] + System.getProperty("file.separator") + "sql"
				+ System.getProperty("file.separator");
		loadFilesDir = appDir + "/temp_yago_files/filtered_tsv_files"
				+ System.getProperty("file.separator");

		if (!Logger.initialize(true)) {
			return;
		}
		
		password = result[3];

		connectionPool = new ConnectionPool("jdbc:mysql://" + dbServerAddress + ":" + dbServerPort + "/" + schemaName,
				username, password);

		if (!connectionPool.createPool()) {
			ui.Utils.showDBConnectionErrorMessage();
			Logger.writeErrorToLog("Failed to create the Connections Pool.");
			closeAllDBConnections();
			System.exit(0);

		}
		Logger.writeToLog("Connections Pool was created");
	}

	public static void closeAllDBConnections() {
		try {
			connectionPool.closeConnections();
			Logger.writeToLog("Closed all connections");
		} catch (SQLException e) {
			Logger.writeErrorToLog("ConnectionPool failed to close connections"
					+ e.getMessage());
		}
	}

}
