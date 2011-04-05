package fp.common.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import fp.common.models.Activity;
import fp.common.models.Employee;
import fp.common.models.Meeting;
import fp.common.models.Room;


public class Storage {
	
	private static Storage instance;
	
	public volatile HashMap<String,Employee> empCache;
	public volatile HashMap<Integer,Room> roomCache;
	public volatile HashMap<Integer,Activity> actCache;
	public volatile HashMap<Integer,Meeting> mtngCache;
	
	private Connection conn;
	
	private Storage() {
		empCache = new HashMap<String,Employee>();
		roomCache = new HashMap<Integer, Room>();
		actCache = new HashMap<Integer, Activity>();
		mtngCache = new HashMap<Integer, Meeting>();
		
		try {
			connect();
		} catch (Exception e) {
			System.err.println("Unable to connect");
			e.printStackTrace();
		}
	}
	
	public static Storage getInstance() {
		if(instance == null) instance = new Storage();
		return instance;
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
	public void disconnect() {
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

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public Connection getConn() {
		return conn;
	}
}
