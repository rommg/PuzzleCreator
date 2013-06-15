package utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import connectionPool.ConnectionPool;

public class DBUtilsTest {
	
	public static ConnectionPool connectionPool = null;
	public static String dbServerAddress = "localhost";
	public static String dbServerPort = "3306";
	public static String username = "root";
	public static String password = "2bsafe"; // enter your password 
	public static String schemaName = "riddle";
	
	@Before 
	public void before(){
		Logger.initialize(true);
		connectionPool = new ConnectionPool("jdbc:mysql://"+ dbServerAddress +":"+ dbServerPort +"/"+ schemaName,
				username, password);
		if(!connectionPool.createPool()) {
			Logger.writeErrorToLog("Failed to create the Connections Pool");
			return;
		}
		Logger.writeToLog("Connections Pool was created");
	}
	
	

	@Test
	public void testGetPossibleAnswers() throws SQLException {
		int[] topics = {1};
		assertNotNull(DBUtils.getPossibleAnswers(topics, 10));
	}

	@Test
	public void testSetHintsAndDefinitions() {
		fail("Not yet implemented");
	}
	
	
	@After
	public void after(){
		try {
			connectionPool.closeConnections();
			Logger.writeToLog("Closed all connections");
		} catch (SQLException e) {
			Logger.writeErrorToLog("ConnectionPool failed to close connections" + e.getMessage());
		}
		
	}

}
