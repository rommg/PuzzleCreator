package core;

import ui.MainView;
import db.ConnectionPool;

public class PuzzleCreator {

	/**
	 * appDir should end with file separator
	 */
	public static String appDir = "." + System.getProperty("file.separator");;
	public static String sqlDir = "";
	public static String loadFilesDir = "";
	public static String homeDir = System.getProperty("user.home");
	public static ConnectionPool connectionPool = null;

	// These parameters won't be hard coded, need to retrieve them from the user
	// through GUI
	public static String dbServerAddress = "mysqlsrv.cs.tau.ac.il";
	public static String dbServerPort = "3306";
	public static String username = "DbMysql02";
	public static String schemaName = "DbMysql02";
	public static String password = "shakshuka";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		loadFilesDir = appDir + "/temp_yago_files/filtered_tsv_files"
				+ System.getProperty("file.separator");

		if (!Logger.initialize(true)) {
			return;
		}
		
		//GUI starts
		MainView.start();

	}


}
