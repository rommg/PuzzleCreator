import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URI;
import java.util.Set;
import java.util.TreeSet;


public class YagoFileHandler {
	private static final String SUFFIX_7Z = ".7z";
	private static final String YAGO_URL = "http://www.mpi-inf.mpg.de/yago-naga/yago/download/yago/";
	private static final String ZIP_FILES_PATH = ".\\7zFiles\\";
	private static final String FILES_PATH = ".\\tsvFiles\\";
	private static final String TO_LOAD_FILES_PATH = "toLoad\\";
	private static final long NS_IN_SEC = 1000000000;
	private static final int SUJECT_INDEX = 1; 

	private File file;
	private Set<String> subjects = new TreeSet<String>();


	//	private File yago2s_tsv = new File(ZIP_FILES_PATH + "yago2s_tsv.7z");
	//	private Map<String,File> files = new HashMap<String,File>();
	//	private File yagoSchema; 
	//	private File yagoTypes; 
	//	private File yagoTaxonomy;
	//	private File yagoTransitiveType;
	//	private File yagoFacts;
	//	private File yagoLabels;
	//	private File yagoLiteralFacts;
	//	private File yagoGeonamesClassIds;
	//	private File yagoGeonamesClasses;
	//	private File yagoStatistics; 
	//	private File yagoMetaFacts;
	//	private File yagoSources;
	//	private File yagoMultilingualInstanceLabels;
	//	private File yagoMultilingualClassLabels;
	//	private File yagoDBpediaClasses;
	//	private File yagoWordnetIds;
	//	private File yagoDBpediaInstances;
	//	private File yagoWordnetDomains;
	//	private File yagoWikipediaInfo;
	//	private File yagoGeonamesData;

	public YagoFileHandler(String fileName){
		URI uri = null;
		long startTime, endTime, delta;
		try {
			file = new File(ZIP_FILES_PATH + fileName + SUFFIX_7Z);
			startTime = System.nanoTime();
			System.out.println("downloding YAGO files...");
			uri = new URI(YAGO_URL);
			uri = uri.resolve(fileName + SUFFIX_7Z);
			org.apache.commons.io.FileUtils.copyURLToFile(uri.toURL(), file);
			endTime = System.nanoTime();
			delta = endTime - startTime;
			System.out.println("Took "+ delta + " ns; " + delta/NS_IN_SEC + "sec" ); 


			startTime = System.nanoTime();
			System.out.println("extract 7z files...");
			new SevenZipJBindingExtractor().extract(ZIP_FILES_PATH + file.getName(), FILES_PATH);
			endTime = System.nanoTime();
			delta = endTime - startTime;
			System.out.println("Took "+ delta + " ns; " + delta/NS_IN_SEC + "sec" ); 

			file = new File(FILES_PATH + fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	public void setSubjects(Set<String> subjects){
		this.subjects = subjects;
	}

	public void addSubjects(String subject){
		this.subjects.add(subject);
	}


	public void createYagoFileToLoad(){
		BufferedWriter bw = null;
		BufferedReader br;
		try {
			File toLoad = new File(TO_LOAD_FILES_PATH + "toLoad.tsv");
			toLoad.createNewFile();
			bw = new BufferedWriter(new FileWriter(toLoad, true));
			br = new BufferedReader(new FileReader(this.file));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().length() == 0) {
					continue;
				}

				String[] values = line.split("\t");
				if (subjects.contains(values[SUJECT_INDEX])){
					bw.write(line);
				}
			}
			bw.close();
			br.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}



}
