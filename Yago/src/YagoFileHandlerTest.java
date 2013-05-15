import static org.junit.Assert.*;


import org.junit.Test;


public class YagoFileHandlerTest {

	@Test
	public void test() {
		YagoFileHandler y = new YagoFileHandler("yagoSchema.tsv");
		y.addSubjects("hasChild");
		y.createYagoFileToLoad();
//		assertEquals(true, y.getYagoSchema().canRead());
//		assertEquals(true, y.getYagoDBpediaClasses().canRead());

	}

}
