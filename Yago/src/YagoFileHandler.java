import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import net.sf.sevenzipjbinding.SevenZipException;

public class YagoFileHandler {

	private static final String TSV = ".tsv";
	private static final String TSV_7Z = TSV + ".7z";
	private static final String HOME_DIR = System.getProperty("user.home") + System.getProperty("file.separator");
	private static final String TEMP_DIR = HOME_DIR +"temp_yago_files" +  System.getProperty("file.separator");
	private static final String ZIP_FILE_DEST_DIR = TEMP_DIR + "7z_files" +  System.getProperty("file.separator");
	private static final String TSV_FILE_DEST_DIR = TEMP_DIR + "tsv_files" +  System.getProperty("file.separator");
	public static final String FILTERED_TSV_FILE_DEST_DIR = TEMP_DIR + "filtered_tsv_files" +  System.getProperty("file.separator");


	// static yago files names
	public static final String YAGO_TYPES = "yagoTypes";
	public static final String YAGO_FACTS = "yagoFacts";
	public static final String YAGO_LITERAL_FACTS = "yagoLiteralFacts";

	// instance fields
	private Set<String> entityTypes = null;
	private Set<String> factsTypeIDs = null;
	private Set<String> LitertalTypes = null;
	private Set<String> relevantEntities= null;

	public YagoFileHandler() {
		getTypes();
		relevantEntities = new HashSet<String>(); // will contain names of interesting entities

	}

	private void getEntityTypes() { // can be changed in the future
		entityTypes = new HashSet<String>(); 
		entityTypes.add("<wikicategory_Israeli_basketball_players>");
		entityTypes.add("<wikicategory_Capitals_in_Europe>");
		entityTypes.add("<wikicategory_States_of_the_United_States>");
		entityTypes.add("<wikicategory_Former_United_States_state_capitals>");
		entityTypes.add("<wikicategory_English-language_singers>");
		entityTypes.add("<wikicategory_Jewish_American_musicians>");
		entityTypes.add("<wikicategory_Jewish_poets>");
		entityTypes.add("<wikicategory_2000s_comedy_films>");
		entityTypes.add("<wordnet_movie_106613686>");
		entityTypes.add("<wikicategory_2013_films>");
		entityTypes.add("<wikicategory_Israeli_rock_singers>");
		entityTypes.add("<wikicategory_Israeli_artists>");
		entityTypes.add("<wikicategory_Israeli_children's_writers>");
	}

	private void getFactTypes() { // can be changed in the future
		factsTypeIDs = new HashSet<String>(); 
		factsTypeIDs.add("<created>"); // 1gi
		factsTypeIDs.add("<actedIn>"); // gar
		factsTypeIDs.add("<hasCapital>"); //" "789"
	}

	private void getLiteralTypes() { // can be changed in the future
		LitertalTypes = new HashSet<String>(); 
		LitertalTypes.add("<wasBornOnDate>"); // there are a lot of defective yago IDs in LiteralFacts, so it's bette to use the relation name.
	}

	private void getTypes() {
		getEntityTypes();
		getFactTypes();
		getLiteralTypes();

	}

	private int getFileFromURL(String yagoFile) {
		URI uri = null;
		String zip_7z_file_path = ZIP_FILE_DEST_DIR + yagoFile + TSV_7Z;
		File zip_7z_file = new File(zip_7z_file_path);

		File tsv_file = new File(TSV_FILE_DEST_DIR + yagoFile + TSV);

		if  (tsv_file.exists()) { // TSV file exists
			Logger.writeToLog(yagoFile + TSV + " already exists.");
			return 1;
		}

		if (zip_7z_file.exists()) {
			Logger.writeToLog(yagoFile + TSV + " already downloaded.");
		}
		else { // need to download
			String urlStr = "http://www.mpi-inf.mpg.de/yago-naga/yago/download/yago/";
			try {
				uri = new URI(urlStr);
				uri = uri.resolve(yagoFile + TSV_7Z); 
				org.apache.commons.io.FileUtils.copyURLToFile(uri.toURL(), new File(zip_7z_file_path));
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		// extract yago 7z file
		Logger.writeToLog("Extracting " + zip_7z_file_path + " ...");

		// Get current time
		long start = System.currentTimeMillis();

		try {
			new SevenZipJBindingExtractor().extract(zip_7z_file_path, TSV_FILE_DEST_DIR);
		} catch (SevenZipException e) {
			Logger.writeErrorToLog("SevenZipException while extracting " + zip_7z_file_path + " .");
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.writeErrorToLog("IOException while extracting " + zip_7z_file_path + " ." );
			return 0;
		}

		// Get elapsed time in milliseconds
		long elapsedTimeMillis = System.currentTimeMillis()-start;

		// Get elapsed time in seconds
		float elapsedTimeSec = elapsedTimeMillis/1000F;

		Logger.writeToLog("Finished Extracting " + zip_7z_file_path + "in " + elapsedTimeSec + "seconds." );


		return 1;

	}

	private BufferedReader getFileReader(String yagoFile) throws IOException{

		// file should have been created by now by getFileFromURL

		return new BufferedReader(new InputStreamReader (new FileInputStream(TSV_FILE_DEST_DIR + yagoFile + TSV),"UTF-8"));
	}

	private BufferedWriter getFileWriter(String yagoFile) throws IOException {

		// create file 
		File f =new File(FILTERED_TSV_FILE_DEST_DIR + yagoFile + TSV); // for example: FILE_DEST_DIR\light_yago_types.tsv
		if (!f.getParentFile().exists()) // create directory of logFile
			f.getParentFile().mkdirs();
		if (f.exists()) // delete if exists, to start with clean file
			f.delete();
		f.createNewFile();

		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),"UTF-8"));
	}

	private int parseYagoTypes() throws IOException {
		int count = 0;
		String line = null;
		String[] lineColumns = null; 
		String[] yagoIDColumns = null;

		BufferedReader br = getFileReader(YAGO_TYPES);
		BufferedWriter bw = getFileWriter(YAGO_TYPES);

		Logger.writeToLog("scanning " + YAGO_TYPES + " ...");

		// Get current time
		long start = System.currentTimeMillis();

		try {

			while ((line = br.readLine()) != null)   {
				lineColumns = line.split("\\t");
				if (entityTypes.contains(lineColumns[3])) { // object is a relevant type
					yagoIDColumns = lineColumns[0].split("_");
					if (yagoIDColumns.length != 4) { // discard rows with defective yagoID
						Logger.writeErrorToLog("Defective yagoID: " + lineColumns[0] + " in " + YAGO_TYPES);
					}
					else {
						relevantEntities.add(lineColumns[1]);
						//	typesIDs.add(yagoIDColumns[1]); //add to relevant type ids collection
						bw.write(line);
						bw.newLine();
						count++;
					}
				} 
			}
		} catch (IOException e) {
			Logger.writeErrorToLog("IOException in parseYagoTypes");
			return 0;
		}

		// Get elapsed time in milliseconds
		long elapsedTimeMillis = System.currentTimeMillis()-start;
		// Get elapsed time in seconds
		float elapsedTimeSec = elapsedTimeMillis/1000F;
		Logger.writeToLog(String.format("Finished scanning " + YAGO_TYPES + " : %d rows copied in %f seconds.", count, elapsedTimeSec));

		// close readers and commit changes
		br.close();
		bw.close();

		return 1;
	}

	private int parseYagoFacts() throws IOException {
		int count = 0;
		String line = null;
		String[] lineColumns = null;
		String[] yagoIDColumns = null;
		//String subject = null;
		//String object = null;
		//String fact = null;

		BufferedReader br = getFileReader(YAGO_FACTS);
		BufferedWriter bw = getFileWriter(YAGO_FACTS);

		Logger.writeToLog("scanning " + YAGO_FACTS + " ...");

		// Get current time
		long start = System.currentTimeMillis();

		try {

			while ((line = br.readLine()) != null)   {
				lineColumns = line.split("\\t");
				yagoIDColumns = lineColumns[0].split("_"); // split the yago ID into subject, fact, object.
				if (yagoIDColumns.length != 4) { //defective yagoID
					Logger.writeErrorToLog("Defective yagoID: " + lineColumns[0] + " in " + YAGO_FACTS);
				}
				else { // valid yago ID
					//subject =yagoIDColumns[1];
					//fact = yagoIDColumns[2];
					//object =yagoIDColumns[3];
					//object = object.substring(0, object.length()-1); // get rid of '>'

					//	if ((typesIDs.contains(subject) || typesIDs.contains(object)) 
					if ((relevantEntities.contains(lineColumns[1]) || relevantEntities.contains(lineColumns[3])) 
							&& (factsTypeIDs.contains(lineColumns[2]))) { // fact has relevant typeID for either subject or object and relevant fact
						bw.write(line);
						bw.newLine();
						count++;
					} 
				}
			}

		} catch (IOException e) {
			Logger.writeErrorToLog("IOException in parseYagoFacts");
			return 0;
		}

		// Get elapsed time in milliseconds
		long elapsedTimeMillis = System.currentTimeMillis()-start;
		// Get elapsed time in seconds
		float elapsedTimeSec = elapsedTimeMillis/1000F;
		Logger.writeToLog(String.format("Finished scanning " + YAGO_FACTS + " : %d rows copied in %f seconds.", count, elapsedTimeSec));

		// close readers and commit changes
		br.close();
		bw.close();

		return 1;
	}

	private int parseYagoLiteralFacts() throws IOException {
		int count = 0;
		String line = null;
		String[] lineColumns = null;
		
		BufferedReader br = getFileReader(YAGO_LITERAL_FACTS);
		BufferedWriter bw = getFileWriter(YAGO_LITERAL_FACTS);

		Logger.writeToLog("scanning " + YAGO_LITERAL_FACTS + " ...");

		// Get current time
		long start = System.currentTimeMillis();

		try {
			while ((line = br.readLine()) != null)   {
				lineColumns = line.split("\\t");
				if (relevantEntities.contains(lineColumns[1]) && LitertalTypes.contains(lineColumns[2])) { // checking by entity name because there are many rows with no yagoID
					bw.write(line);
					bw.newLine();
					count++;

				}
			}

		} catch (IOException e) {
			Logger.writeErrorToLog("IOException in parseYagoLiteralFacts");
			return 0;
		}

		// Get elapsed time in milliseconds
		long elapsedTimeMillis = System.currentTimeMillis()-start;
		// Get elapsed time in seconds
		float elapsedTimeSec = elapsedTimeMillis/1000F;
		Logger.writeToLog(String.format("Finished scanning " + YAGO_LITERAL_FACTS + " : %d rows copied in %f seconds.", count, elapsedTimeSec));

		// close readers and commit changes
		br.close();
		bw.close();

		return 1;
	}
	
	public void deleteAllYagoFiles() {
		deleteYagoFile(YAGO_TYPES);
		deleteYagoFile(YAGO_FACTS);
		deleteYagoFile(YAGO_LITERAL_FACTS);
		
		//deleting empty directories
		deleteFileOrDirectory(ZIP_FILE_DEST_DIR);
		deleteFileOrDirectory(TSV_FILE_DEST_DIR);
		deleteFileOrDirectory(FILTERED_TSV_FILE_DEST_DIR);
		deleteFileOrDirectory(TEMP_DIR);

	}
	
	private void deleteYagoFile(String yagoFile) {
		// delete 7z file
		deleteFileOrDirectory(ZIP_FILE_DEST_DIR + yagoFile + TSV_7Z);
		//delete tsv file
		deleteFileOrDirectory(TSV_FILE_DEST_DIR + yagoFile + TSV);
		//delete filtered TSV
		deleteFileOrDirectory(FILTERED_TSV_FILE_DEST_DIR +  yagoFile + TSV);
	}

	private void deleteFileOrDirectory(String file) {
		File f;
		f = new File(file);
		if (f.exists())
			if (f.isFile() || (f.isDirectory() && (f.list().length == 0))) // file or empty directory
				f.delete();
	}
	
	public int createFilteredYagoFiles() {

		// create filtered TSV files
		try {
			parseYagoTypes();
			parseYagoFacts();
			parseYagoLiteralFacts();
		} catch (IOException e) {
			return 0;
		}
		return 1;
	}

	public void getFilesFromURL() {

		getFileFromURL(YAGO_TYPES);
		getFileFromURL(YAGO_FACTS);
		getFileFromURL(YAGO_LITERAL_FACTS);
	}
}
