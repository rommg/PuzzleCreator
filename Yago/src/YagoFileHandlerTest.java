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
		
		//y.getFileFromURL(y.getYagotypes()); // get types file
		y.createAllFilteredYagoFiles();
		
	//	YagoFileHandler y = new YagoFileHandler();
		//assertEquals(true, y.yagoSchema.canRead());
		
	}

}
