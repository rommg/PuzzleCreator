package connectionPool;

import java.sql.*; 
import java.util.*; 
import utils.*;


/** 
 * ConnectionPool creates a pool of connections of the specified 
 * size to the specified database. The connection pool object 
 * allows the client to specify the JDBC driver, database, 
 * username, and password. In addition, the client can also 
 * specify the number of connections to create when this class is 
 * instantiated, the number of additional connections to create 
 * if all connections are exhausted, and the absolute maximum 
 * number of connections. 
 * 
 */ 
public class ConnectionPool {

	private String dbUrl; 
	private String dbUsername; 
	private String dbPassword; 
	private String testTable = "topics"; 
	private int initialConnections = 5; 
	private int incrementalConnections =5; 
	private int maxConnections = 40; 
	private Vector<PooledConnection> connections = null; 

	/** 
	 * Constructor stores the parameters passed by the calling 
	 * object. 
	 * 
	 * @param url String containing the database URL 
	 * @param username String containing the username to use when 
	 * logging into the database 
	 * @param password String containing the password to use when 
	 * logging into the database 
	 */ 
	public ConnectionPool(String url,String username, String password) 
	{  
		this.dbUrl = url; 
		this.dbUsername = username; 
		this.dbPassword = password;
	} 

	/** 
	 * Returns the initial number of connections to create. 
	 * 
	 * @return Initial number of connections to create. 
	 */ 
	public int getInitialConnections() 
	{ 
		return initialConnections; 
	} 

	/** 
	 * Sets the initial number of connections to create. 
	 * 
	 * @param initialConnections Initial number of connections to 
	 * create 
	 */ 
	public void setInitialConnections(int initialConnections) 
	{ 
		this.initialConnections = initialConnections; 
	} 

	/**
	 * Returns the number of incremental connections to create if 
	 * the initial connections are all in use. 
	 * 
	 * @return Number of incremental connections to create. 
	 */ 
	public int getIncrementalConnections() 
	{ 
		return incrementalConnections; 
	} 

	/** 
	 * Sets the number of incremental connections to create if 
	 * the initial connections are all in use. 
	 * 
	 *@param incrementalConnections Number of incremental 
	 * connections to create. 
	 */ 
	public void setIncrementalConnections( 
			int incrementalConnections) 
	{ 
		this.incrementalConnections = incrementalConnections; 
	} 

	/** 
	 * Returns the absolute maximum number of connections to 
	 * create. If all connections are in use, the getConnection() 
	 * method will block until one becomes free. 
	 * 
	 * @return Maximum number of connections to create. 
	 */
	public int getMaxConnections() 
	{ 
		return maxConnections; 
	} 

	/** 
	 * Sets the absolute maximum number of connections to create. 
	 * If all connections are in use, the getConnection() method 
	 * will block until one becomes free. 
	 * 
	 * @param maxConnections Maximum number of connections to 
	 * create. 
	 */ 
	public void setMaxConnections(int maxConnections) 
	{ 
		this.maxConnections = maxConnections; 
	} 

	/** 
	 * Returns the name of the table that should be tested to 
	 * insure that the database connection is still open. 
	 * 
	 * @return Name of the database table used to test the 
	 * connection. 
	 */ 
	public String getTestTable() 
	{ 
		return testTable; 
	} 

	/**
	 * Sets the name of the table that should be tested to insure 
	 * that the database connection is still open. 
	 * 
	 * @param testTable Name of the database table used to test the 
	 * connection. 
	 */ 
	public void setTestTable(String testTable) 
	{ 
		this.testTable = testTable;
	} 

	/** 
	 * Creates a pool of connections. Number of connections is 
	 * determined by the value of the initialConnections property. 
	 */ 
	public synchronized boolean createPool() 
	{ 
		//make sure that createPool hasn't already been called 
		if (connections != null) { 
			return false; 
		} 

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException E) {
			Logger.writeErrorToLog("Unable to load the JDBC driver " + E.toString());
			return false;
		} catch (Exception E) {
			Logger.writeErrorToLog("Unable to load the JDBC driver " + E.toString());
			return false;
		}

		connections = new Vector<PooledConnection>(); 

		try {
			//creates the proper number of initial connections 
			createConnections(initialConnections);
		} catch (SQLException e) {
			Logger.writeErrorToLog("Unable to create connections " + e.toString());
			return false;
		}
		return true;
	} 

	/** 
	 * Creates the specified number of connections, places them in
	 * a PooledConnection object, and adds the PooledConnection to 
	 * the connections vector. 
	 * 
	 * @param numConnections Number of connections to create. 
	 */ 
	private void createConnections(int numConnections) throws SQLException 
	{  
		for (int i=0; i < numConnections; i++) { 
			//a maxConnections value of zero indicates no limit 
			if (maxConnections > 0 && connections.size() >= maxConnections)	{ 
				break; 
			} 

			//add a new PooledConnection object to connections vector 
			connections.addElement(new PooledConnection(newConnection())); 
		} 
	} 

	/** 
	 * Creates a new database connection and returns it. 
	 * 
	 * @return New database connection. 
	 */ 
	private Connection newConnection() throws SQLException 
	{ 
		Connection conn = DriverManager.getConnection (dbUrl, dbUsername, dbPassword); 

		//if this is the first connection, check the maximum number 
		//of connections supported by this database/driver 
		if (connections.size()== 0) { 
			DatabaseMetaData metaData = conn.getMetaData(); 
			int driverMaxConnections = metaData.getMaxConnections(); 

			//driverMaxConnections value of zero indicates no maximum 
			//or unknown maximum 
			if (driverMaxConnections > 0 &&	maxConnections > driverMaxConnections) { 
				maxConnections = driverMaxConnections; 
			} 
		} 
		return conn; 
	} 

	/** 
	 * Attempts to retrieve a connection from the connections 
	 * vector by calling getFreeConnection(). If no connection is
	 * currently free, and more can not be created, getConnection() 
	 * waits for a short amount of time and tries again. 
	 * 
	 * @return Connection object 
	 */ 
	public synchronized Connection getConnection()throws SQLException 
	{ 
		//make sure that createPool has been called 
		if (connections == null) { 
			return null; 
		} 

		Connection conn = getFreeConnection(); 

		while (conn == null) { 
			//sleep for a quarter of a second and then check to see if 
			//a connection is free 
			wait(250); 
			conn = getFreeConnection();  
		} 

		return conn; 
	} 

	/** 
	 * Returns a free connection from the connections vector. If no 
	 * connection is available, a new batch of connections is 
	 * created according to the value of the incrementalConnections 
	 * variable. If all connections are still busy after creating 
	 * incremental connections, the method will return null. 
	 * 
	 * @return Database connection object 
	 */ 
	private Connection getFreeConnection() throws SQLException 
	{ 
		//look for a free connection in the pool 
		Connection conn = findFreeConnection(); 

		if (conn == null) { 
			//no connection is free, create additional connections 
			createConnections(incrementalConnections); 

			//try again to find a free connection 
			conn = findFreeConnection();

			if (conn == null) { 
				return null; 
			} 
		} 
		return conn; 
	} 

	/** 
	 * Searches through all of the pooled connections looking for 
	 * a free connection. If a free connection is found, its 
	 * integrity is verified and it is returned. If no free 
	 * connection is found, null is returned. 
	 * 
	 * @return Database connection object. 
	 */ 
	private Connection findFreeConnection()throws SQLException 
	{ 
		Connection conn = null; 
		PooledConnection pConn = null; 

		Enumeration<PooledConnection> pooledConn = connections.elements(); 

		//iterate through the pooled connections looking for free one 
		while (pooledConn.hasMoreElements()) { 
			pConn =(PooledConnection)pooledConn.nextElement(); 

			if (!pConn.isBusy()) { 
				conn = pConn.getConnection(); 
				pConn.setBusy(true);

				//test the connection to make sure it is still valid 
				if	(!testConnection(conn)) 
				{ 
					try {
						conn.close();
					} catch (SQLException e) {
						Logger.writeErrorToLog("ConnectionPool failed to close an invalid connection" + e.getMessage());
					}
					//this connection is no longer valid 
					conn = newConnection(); 

					//replace invalid connection with new connection 
					pConn.setConnection(conn); 
				} 

				break; //we found a free connection 
			} 
		} 
		return conn; 
	} 

	/** 

	 * Test the connection to make sure it is still valid. If not, 
	 * close it and return FALSE. 
	 *
	 * @param conn Database connection object to test. 
	 * @return True indicates connection object is valid. 
	 */ 
	private boolean testConnection(Connection conn) 
	{ 
		try 
		{ 
			//determine if a test table has been designated 
			if (testTable.equals("")) { 
				//There is no table to test the database connection so 
				//try setting the auto commit property. This verifies 
				//a valid connection on some databases. However, the 
				//test table method is much more reliable. 
				conn.setAutoCommit(true); 
			} 
			else { 
				//check if this connection is valid 
				Statement stmt = conn.createStatement(); 
				stmt.execute("select count(*) from " + testTable); 
			} 
		} 
		catch(SQLException e) 
		{ 
			//connection is no longer valid, attempt to close it 
			closeConnection(conn); 
			return false; 
		} 
		return true; 
	} 

	/** 
	 * Turns off the busy flag for the current pooled connection. 
	 * All ConnectionPool clients should call returnConnection() as 
	 * soon as possible following any database activity (within a 
	 * finally block). 
	 * 
	 * @param conn Connection object 
	 * @return True indicates returning the connection succeed.
	 */ 
	public boolean returnConnection(Connection conn)
	{ 
		//make sure that createPool has been called 
		if (connections == null) { 
			return false;
		} 

		PooledConnection pConn = null; 
		Enumeration<PooledConnection> element = connections.elements(); 

		//iterate through the pooled connections looking for the 
		//returned connection 
		while (element.hasMoreElements()) 
		{ 
			pConn =(PooledConnection)element.nextElement(); 

			//determine if this pooled connection contains the returned connection 
			if (conn == pConn.getConnection()) { 
				pConn.setBusy(false); 
				return true; 
			} 
		}
		return false;
	} 

	/** 
	 * Refreshes all of the connections in the connection pool. 
	 */ 
	public synchronized void refreshConnections()throws	SQLException 
	{ 
		//make sure that createPool has been called 
		if (connections == null) { 
			return; 
		} 

		PooledConnection pConn = null; 
		Enumeration<PooledConnection> element = connections.elements(); 

		while (element.hasMoreElements()) { 
			pConn =(PooledConnection)element.nextElement(); 

			if (!pConn.isBusy()) {
				wait(5000); //wait 5 seconds 
			} 

			closeConnection(pConn.getConnection()); 
			pConn.setConnection(newConnection()); 
			pConn.setBusy(false); 
		} 
	} 
	/** 
	 * Closes all of the connections and empties the connection 
	 * pool. Once this method has been called, the createPool() 
	 * method can again be called. 
	 */ 
	public synchronized void closeConnections() throws SQLException 
	{ 
		//make sure that createPool has been called 
		if (connections == null) { 
			return;  
		} 

		PooledConnection pConn = null; 
		Enumeration<PooledConnection> element = connections.elements(); 

		while (element.hasMoreElements()) { 
			pConn = (PooledConnection)element.nextElement(); 

			/*if (!pConn.isBusy()) { 
				wait(5000); //wait 5 seconds 
			}*/ 
			closeConnection(pConn.getConnection()); 
			//connections.removeElement(pConn);  
		} 
		connections = null; 
	} 

	/** 
	 * Closes a database connection. 
	 * 
	 * @param conn Database connection to close. 
	 */ 
	private void closeConnection(Connection conn) 
	{ 
		if (conn == null) {
			return;
		}		
		try	{ 
			conn.close();
		} catch (SQLException e) { 
			Logger.writeToLog("Error: trying to close connection " + e);
		} 
	} 

	/** 
	 * Sleeps for a specified number of milliseconds. 
	 * 
	 *@param mSeconds Number of seconds to sleep. 
	 */ 
	private void wait(int mSeconds) 
	{ 
		try { 
			Thread.sleep(mSeconds); 
		} 
		catch (InterruptedException e) {
			Logger.writeErrorToLog("Sleep interrupted" + e.getMessage());
		} 
	} 

	/** 
	 * Inner class encapsulating the properties of a pooled 
	 * connection object. These properties include a JDBC database 
	 * connection object and a flag indicating whether or not the 
	 * database object is currently in use (busy). 
	 */ 
	class PooledConnection {

		Connection connection = null; 
		boolean busy = false; 

		public PooledConnection(Connection connection) 
		{ 
			this.connection = connection; 
		} 

		public Connection getConnection() 
		{ 
			return connection; 
		} 

		public void setConnection(Connection connection) 
		{ 
			this.connection = connection; 
		} 

		public boolean isBusy() 
		{ 
			return busy; 
		} 

		public void setBusy(boolean busy) 
		{ 
			this.busy = busy; 
		} 
	} 
}