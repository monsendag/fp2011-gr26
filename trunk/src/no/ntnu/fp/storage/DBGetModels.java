package no.ntnu.fp.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.joda.time.DateTime;

import no.ntnu.fp.model.Activity;
import no.ntnu.fp.model.Employee;
import no.ntnu.fp.model.Meeting;
import no.ntnu.fp.model.Message;
import no.ntnu.fp.model.Participant;
import no.ntnu.fp.model.Room;

/**
 * Class used for retrieving everything from the database and store it as object models.
 * 
 * @author fp2011-gr26
 */
public class DBGetModels extends DBConnection {
	
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
				e = new Employee();
				e.setUsername(rs.getString("username"));
				e.setName(rs.getString("name"));
				e.setPassword(rs.getString("password"));
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
	 * {@link Message} objects. Uses a cached list of meetings.
	 * @return ArrayList with {@link Message}s.
	 */
	public ArrayList<Message> getAllAlerts(ArrayList<Meeting> meetings) {
		ArrayList<Message> alerts = new ArrayList<Message>();
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM alert " +
					"WHERE (isread = false OR isread IS NULL)");
			
			Message m;
			while(rs.next()) {
				m = new Message();
				m.setCreatedOn(new DateTime(rs.getTimestamp("time")));
				m.setDescription(rs.getString("message"));
				for (Meeting meeting : meetings) {
					if(meeting.getId() == rs.getInt("activityID")) {
						m.setMeeting(meeting);
					}
				}
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
				r = new Room();
				r.setName(rs.getString("name"));
				r.setCapacity(rs.getInt("capacity"));
				r.setRoomID(rs.getInt("roomID"));
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
	 * Uses a cached list of employees.
	 * @return ArrayList with all the activities.
	 */
	public ArrayList<Activity> getAllActivities(ArrayList<Employee> emps) {
		ArrayList<Activity> activities = new ArrayList<Activity>();
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM activity WHERE ismeeting = false " +
					"AND (cancelled = false OR cancelled IS NULL)");
			
			Activity a;
			while(rs.next()) {
				a = new Activity();
				for (Employee emp : emps) {
					if(emp.getUsername().equals(rs.getString("username"))) {
						a.setOwner(emp);
					}
				}
				a.setDescription(rs.getString("description"));
				a.setLocation(rs.getString("location"));
				a.setId(rs.getInt("activityID"));
				a.setStartTime(new DateTime(rs.getTimestamp("starttime")));
				a.setEndTime(new DateTime(rs.getTimestamp("endtime")));
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
	 * Returns all meetings (multiple user activities) in the database
	 * as an {@link ArrayList} with {@link Meeting} objects.
	 * Uses a cached list of employees.
	 * @return ArrayList with all the meetings.
	 */
	public ArrayList<Meeting> getAllMeetings(ArrayList<Employee> emps) {
		ArrayList<Meeting> meetings = new ArrayList<Meeting>();
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM activity WHERE ismeeting = true " +
					"AND (cancelled = false OR cancelled IS NULL)");
			
			Meeting m;
			while(rs.next()) {
				m = new Meeting();
				for (Employee e : emps) {
					if(e.getUsername().equals(rs.getString("username"))) {
						m.setOwner(e);
					}
				}
				m.setDescription(rs.getString("description"));
				m.setLocation(rs.getString("location"));
				m.setId(rs.getInt("activityID"));
				m.setStartTime(new DateTime(rs.getTimestamp("starttime")));
				m.setEndTime(new DateTime(rs.getTimestamp("endtime")));
				m.setParticipants(getParticipantsByMeetingID(emps,m.getId()));
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
	 * <p>Returns every employee invited to the specified meeting as
	 * an {@link #ArrayList} with {@link Participant} objects.<br />
	 * Uses the meeting's id and a cached list of employees.</p>
	 * <p>Does not return the owner, see {@link Meeting#getOwner()}.</p>
	 * @param activityID The meeting's ID
	 * @return ArrayList with {@link employee}s or an empty ArrayList if no employees where found.
	 */
	public ArrayList<Participant> getParticipantsByMeetingID(ArrayList<Employee> emps, int activityID) {
		ArrayList<Participant> participants = new ArrayList<Participant>();
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM participant " +
					"WHERE activityID =" + activityID);
			
			Participant p;
			while(rs.next()) {
				for (Employee emp : emps) {
					if(emp.getUsername().equals(rs.getString("username"))) {
						p = new Participant(emp,Participant.intToEnum(rs.getInt("status")));
						participants.add(p);
					}
				}
			}
			s.close();
		} catch (SQLException ex) {
			System.err.println("Could not get participants");
			ex.printStackTrace();
		}
		
		return participants;
	}
}
