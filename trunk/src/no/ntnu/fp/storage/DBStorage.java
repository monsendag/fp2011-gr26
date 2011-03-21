package no.ntnu.fp.storage;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

import no.ntnu.fp.model.*;

public class DBStorage {
	
	/**
	 * The database connection object.
	 */
	public Connection conn;
	
	/**
	 * Default constructor. Connects to the database through {@link #connect()}.
	 */
	public DBStorage() {
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
	 * Converts a string to a hexadecimal md5 hash.
	 * (Burde finne et mer fornuftig sted å legge denne)
	 * @param str The string to be converted.
	 * @return A hexadecimal md5 hash.
	 */
	public String md5(String str) {
		MessageDigest m = null;;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.reset();
		m.update(str.getBytes());
		byte[] digest = m.digest();
		BigInteger bigInt = new BigInteger(1,digest);
		return bigInt.toString(16);
	}
	
	/**
	 * Adds an employee to the database from an employee object.
	 * (mest for testing, skal vel ikke drive å legge til/endre brukere fra programmet til slutt)
	 * @param emp The employee to be added.
	 * @throws SQLException
	 */
	public void addEmployee(Employee emp) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("INSERT INTO employee VALUES (?,?,?)");
		ps.setString(1,emp.getUsername());
		ps.setString(2,emp.getName());
		ps.setString(3,emp.getPassword());
		ps.executeUpdate();
		ps.close();
	}
	
	/**
	 * Returns an employee with the specified username from the database
	 * as an {@link Employee} object.
	 * @param username The username of the desired employee.
	 * @return The {@link Employee} or null if no employee was found.
	 * @throws SQLException 
	 */
	public Employee getEmployee(String username) throws SQLException {
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM employee WHERE username ='" + username + "'");
		
		while(rs.next()) {
			Employee e = new Employee();
			e.setUsername(rs.getString("username"));
			e.setName(rs.getString("name"));
			e.setPassword(rs.getString("password"));
			return e;
		}
		
		return null;
	}
	
	/**
	 * Returns an {@link #ArrayList} with every employee from the database as
	 * {@link Employee} objects.
	 * @return ArrayList with {@link employee}s or an empty ArrayList if no employees where found.
	 * @throws SQLException 
	 */
	public ArrayList<Employee> getEmployees() throws SQLException {
		ArrayList<Employee> employees = new ArrayList<Employee>();
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM employee");
		
		Employee e;
		while(rs.next()) {
			e = new Employee();
			e.setUsername(rs.getString("username"));
			e.setName(rs.getString("name"));
			e.setPassword(rs.getString("password"));
			employees.add(e);
		}
		
		return employees;
	}
	
	/**
	 * Adds a room to the database from a room object.
	 * (mest for testing, skal brått ikke drive å legge til/endre rom fra programmet til slutt)
	 * @param emp The room to be added.
	 * @throws SQLException
	 */
	public void addRoom(Room room) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("INSERT INTO room VALUES (?,?,?)");
		ps.setInt(1,room.getRoomID());
		ps.setString(2,room.getName());
		ps.setInt(3,room.getCapacity());
		ps.executeUpdate();
		ps.close();
	}
	
	/**
	 * Adds an activity to the database from an {@link Activity} object.
	 * @param act The activity to be added.
	 * @throws SQLException
	 */
	public void addActivity(Activity act) throws SQLException {
		// TODO: Date i java og sql er ikke kompatible, sql.date har bare dato, ikke tid
		
		//PreparedStatement ps = conn.prepareStatement("INSERT INTO activty VALUES (?,?,?,?,?,?)");
		// Midler tidig activityID, glemte auto increment i test databasen
		/*ps.setInt(1,2);
		ps.set
		ps.setString(3,emp.getPassword());
		ps.executeUpdate();
		ps.close();*/
	}
	
	/**
	 * Used for testing database output.
	 * @throws SQLException
	 */
	public void testPrint() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM employee");
		
		while(rs.next()) {
			System.out.println(rs.getString(1) + " " + rs.getString(2)+ " " + rs.getString(3));
		}
		
		System.out.println();
		
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT * FROM room");
		
		while(rs.next()) {
			System.out.println(rs.getString(1) + " " + rs.getString(2)+ " " + rs.getString(3));
		}	
	}
	
	public static void main(String[] args) {
		
		DBStorage dbc = new DBStorage();
		/*
		Employee e1 = new Employee("Carl Ivar Hagen", "bigcarl", "mamma");
		Employee e2 = new Employee("Ole Andreas Knudsen", "oleak", "qwerty");
		Employee e3 = new Employee("Gregory House", "hus", "L0Pq12Ze");
		Room r1 = new Room(202,"Bøttekott",2);
		Room r2 = new Room(303,"Latter",4);
		Room r3 = new Room(204,"Labben",30);
		
		try {
			dbc.addRoom(r1);
			dbc.addRoom(r2);
			dbc.addRoom(r3);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			dbc.testPrint();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		*/
		
		try {
			System.out.println(dbc.getEmployee("oletobs").getName());
			System.out.println(dbc.getEmployees().get(1).getName());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
