package no.ntnu.fp.storage;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * <p>Database communication class.</p>
 * 
 * <p>This class connects to the Apache Derby database</p>
 * 
 * @author fp2011-gr26
 */
public class DBConnection {
	/**
	 * The database connection object.
	 */
	public Connection conn;
	
	/**
	 * Default constructor. Connects to the database through {@link #connect()}.
	 */
	public DBConnection() {
		try {
			connect();
		} catch (Exception e) {
			System.err.println("Unable to connect");
			e.printStackTrace();
		}
	}
	
	/**
	 * Connects to the Apache Derby database.
	 * @throws Exception
	 */
	public void connect() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		conn = DriverManager.getConnection("jdbc:derby:kalenderdb");
	}
	
	/**
	 * Closes the connection to the database.
	 */
	public void close() {
		if(conn != null) {
			try {
				conn.close();
				System.out.println("Database connection terminated");
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
