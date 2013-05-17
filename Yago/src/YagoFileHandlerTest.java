import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;

import net.sf.sevenzipjbinding.SevenZipException;

import org.junit.Test;


public class YagoFileHandlerTest {

	@Test
	public void test() throws SevenZipException, IOException {
		
		Logger.initialize();
		
		YagoFileHandler y = new YagoFileHandler();
		
		//assertEquals(1,y.createAllFilteredYagoFiles());
		
		String tableBuildSql = 
				"DROP TABLE IF EXISTS yago_temp;" +
						"CREATE TABLE yago_temp (yago_id VARCHAR(50) DEFAULT NULL, subject VARCHAR(248) ,predicate VARCHAR(200) ,object  VARCHAR(248), VALUE VARCHAR(200) DEFAULT NULL, PK INT NOT NULL AUTO_INCREMENT,PRIMARY KEY (PK));";

		assertEquals(1, DBConnector.createTable(tableBuildSql));
		
		String importSql = 
				"LOAD DATA LOCAL INFILE '" + YagoFileHandler.FILTERED_TSV_FILE_DEST_DIR + YagoFileHandler.YAGO_TYPES + "'" +
						"INTO TABLE yagoFacts" +
						"fields terminated by '\t'" +
						"lines terminated by '\n'" +
			"(yago_id,subject,predicate,object,value);";
		
		assertEquals(1, DBConnector.executeSql(importSql));
			
	}

}
