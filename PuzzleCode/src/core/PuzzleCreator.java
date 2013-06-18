package core;

import ui.MainView;
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
	public static String dbServerAddress = "mysqlserv.cs.tau.ac.il";
	public static String dbServerPort = "3306";
	public static String username = "DbMysql02";
	public static String schemaName = "DbMysql02";
	public static String password = "shakshuka";

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

		appDir = args[0] + System.getProperty("file.separator");
		//	appDir = ui.Utils.getAppDir();
		//String password = args[1];
		sqlDir = args[0] + System.getProperty("file.separator") + "sql"
				+ System.getProperty("file.separator");
		loadFilesDir = appDir + "/temp_yago_files/filtered_tsv_files"
				+ System.getProperty("file.separator");

		if (!Logger.initialize(true)) {
			return;
		}
		
		MainView.start();

	}


}
