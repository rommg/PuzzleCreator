package main;

import java.sql.SQLException;
//TODO: remove
import massiveImport.HintsHandler;
import massiveImport.YagoFileHandler;
import gui.MainView;
import connectionPool.*;
import utils.*;

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
	public static String dbServerPort = "3306";
	public static String username = "root";
	// public static String password = ""; // enter your password
	public static String schemaName = "DbMysql02";
	//public static String schemaName = "riddle";


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("Not enough arguments");
			return;
		}
		if (args.length == 3){
			if (args[2].compareTo("true")==0){
				dbServerPort = "3305";
				username = "DbMysql02";
			}
		}
		appDir = args[0] + System.getProperty("file.separator");
		String password = args[1];
		sqlDir = args[0] + System.getProperty("file.separator") + "sql" + System.getProperty("file.separator");
		loadFilesDir = sqlDir + "filesToLoad" + System.getProperty("file.separator");

		if (!Logger.initialize(true)) {
			return;
		}

		MainView.start();

		connectionPool = new ConnectionPool("jdbc:mysql://" + dbServerAddress + ":" + dbServerPort + "/" + schemaName,
				username, password);

		int tries = 1;
		while (!connectionPool.createPool()) {
			if (tries++ == 4) { // upon third failed attempt to restart, quit.
				closeAllDBConnections();
				System.exit(0);
			}
			Logger.writeErrorToLog("Failed to create the Connections Pool on try #" +tries +".");
			gui.Utils.showDBConnectionErrorMessage();
			MainView.start();
				
		}

		Logger.writeToLog("Connections Pool was created");

		//		 TODO: To Delete
		//		DBUtils.test();
		//		
		//		HintsHandler.test();
		//		DBUtils.getTriviaQuestion();
		//
		//		int[] topics = {1,2};
		//		AlgorithmWorker aw = new AlgorithmWorker(null, topics, 0);
		//		
		//		HintsHandler.test();
		//		DBUtils.getTriviaQuestion();
				YagoFileHandler yf = new YagoFileHandler(null);
				try {
					Logger.writeToLog("begin 05");
					yf.importFilesToDB();
					Logger.writeToLog("finished 05, begin 06");
					yf.populateDB();
					Logger.writeToLog("finished 06");
				} catch (Exception ex){
			
				}

		// MassiveImporter.runMassiveImporter();
		// AlgorithmRunner.runAlgorithm();
		// GuiAlgorithmConnector guiAlConnect = new GuiAlgorithmConnector();

		// TODO: move the call to closeAllDBConnections to the MainView thread.
		// closeAllDBConnections();

	}

	public static void closeAllDBConnections() {
		try {
			connectionPool.closeConnections();
			Logger.writeToLog("Closed all connections");
		} catch (SQLException e) {
			Logger.writeErrorToLog("ConnectionPool failed to close connections" + e.getMessage());
		}
	}

}
