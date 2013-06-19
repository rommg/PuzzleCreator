package parsing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import core.Logger;
import core.PuzzleCreator;

import db.DBConnection;
import db.HintsHandler;


import net.sf.sevenzipjbinding.SevenZipException;

public class YagoFileHandler {

	//private static fields
	private static final String TSV = ".tsv";
	private static final String TSV_7Z = TSV + ".7z";
	private static final String APP_HOME_DIR = PuzzleCreator.appDir;
	private static final String SQL_DIR = PuzzleCreator.sqlDir;
	private static final String TEMP_DIR = APP_HOME_DIR +"temp_yago_files" +  System.getProperty("file.separator");
	private static final String ZIP_FILE_DEST_DIR = TEMP_DIR + "7z_files" +  System.getProperty("file.separator");
	private static final String TSV_FILE_DEST_DIR = TEMP_DIR + "tsv_files" +  System.getProperty("file.separator");
	private static final String FILTERED_TSV_FILE_DEST_DIR = TEMP_DIR + "filtered_tsv_files" +  System.getProperty("file.separator");
	private static final String HAS_GENDER = "<hasGender>";
	private static final String MARRIED_TO = "<isMarriedTo>";
	private static final List<String> ILLEGAL_ANSWERS = new ArrayList<String>(Arrays.asList("the", "a", "an", "mr"));

	// static yago files names
	public static final String YAGO_TYPES = "yagoTypes";
	public static final String YAGO_FACTS = "yagoFacts";
	public static final String YAGO_LITERAL_FACTS = "yagoLiteralFacts";
	public static final String YAGO_HUMAN_ANSWERS = "yagoHumanAnswers";

	// instance fields
	private String tsvDirectory = null;
	private Set<String> entityTypes = null;
	private Set<String> predicateTypes = null;
	private Set<String> litertalTypes = null;
	private Set<String> relevantEntities= null;

	public YagoFileHandler(File direcotry) throws SQLException{
		if (direcotry == null)
			this.tsvDirectory = TSV_FILE_DEST_DIR;
		else
			this.tsvDirectory = direcotry.getAbsolutePath() + System.getProperty("file.separator");

		getTypes(); // gets types from DB
		relevantEntities = new HashSet<String>(); // will contain names of interesting entities
	}

	private void getEntityTypes()  throws SQLException{ // can be changed in the future
		entityTypes = new HashSet<String>(); 
		fillCollectionEntitiesFromDB("definitions", "type", entityTypes);
	}

	private void getPredicateTypes() throws SQLException{ // can be changed in the future
		predicateTypes = new HashSet<String>(); 
		fillCollectionEntitiesFromDB("predicates", "predicate", predicateTypes);
	}

	private void getLiteralTypes() throws SQLException{ // can be changed in the future
		litertalTypes = new HashSet<String>(); 
		fillCollectionEntitiesFromDB("predicates", "predicate", litertalTypes);

	}
	
	public static boolean containsFiles(File directory) {
		 
		if (!directory.isDirectory())
			return false;
		File types = new File(directory + System.getProperty("file.separator") + YAGO_TYPES + TSV);
		File facts = new File(directory + System.getProperty("file.separator") + YAGO_FACTS + TSV);
		File literalFacts = new File(directory + System.getProperty("file.separator") + YAGO_LITERAL_FACTS + TSV);
		
		return (literalFacts.exists() && facts.exists() && types.exists());

	}

	private void fillCollectionEntitiesFromDB(String tableName, String entityType, Set<String> collection) throws SQLException{
		String columnName = "yago_" + entityType;
		List<Map<String,Object>> rs = null;
		String query = "SELECT " + tableName + "." + columnName +  " FROM " + tableName + ";";
		rs = DBConnection.executeQuery(query);
		for (Map<String,Object> row : rs )
			collection.add((String)row.get(columnName));
	}

	private void getTypes() throws SQLException{
		getEntityTypes();
		getPredicateTypes();
		getLiteralTypes();

	}

	public static String getFilteredTsvFileDestDir() {
		return FILTERED_TSV_FILE_DEST_DIR;
	}


	private void getTSVFileFromURL(String yagoFile) throws IOException, SevenZipException {
		URI uri = null;
		String zip_7z_file_path = ZIP_FILE_DEST_DIR + yagoFile + TSV_7Z;
		File zip_7z_file = new File(zip_7z_file_path);

		File tsv_file = new File(TSV_FILE_DEST_DIR + yagoFile + TSV);

		if  (tsv_file.exists()) { // TSV file exists
			Logger.writeToLog(yagoFile + TSV + " already exists.");
			return;
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
			}catch (IOException e) {
				Logger.writeErrorToLog("ERROR failed to download file from YAGO. " + zip_7z_file_path);
				Logger.writeErrorToLog(e.getStackTrace().toString());
				throw new IOException("failed to download file from Yago", e);
			} catch (URISyntaxException e) {
				Logger.writeErrorToLog("ERROR bad YAGO url." + urlStr);
				Logger.writeErrorToLog(e.getStackTrace().toString());
				throw new IOException("failed to download file from Yago", e);
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
			Logger.writeErrorToLog(e.getStackTrace().toString());
			throw new SevenZipException("failed to extract 7z file", e);
		} catch (IOException e) {
			Logger.writeErrorToLog("IOException while extracting " + zip_7z_file_path + " ." );
			Logger.writeErrorToLog(e.getStackTrace().toString());
			throw new IOException("failed to extract 7z file", e);
		}

		// Get elapsed time in milliseconds
		long elapsedTimeMillis = System.currentTimeMillis()-start;

		// Get elapsed time in seconds
		float elapsedTimeSec = elapsedTimeMillis/1000F;

		Logger.writeToLog("Finished Extracting " + zip_7z_file_path + "in " + elapsedTimeSec + "seconds." );

	}

	private BufferedReader getFileReader(String yagoFile) throws IOException{

		// file should have been created by now by getFileFromURL

		return new BufferedReader(new InputStreamReader (new FileInputStream(tsvDirectory + yagoFile + TSV),"UTF-8"));
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

	/**
	 * This method takes a yago entity string, e.g <Abraham_Lincoln_(band)> and returns the corresponding answer, e.g. abraham lincoln
	 * @param input
	 * @return
	 */
	public static String getProperAnswerName(String input) {

		String returnString;
		returnString= input.replace('_', ' ').replace('-', ' ').replaceAll("\\.", "").replaceAll("'", "");  // remove .,-,' and add spaces
		returnString = returnString.substring(1, returnString.lastIndexOf('>')); // trim <,>
		int i = returnString.lastIndexOf('(');
		if (i<=0) // also for entities with 1 char e.g. (_)
			return returnString.toLowerCase();
		else 
			return returnString.substring(0, i-1).toLowerCase(); // get rid of ' ' +  '(... )' in end of entity names
	}

	private boolean containsNonEnglishChars(String input) {
		return !input.matches("[a-zA-Z0-9 -!,;:.']+");

	}
	private void parseYagoTypes() throws IOException {
		int count = 0;
		int row = 1;
		String line = null;

		BufferedReader br = getFileReader(YAGO_TYPES);
		BufferedWriter bw = getFileWriter(YAGO_TYPES);

		Logger.writeToLog("scanning " + YAGO_TYPES + " ...");

		// Get current time
		long start = System.currentTimeMillis();

		try {
			while ((line = br.readLine()) != null)   {
				String[] lineColumns = line.split("\\t");
				String[] decomposedYagoID = lineColumns[0].split("_");
				if ((decomposedYagoID.length != 4) || (decomposedYagoID == null)) {
					Logger.writeErrorToLog("Invalid yagoID in line #" + row);
				}
				else {
					if ((lineColumns[1].length() <=50) && entityTypes.contains(lineColumns[3])) {

						String properName = getProperAnswerName(lineColumns[1]); // get clean entity name
						if (!containsNonEnglishChars(properName)) { // subject is of a relevant type and English letters only
							relevantEntities.add(lineColumns[1]);

							String newLine = lineColumns[1] + decomposedYagoID[1] + "\t"
									+ lineColumns[2] + "\t" 
									+ lineColumns[3] + "\t" 
									+ properName.replaceAll(" ", "") + "\t";

							StringBuffer buf = new StringBuffer(newLine);

							String[] entityNameDivided = properName.split(" ");
							if (entityNameDivided.length > 1) { // create additional information if there word count in entity > 1
								buf.append("(");
								for (int i = 0; i<entityNameDivided.length; ++i) {
									buf.append(entityNameDivided[i].length());
									if (i == entityNameDivided.length - 1) //last word in entity
										buf.append(")");
									else 
										buf.append(",");
								}
							}

							bw.write(buf.toString());
							bw.newLine();
							count++;
						}
					} 
				}
				row++;
			}
		} catch (IOException e) {
			Logger.writeErrorToLog("IOException in parseYagoTypes");
			Logger.writeErrorToLog(e.getStackTrace().toString());
			throw new IOException("failed to parse YagoType", e);
		}

		// Get elapsed time in milliseconds
		long elapsedTimeMillis = System.currentTimeMillis()-start;
		// Get elapsed time in seconds
		float elapsedTimeSec = elapsedTimeMillis/1000F;
		Logger.writeToLog(String.format("Finished scanning " + YAGO_TYPES + " : %d rows copied in %f seconds.", count, elapsedTimeSec));

		// close readers and commit changes
		br.close();
		bw.close();

	}

	private void parseYagoFacts() throws IOException {
		int count = 0;
		int row = 1;
		String line = null;

		BufferedReader br = getFileReader(YAGO_FACTS);
		BufferedWriter bw = getFileWriter(YAGO_FACTS);
		BufferedWriter bwAnswers = getFileWriter(YAGO_HUMAN_ANSWERS);

		Logger.writeToLog("scanning " + YAGO_FACTS + " ...");

		// Get current time
		long start = System.currentTimeMillis();

		try {
			while ((line = br.readLine()) != null)   {
				String[] lineColumns = line.split("\\t");	
				String[] decomposedYagoID = lineColumns[0].split("_");
				if ((decomposedYagoID.length != 4) || (decomposedYagoID == null)) {
					Logger.writeErrorToLog("Invalid yagoID in line #" + row);
				}
				else {
					decomposedYagoID[3] = decomposedYagoID[3].replaceAll(">", ""); // remove '>' in last cell
					boolean subjectHit = relevantEntities.contains(lineColumns[1]);
					boolean objectHit = relevantEntities.contains(lineColumns[3]) 
							&& (lineColumns[2].compareTo(MARRIED_TO) != 0); // object is not a hit if MARRIED_TO predicate, to prevent multiple MARRIED_TO hints
					if ((subjectHit || objectHit) 
							&& (lineColumns[1].length() <= 50) && (lineColumns[3].length() <=50)
							&& (predicateTypes.contains(lineColumns[2]))) { // fact has relevant typeID for either subject or object and relevant fact

						String newLine = lineColumns[1] + decomposedYagoID[1] + "\t"
								+ lineColumns[2] + "\t" 
								+ lineColumns[3] + decomposedYagoID[3] + "\t";

						
						if (subjectHit) {
							String subjectLine = newLine + "1"; 
							bw.write(subjectLine); // write one line for subject matched
							bw.newLine();
							count++;
						}

						if (objectHit) {
							String objectLine = newLine + "0"; 
							bw.write(objectLine); // write one line for object matched
							bw.newLine();
							count++;
						}
					}

					if (subjectHit && (lineColumns[1].length() <= 50) && (lineColumns[2].compareTo(HAS_GENDER) == 0)) { // subject is human
						String properName = getProperAnswerName(lineColumns[1]); // human name in this predicate is in the subject
						int index = properName.indexOf(' ');
						String answerLine = null;
						if (index != -1)  { // at least one name
							if (!ILLEGAL_ANSWERS.contains(properName.substring(0, index).toLowerCase())){
								answerLine = lineColumns[1] + decomposedYagoID[1] + "\t" + properName.substring(0, index) + "\t(First Name)"; 
								bwAnswers.write(answerLine);
								bwAnswers.newLine();	
							}
							index = properName.lastIndexOf(' ');
							if(!ILLEGAL_ANSWERS.contains(properName.substring(index + 1, properName.length()).toLowerCase())){
								answerLine = lineColumns[1] + decomposedYagoID[1] + "\t" + properName.substring(index + 1, properName.length()) + "\t(Last Name)";
								bwAnswers.write(answerLine);
								bwAnswers.newLine();
							}
						}
					}
				}
				row++;
			}
		} catch (IOException e) {
			Logger.writeErrorToLog("IOException in parseYagoFacts");
			Logger.writeErrorToLog(e.getStackTrace().toString());
			throw new IOException("failed to parse yagoFacts files", e);
		}

		// Get elapsed time in milliseconds
		long elapsedTimeMillis = System.currentTimeMillis()-start;
		// Get elapsed time in seconds
		float elapsedTimeSec = elapsedTimeMillis/1000F;
		Logger.writeToLog(String.format("Finished scanning " + YAGO_FACTS + " : %d rows copied in %f seconds.", count, elapsedTimeSec));

		// close readers and commit changes
		br.close();
		bw.close();
		bwAnswers.close();
	}

	private void parseYagoLiteralFacts() throws IOException {
		int count = 0;
		int row = 1;
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
				String[] decomposedYagoID = lineColumns[0].split("_");
				if ((decomposedYagoID.length != 4) || (decomposedYagoID == null)) {
					Logger.writeErrorToLog("Invalid yagoID in line #" + row);
				}
				else {
					String properLiteral = lineColumns[3].substring(1, lineColumns[3].lastIndexOf('"'));
					int index = properLiteral.indexOf('#');
					if (index > 0) {
						properLiteral = properLiteral.substring(0, index - 1); // -1 to get rid of '-' char before '#' 
						if (lineColumns[1].length() <=50 && 
								relevantEntities.contains(lineColumns[1]) &&  // checking by entity name because there are many rows with no yagoID
								litertalTypes.contains(lineColumns[2]) &&
								!properLiteral.isEmpty()){
								//(!containsNonEnglishChars(properLiteral))) { 
									String newline = lineColumns[1] + decomposedYagoID[1] + "\t" + lineColumns[2] + "\t" + properLiteral;
									bw.write(newline);
									bw.newLine();
									count++;
								}
					}
				}
				row++;
			}

		} catch (IOException e) {
			Logger.writeErrorToLog("IOException in parseYagoLiteralFacts");
			Logger.writeErrorToLog(e.getStackTrace().toString());
			throw new IOException("failed to parse yagoLiteralFacts files", e);

		}

		// Get elapsed time in milliseconds
		long elapsedTimeMillis = System.currentTimeMillis()-start;
		// Get elapsed time in seconds
		float elapsedTimeSec = elapsedTimeMillis/1000F;
		Logger.writeToLog(String.format("Finished scanning " + YAGO_LITERAL_FACTS + " : %d rows copied in %f seconds.", count, elapsedTimeSec));

		// close readers and commit changes
		br.close();
		bw.close();

	}

	public void cleanDataTables() throws SQLException, IOException {

		DBConnection.executeSqlScript("00create_schema_and_tables.sql");
	}
	
	public void importFilesToDB() throws SQLException, IOException {

		DBConnection.executeSqlScript("05load_yago_data.sql");
	}

	public void populateDB() throws SQLException, IOException {

		DBConnection.executeSqlScript("06create_relevant_data.sql"); 
	}
	
	public void reduceHints() throws SQLException {
		HintsHandler.setMaximumTemHintsForEachEntity();
	}


	public void deleteAllYagoFiles() {
		deleteYagoFile(YAGO_TYPES);
		deleteYagoFile(YAGO_FACTS);
		deleteYagoFile(YAGO_LITERAL_FACTS);
		deleteYagoFile(YAGO_HUMAN_ANSWERS);

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

	public void createFilteredYagoFiles() throws IOException {

		// create filtered TSV files
		try {
			parseYagoTypes();
			parseYagoFacts();
			parseYagoLiteralFacts();
		} catch (IOException e) {
			Logger.writeErrorToLog("IOException in createFilteredYagoFiles" );
			Logger.writeErrorToLog(e.getStackTrace().toString());
			throw new IOException("failed to create filtered yago files", e);
		}
	}

	public void getFilesFromURL() throws IOException, SevenZipException {
		//TODO: download with multi thread??? can we?
		getTSVFileFromURL(YAGO_TYPES);
		getTSVFileFromURL(YAGO_FACTS);
		getTSVFileFromURL(YAGO_LITERAL_FACTS);
	}
}
