package main;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import gui.MainView;
import connectionPool.*;
import puzzleAlgorithm.*;
import utils.*;
import massiveImport.*;

public class PuzzleCreator {

	/**
	 * appDir should end with file separator
	 */
	public static String appDir = "";
	public static String homeDir = System.getProperty("user.home");
	public static ConnectionPool connectionPool = null;

	// These parameters won't be hard coded, need to retrieve them from the user
	// through GUI
	public static String dbServerAddress = "localhost";
	public static String dbServerPort = "3306";
	public static String username = "root";
	// public static String password = ""; // enter your password
	public static String schemaName = "riddle";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("Wrong number of arguments");
			return;
		}
		appDir = args[0];
		String password = args[1];

		Logger.initialize(true);
		connectionPool = new ConnectionPool("jdbc:mysql://" + dbServerAddress + ":" + dbServerPort + "/" + schemaName,
				username, password);
		if (!connectionPool.createPool()) {
			Logger.writeErrorToLog("Failed to create the Connections Pool");
			return;
		}
		Logger.writeToLog("Connections Pool was created");
		
//		// TODO: To Delete
////		DBUtils.test();
//		//
//
//		//HintsHandler.test();
//		DBUtils.getTriviaQuestion();
//
////		int[] topics = {1,2};
////		AlgorithmWorker aw = new AlgorithmWorker(null, topics, 0);
//		
//		HintsHandler.test();
////		DBUtils.getTriviaQuestion();

		MainView.start();

		//createDB();

		//MassiveImporter.runMassiveImporter();
		//AlgorithmRunner.runAlgorithm();
		//GuiAlgorithmConnector guiAlConnect = new GuiAlgorithmConnector();
		
		
		//TODO: move the call to closeAllDBConnections to the MainView thread. 
		//closeAllDBConnections();

	}

	public static void closeAllDBConnections() {
		try {
			connectionPool.closeConnections();
			Logger.writeToLog("Closed all connections");
		} catch (SQLException e) {
			Logger.writeErrorToLog("ConnectionPool failed to close connections" + e.getMessage());
		}
	}

	/**
	 * Create the database - run the SQL scripts. 
	 */
	private static void createDB() {
		try {
			DBConnection.executeSqlScript(appDir + "sql\\00 create_schema_and_tables.sql");
			DBConnection.executeSqlScript(appDir + "sql\\01 insert_default_topics.sql");
			DBConnection.executeSqlScript(appDir + "sql\\02 insert_default_definitions.sql");
			DBConnection.executeSqlScript(appDir + "sql\\03 insert_default_definitions_topics.sql");
			DBConnection.executeSqlScript(appDir + "sql\\04 insert_default_predicates.sql");
			DBConnection.executeSqlScript(appDir + "sql\\05 load_yago_data.sql");
			DBConnection.executeSqlScript(appDir + "sql\\06 create_relevant_data.sql");
		} catch (SQLException e) {
			Logger.writeErrorToLog("Executing SQL script failed" + e.getMessage());
			//TODO: Report an issue to the GUI
		}
	}

}
