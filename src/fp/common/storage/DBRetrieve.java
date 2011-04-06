package fp.common.storage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;

import fp.common.models.Activity;
import fp.common.models.Employee;
import fp.common.models.Meeting;
import fp.common.models.Message;
import fp.common.models.Participant;
import fp.common.models.Room;


/**
 * <p>Retrieves data from the database through {@link DBConnection}.</p>
 * @author fp2011-gr26
 */
public class DBRetrieve {
	
	private static DBRetrieve instance;
	private Connection conn;
	private HashMap<String,Employee> empCache;
	private HashMap<Integer,Room> roomCache;
	private HashMap<Integer,Activity> actCache;
	private HashMap<Integer,Meeting> mtngCache;
	private DBStore dbs;
	
	public DBRetrieve() {
		Storage s = Storage.getInstance();
		conn = s.getConn();
		empCache = s.empCache;
		roomCache = s.roomCache;
		actCache = s.actCache;
		mtngCache = s.mtngCache;
		dbs = DBStore.getInstance();
		dbs.setDBR(this);
	}
	
	public Employee login(String username,String passwd) {
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM employee WHERE username = '" +
					username + "' AND password = '" + passwd + "'");
			
			Employee e = new Employee();
			while(rs.next()) {
				e.setName(rs.getString("name"));
				e.setUsername(rs.getString("username"));
				return e;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static DBRetrieve getInstance() {
		if(instance == null) instance = new DBRetrieve();
		return instance;
	}
	
	/**
	 * Returns an employee with the specified username from the database
	 * as an {@link Employee} object.
	 * @param username The username of the desired employee.
	 * @return The {@link Employee} or null if no employee was found.
	 */
	public Employee getEmployee(String username) {
		// Check cache
		if(empCache.containsKey(username)) {
			System.out.println("Getting " + username + " from cache.");
			return empCache.get(username);
			
		}
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM employee WHERE username ='" + username + "'");
			
			while(rs.next()) {
				Employee e = new Employee();
				e.setUsername(rs.getString("username"));
				e.setName(rs.getString("name"));
				// Add to cache
				empCache.put(e.getUsername(), e);
				System.out.println("Adding " + e.getUsername() + " to cache.");
				return e;
			}
			s.close();
		} catch (SQLException e) {
			System.err.println("Could not get employee.");
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Returns an {@link #ArrayList} with every employee from the database as
	 * {@link Employee} objects.
	 * @return ArrayList with {@link employee}s or an empty ArrayList if no employees where found.
	 */
	public ArrayList<Employee> getAllEmployees() {
		ArrayList<Employee> employees = new ArrayList<Employee>();
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM employee");
			Employee e;
			while(rs.next()) {
				String username = rs.getString("username");
				if(empCache.containsKey(username)) {
					e = empCache.get(username);
					System.out.println("Getting " + username + " from cache.");
				} else {
					e = new Employee();
					e.setUsername(username);
					e.setName(rs.getString("name"));
					System.out.println("Adding " + e.getUsername() + " to cache.");
					empCache.put(username, e);
				}
				employees.add(e);
			}
			s.close();
		} catch (SQLException e1) {
			System.err.println("Error fetching employees.");
			e1.printStackTrace();
		}
		
		return employees;
	}
	
	/**
	 * Returns an {@link #ArrayList} with every unread alert from the database as
	 * {@link Message} objects.
	 * @return ArrayList with {@link Message}s.
	 */
	public ArrayList<Message> getAllAlerts() {
		ArrayList<Message> alerts = new ArrayList<Message>();
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM alert");
			
			Message m;
			while(rs.next()) {
				m = new Message();
				m.setRead(rs.getBoolean("isread"));
				m.setCreatedOn(new DateTime(rs.getTimestamp("time").getTime()));
				m.setDescription(rs.getString("message"));
				m.setEmployee(getEmployee(rs.getString("username")));
				m.setMeeting(getMeeting(rs.getInt("activityID")));
				alerts.add(m);
			}
			s.close();
		} catch (SQLException e1) {
			System.err.println("Error fetching alerts.");
			e1.printStackTrace();
		}
		
		return alerts;
	}
	
	/**
	 * <p>Returns every employee invited to the specified meeting as
	 * an {@link #ArrayList} with {@link Participant} objects.<br />
	 * Uses a {@link Meeting} object to locate the participants.</p>
	 * <p>Does not return the owner, see {@link Meeting#getOwner()}.</p>
	 * @param meeting The meeting
	 * @return ArrayList with {@link employee}s or an empty ArrayList if no employees where found.
	 */
	private ArrayList<Participant> getParticipants(Meeting meeting) {
		return getParticipantsByMeetingID(meeting.getId());
	}
	
	/**
	 * <p>Returns every employee invited to the specified meeting as
	 * an {@link #ArrayList} with {@link Participant} objects.<br />
	 * Uses the meeting's id.</p>
	 * <p>Does not return the owner, see {@link Meeting#getOwner()}.</p>
	 * @param activityID The meeting's ID
	 * @return ArrayList with {@link employee}s or an empty ArrayList if no employees where found.
	 */
	private ArrayList<Participant> getParticipantsByMeetingID(int activityID) {
		ArrayList<Participant> participants = new ArrayList<Participant>();
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT e.username, e.name, e.password, p.status " +
					"FROM employee e, participant p " +
					"WHERE p.activityID = " + activityID + " AND e.username = p.username");
			
			Employee e;
			Participant p;
			while(rs.next()) {
				String username = rs.getString("username");
				if(empCache.containsKey(username)) {
					System.out.println("Getting " + username + " from cache.");
					e = empCache.get(username);
				} else {
					e = new Employee();
					e.setUsername(username);
					e.setName(rs.getString("name"));
					e.setPassword(rs.getString("password"));
					empCache.put(username, e);
					System.out.println("Adding " + username + " to cache.");
				}
				p = new Participant(e,Participant.intToEnum(rs.getInt("status")));
				participants.add(p);
			}
			s.close();
		} catch (SQLException ex) {
			System.err.println("Could not get participants");
			ex.printStackTrace();
		}
		
		return participants;
	}
	
	/**
	 * Returns a room as a {@link Room} object.
	 * @param roomId The room's id
	 * @return The room
	 */
	public Room getRoom(int roomId) {
		// Check cache
		if(roomCache.containsKey(roomId)) {
			System.out.println("Getting room " + roomId + " from cache.");
			return roomCache.get(roomId);
		}
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM room WHERE roomID =" + roomId);
			
			while(rs.next()) {
				Room r = new Room();
				r.setRoomID(rs.getInt("roomID"));
				r.setName(rs.getString("name"));
				r.setCapacity(rs.getInt("capacity"));
				// Add to cache
				roomCache.put(r.getRoomID(), r);
				System.out.println("Adding room " + r.getRoomID() + " to cache.");
				return r;
			}
			s.close();
		} catch (SQLException e) {
			System.err.println("Could not get room.");
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Returns every room in the database as an {@link ArrayList} with {@link Room} objects.
	 * @return ArrayList with all the rooms.
	 */
	public ArrayList<Room> getAllRooms() {
		ArrayList<Room> rooms = new ArrayList<Room>();
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM room");
			
			Room r;
			while(rs.next()) {
				int id = rs.getInt("roomID");
				// Checking cache
				if(roomCache.containsKey(id)) {
					r = roomCache.get(id);
					System.out.println("Getting room " + id + " from cache.");
				} else {
					r = new Room();
					r.setName(rs.getString("name"));
					r.setCapacity(rs.getInt("capacity"));
					r.setRoomID(id);
					// Adding to cache
					System.out.println("Adding room " + id + " to cache.");
					roomCache.put(id, r);
				}
				rooms.add(r);
			}
			s.close();
		} catch (SQLException e) {
			System.err.println("Could not get rooms.");
			e.printStackTrace();
		}
		
		return rooms;
	}
	
	/**
	 * Returns all activities (single user activities) in the database
	 * as an {@link ArrayList} with {@link Activity} objects.
	 * @return ArrayList with all the activities.
	 */
	public ArrayList<Activity> getAllActivities() {
		ArrayList<Activity> activities = new ArrayList<Activity>();
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM activity WHERE ismeeting = false " +
					"AND (cancelled = false OR cancelled IS NULL)");
			
			Activity a;
			while(rs.next()) {
				int id = rs.getInt("activityID");
				// Checking cache
				if(actCache.containsKey(id)) {
					a = actCache.get(id);
					System.out.println("Getting activity " + a.getId() + ", owned by " + a.getOwner() + " from cache.");
				} else {
					a = new Activity();
					a.setOwner(getEmployee(rs.getString("username")));
					a.setDescription(rs.getString("description"));
					a.setLocation(rs.getString("location"));
					a.setId(id);
					a.setStartTime(new DateTime(rs.getTimestamp("starttime").getTime()));
					a.setEndTime(new DateTime(rs.getTimestamp("endtime").getTime()));
					// Add to cache
					actCache.put(id, a);
					System.out.println("Adding activity " + a.getId() + ", owned by " + a.getOwner() + " to cache.");
				}
				activities.add(a);
			}
			s.close();
		} catch (SQLException e) {
			System.err.println("Could not get activities.");
			e.printStackTrace();
		}
		
		return activities;
	}
	
	/**
	 * Returns a meeting based on the meeting's ID.
	 * @return The meeting
	 */
	public Meeting getMeeting(int meetingID) {
		if(mtngCache.containsKey(meetingID)) {
			Meeting m1 = mtngCache.get(meetingID); // EDIT
			System.out.println("Getting meeting " + m1.getId() + ", owned by " + m1.getOwner() + " from cache.");
			return m1;
		}
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM activity " +
					"WHERE activityID = " + meetingID);
			
			Meeting m;
			while(rs.next()) {
				m = new Meeting();
				m.setOwner(getEmployee(rs.getString("username")));
				m.setDescription(rs.getString("description"));
				m.setLocation(rs.getString("location"));
				m.setRoom(getRoom(rs.getInt("roomID")));
				m.setId(rs.getInt("activityID"));
				m.setStartTime(new DateTime(rs.getTimestamp("starttime").getTime()));
				m.setEndTime(new DateTime(rs.getTimestamp("endtime").getTime()));
				m.setParticipants(getParticipantsByMeetingID(m.getId()));
				System.out.println("Adding meeting " + m.getId() + ", owned by " + m.getOwner() + " to cache.");
				mtngCache.put(m.getId(), m);
				return m;
			}
			s.close();
		} catch (SQLException e) {
			System.err.println("Could not get meetings.");
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Returns all meetings (multiple user activities) in the database
	 * as an {@link ArrayList} with {@link Meeting} objects.
	 * @return ArrayList with all the meetings.
	 */
	public ArrayList<Meeting> getAllMeetings() {
		ArrayList<Meeting> meetings = new ArrayList<Meeting>();
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM activity WHERE ismeeting = true " +
					"AND (cancelled = false OR cancelled IS NULL)");
			
			Meeting m;
			while(rs.next()) {
				int id = rs.getInt("activityID");
				// Check cache
				if(mtngCache.containsKey(id)) {
					m = mtngCache.get(id);
					System.out.println("Getting meeting " + m.getId() + ", owned by " + m.getOwner() + " from cache.");
				} else {
					m = new Meeting();
					m.setOwner(getEmployee(rs.getString("username")));
					m.setDescription(rs.getString("description"));
					m.setLocation(rs.getString("location"));
					m.setRoom(getRoom(rs.getInt("roomID")));
					m.setId(id);
					m.setStartTime(new DateTime(rs.getTimestamp("starttime").getTime()));
					m.setEndTime(new DateTime(rs.getTimestamp("endtime").getTime()));
					m.setParticipants(getParticipantsByMeetingID(id));
					// Add to cache
					mtngCache.put(id, m);
					System.out.println("Adding meeting " + m.getId() + ", owned by " + m.getOwner() + " to cache.");
				}
				meetings.add(m);
			}
			s.close();
		} catch (SQLException e) {
			System.err.println("Could not get meetings.");
			e.printStackTrace();
		}
		
		return meetings;
	}
	
	/**
	 * Returns an {@link ArrayList} with an employee's activities,
	 * based on an {@link Employee} object.
	 * @param emp The employee 
	 * @return The ArrayList with activities.
	 */
	public ArrayList<Activity> getEmpActivities(Employee emp) {
		return getEmpActivitiesByUsername(emp.getUsername());
	}
	
	/**
	 * Returns an {@link ArrayList} with an employee's activities,
	 * based on username.
	 * @param username The username
	 * @return The ArrayList with activities.
	 */
	public ArrayList<Activity> getEmpActivitiesByUsername(String username) {
		ArrayList<Activity> activities = new ArrayList<Activity>();
		try {
			Statement s = conn.createStatement();
			
			/*
			 * Personal activities can't reserve a room.
			 */
			ResultSet rs = s.executeQuery("SELECT * FROM activity WHERE username ='"
					+ username + "' AND ismeeting = false " +
					"AND (cancelled = false OR cancelled IS NULL)");
			
			Activity a;
			while(rs.next()) {
				int id = rs.getInt("activityID");
				// Check cache
				if(actCache.containsKey(id)) {
					a = actCache.get(id);
					System.out.println("Getting activity " + a.getId() + ", owned by " + a.getOwner() + " from cache.");
				} else {
					a = new Activity();
					a.setOwner(getEmployee(username));
					a.setDescription(rs.getString("description"));
					a.setLocation(rs.getString("location"));
					a.setId(id);
					a.setStartTime(new DateTime(rs.getTimestamp("starttime").getTime()));
					a.setEndTime(new DateTime(rs.getTimestamp("endtime").getTime()));
					// Add to cache
					actCache.put(id, a);
					System.out.println("Adding activity " + a.getId() + ", owned by " + a.getOwner() + " to cache.");
				}
				activities.add(a);
			}
			s.close();
		} catch (SQLException e) {
			System.err.println("Could not get employee's activities.");
			e.printStackTrace();
		}
		
		return activities;
	}
	
	/**
	 * Returns an {@link ArrayList} with an employee's meetings,
	 * based on an {@link Employee} object.
	 * @param emp The employee 
	 * @return The ArrayList with meetings.
	 */
	public ArrayList<Meeting> getEmpMeetings(Employee emp) {
		return getEmpMeetingsByUsername(emp.getUsername());
	}
	
	/**
	 * Returns an {@link ArrayList} with an employee's meetings, based on username.
	 * @param username The username 
	 * @return The ArrayList with meetings.
	 */
	public ArrayList<Meeting> getEmpMeetingsByUsername(String username) {
		ArrayList<Meeting> meetings = new ArrayList<Meeting>();
		try {
			Statement s = conn.createStatement();
			
			/*
			 * Fetches every meeting that the employee is invited to
			 */
			ResultSet rs = s.executeQuery("SELECT a.username, a.description, a.location, " +
					"a.activityID, a.starttime, a.endtime " +
					"FROM activity a, participant p " +
					"WHERE a.activityID = p.activityID AND p.username ='" + username + "' " +
					"AND ismeeting = true AND (cancelled = false OR cancelled IS NULL)");
			
			Meeting m;
			while(rs.next()) {
				int id = rs.getInt("activityID");
				// Check cache
				if(mtngCache.containsKey(id)) {
					m = mtngCache.get(id);
					System.out.println("Getting meeting " + m.getId() + ", owned by " + m.getOwner() + " from cache.");
				} else {
					m = new Meeting();
					m.setOwner(getEmployee(rs.getString("username")));
					m.setDescription(rs.getString("description"));
					m.setLocation(rs.getString("location"));
					m.setRoom(getRoom(rs.getInt("roomID")));
					m.setId(id);
					m.setStartTime(new DateTime(rs.getTimestamp("starttime").getTime()));
					m.setEndTime(new DateTime(rs.getTimestamp("endtime").getTime()));
					m.setParticipants(getParticipantsByMeetingID(id));
					// Add to cache
					mtngCache.put(id, m);
					System.out.println("Adding meeting " + m.getId() + ", owned by " + m.getOwner() + " to cache.");
				}
				meetings.add(m);
			}
			
			/*
			 * Fetches every meeting that the employee has created
			 */
			rs = s.executeQuery("SELECT * FROM activity " +
					"WHERE username ='" + username + "' AND ismeeting = true " +
					"AND (cancelled = false OR cancelled IS NULL)");
			
			while(rs.next()) {
				int id = rs.getInt("activityID");
				// Check cache
				if(mtngCache.containsKey(id)) {
					m = mtngCache.get(id);
					System.out.println("Getting meeting " + m.getId() + ", owned by " + m.getOwner() + " from cache.");
				} else {
					m = new Meeting();
					m.setOwner(getEmployee(rs.getString("username")));
					m.setDescription(rs.getString("description"));
					m.setLocation(rs.getString("location"));
					m.setId(id);
					m.setStartTime(new DateTime(rs.getTimestamp("starttime").getTime()));
					m.setEndTime(new DateTime(rs.getTimestamp("endtime").getTime()));
					m.setParticipants(getParticipantsByMeetingID(id));
					// Add to cache
					mtngCache.put(id, m);
					System.out.println("Adding meeting " + m.getId() + ", owned by " + m.getOwner() + " to cache.");
				}
				meetings.add(m);
			}
			s.close();
		} catch (SQLException e) {
			System.err.println("Could not get employee's meetings.");
			e.printStackTrace();
		}
		
		return meetings;
	}
	
	/**
	 * Returns an {@link ArrayList} with an employee's alerts(messages),
	 * based on an {@link Employee} object.
	 * @param emp The employee
	 * @return The ArrayList with alerts.
	 */
	public ArrayList<Message> getEmpAlerts(Employee emp) {
		return getEmpAlertsByUsername(emp.getUsername());
	}
	
	/**
	 * Returns an {@link ArrayList} with an employee's alerts(messages),
	 * based on username.
	 * @param username The username
	 * @return The ArrayList with alerts.
	 */
	public ArrayList<Message> getEmpAlertsByUsername(String username) {
		ArrayList<Message> alerts = new ArrayList<Message>();
		try {
			Statement s = conn.createStatement();
			
			/*
			 * Personal activities can't reserve a room.
			 */
			ResultSet rs = s.executeQuery("SELECT * FROM alert WHERE username ='"
					+ username + "'");
			
			Message m;
			while(rs.next()) {
				m = new Message();
				m.setCreatedOn(new DateTime(rs.getTimestamp("time").getTime()));
				m.setDescription(rs.getString("message"));
				m.setEmployee(getEmployee(username));
				m.setMeeting(getMeeting(rs.getInt("activityID")));
				m.setRead(rs.getBoolean("isread"));
				alerts.add(m);
			}
			s.close();
		} catch (SQLException e) {
			System.err.println("Could not get employee's activities.");
			e.printStackTrace();
		}
		
		return alerts;
	}
}
