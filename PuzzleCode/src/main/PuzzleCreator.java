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

	// These parameters won't be hard coded, need to retrieve them from the user through GUI
	public static String dbServerAddress = "localhost";
	public static String dbServerPort = "3306";
	public static String username = "root";
	public static String password = ""; // enter your password 
	public static String schemaName = "riddle";


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1){
			Logger.writeErrorToLog("Wrong number of arguments");
			return;
		}
		appDir = args[0];
		Logger.initialize(true);		

		connectionPool = new ConnectionPool("jdbc:mysql://"+ dbServerAddress +":"+ dbServerPort +"/"+ schemaName,
				username, password);
		if(!connectionPool.createPool()) {
			Logger.writeErrorToLog("Failed to create the Connections Pool");
			return;
		}
		Logger.writeToLog("Connections Pool was created");
		MainView.start();

		//createDB();

		try {
			connectionPool.closeConnections();
			Logger.writeToLog("Closed all connections");
		} catch (SQLException e) {
			Logger.writeErrorToLog("ConnectionPool failed to close connections" + e.getMessage());
		}

	}


	/**
	 * This method runs SQL scripts 01 02 03 04 
	 */
	private static void createDB() {
		try {
			DBConnection.executeSqlScript(appDir + "sql\\01 insert_default_topics.sql");
			DBConnection.executeSqlScript(appDir + "sql\\02 insert_default_definitions.sql");
			DBConnection.executeSqlScript(appDir + "sql\\03 insert_default_definitions_topics.sql");
			DBConnection.executeSqlScript(appDir + "sql\\04 insert_default_predicates.sql");
		} catch (SQLException e) {
			Logger.writeErrorToLog("Executing SQL script failed" + e.getMessage());
		}

		MassiveImporter.runMassiveImporter();
		AlgorithmRunner.runAlgorithm();
		//GuiAlgorithmConnector guiAlConnect = new GuiAlgorithmConnector();
	}

}
