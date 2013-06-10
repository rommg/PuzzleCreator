package massiveImport;
import java.io.IOException;
import java.sql.SQLException;

import main.PuzzleCreator;
import connectionPool.DBConnection;

import utils.Logger;


public class MassiveImporter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static boolean runMassiveImporter() {

		Logger.writeToLog("Starting importing process...");
		
		YagoFileHandler y = new YagoFileHandler();

		Logger.writeToLog("Downloading and extracting yago files from website...");

		y.getFilesFromURL(); // download yago files

		Logger.writeToLog("Filtering TSV files...");

		// create TSVs with relevant data only
		if (y.createFilteredYagoFiles() != 1) {
			return false; 
		}
		
		Logger.writeToLog("Importing TSV files to DB...");
		
		// create DB and write the tsv files into it
		if (!createDB()){
			return false;
		}
		
		y.deleteAllYagoFiles(); // delete all temporary files and folders
				
		Logger.writeToLog("Finished importing process!");
		
		return true;
	}
	
	/**
	 * Creates the database - runs the SQL scripts, creates and fills DB. 
	 * @return return true on success, false if there was an SQLExecption.
	 */
	private static boolean createDB() {
		try {
			DBConnection.executeSqlScript(PuzzleCreator.appDir + "sql\\00 create_schema_and_tables.sql");
			DBConnection.executeSqlScript(PuzzleCreator.appDir + "sql\\01 insert_default_topics.sql");
			DBConnection.executeSqlScript(PuzzleCreator.appDir + "sql\\02 insert_default_definitions.sql");
			DBConnection.executeSqlScript(PuzzleCreator.appDir + "sql\\03 insert_default_definitions_topics.sql");
			DBConnection.executeSqlScript(PuzzleCreator.appDir + "sql\\04 insert_default_predicates.sql");
			DBConnection.executeSqlScript(PuzzleCreator.appDir + "sql\\05 load_yago_data.sql");
			DBConnection.executeSqlScript(PuzzleCreator.appDir + "sql\\06 create_relevant_data.sql");
			
			return true;
			
		} catch (SQLException e) {
			Logger.writeErrorToLog("MassiveImporter.createDB : Executing SQL scripts failed" + e.getMessage());
			return false;
		}
	}

}
