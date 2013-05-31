package massiveImport;
import java.io.IOException;
import Utils.DBConnector;
import Utils.Logger;


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

		y.createFilteredYagoFiles(); // create TSVs with relevant data only
		//
		//		Logger.writeToLog("Importing TSV files to DB...")
		//
		//		write the tsv files into the DB somehow... 
		
		//		//y.deleteAllYagoFiles(); // delete all temporary files and folders
		//		
		//		Logger.writeToLog("Finished importing process!");
		//
		return true;
	}

}
