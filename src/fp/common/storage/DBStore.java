package fp.common.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.joda.time.DateTime;

import fp.common.models.Activity;
import fp.common.models.Employee;
import fp.common.models.Meeting;
import fp.common.models.Message;
import fp.common.models.Participant;
import fp.common.models.Room;


/**
 * <p>Stores and alters data in the database through {@link DBConnection}.</p>
 * @author fp2011-gr26
 */
public class DBStore {
	
	private static DBStore instance;
	private Connection conn;
	private HashMap<String,Employee> empCache;
	private HashMap<Integer,Room> roomCache;
	private HashMap<Integer,Activity> actCache;
	private HashMap<Integer,Meeting> mtngCache;
	private DBRetrieve dbr;
	
	public DBStore() {
		Storage s = Storage.getInstance();
		conn = s.getConn();
		empCache = s.empCache;
		roomCache = s.roomCache;
		actCache = s.actCache;
		mtngCache = s.mtngCache;
	}
	
	public static DBStore getInstance() {
		if(instance == null) instance = new DBStore();
		return instance;
	}

	/**
	 * Adds an employee to the database from an employee object.
	 * @param emp The employee to be added.
	 */
	public void addEmployee(Employee emp) {
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO employee VALUES (?,?,?)");
			ps.setString(1,emp.getUsername());
			ps.setString(2,emp.getName());
			ps.setString(3,emp.getPassword());
			ps.executeUpdate();
			ps.close();
			
			// Add to cache
			empCache.put(emp.getUsername(), emp);
			System.out.println("#DB: Adding " + emp.getUsername() + " to DB and cache.");
		} catch (SQLException e) {
			System.err.println("Could not add employee.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a room to the database from a room object.
	 * @param emp The room to be added.
	 */
	public void addRoom(Room room) {
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO room (name, capacity)" +
					"VALUES (?,?)");
			ps.setString(1,room.getName());
			ps.setInt(2,room.getCapacity());
			ps.executeUpdate();
			ps.close();
			
			// Add to cache
			roomCache.put(room.getRoomID(), room);
			System.out.println("#DB: Adding room " + room.getRoomID() + " to DB and cache.");
		} catch (SQLException e) {
			System.err.println("Could not add room.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds one or multiple messages to the database from a {@link Message} object.
	 * @param message The message to be added.
	 */
	public void addMessage(Message message) {
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO message " +
					"(isread,time,message,username,activityID) VALUES (?,?,?,?,?)");
			
			for (Participant p : message.getMeeting().getParticipants()) {
				ps.setBoolean(1,false);
				ps.setTimestamp(2,new Timestamp(message.getCreatedOn().getMillis()));
				ps.setString(3,message.getDescription());
				ps.setString(4,p.getEmployee().getUsername());
				ps.setInt(5,message.getMeeting().getId());
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
		} catch (SQLException e) {
			System.err.println("Could not add message(s).");
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds an activity to the database from an {@link Activity} object.
	 * @param act The activity to be added.
	 * @return Returns the activity id or -1 if the operation failed.
	 */
	public int addActivity(Activity act) {
		int activityID = -1;
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO activity " +
					"(starttime,endtime,description,username,location,ismeeting) " +
					"VALUES(?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			
			ps.setTimestamp(1, new Timestamp(act.getStartTime().getMillis()));
			ps.setTimestamp(2, new Timestamp(act.getEndTime().getMillis()));
			ps.setString(3, act.getDescription());
			ps.setString(4, act.getOwner().getUsername());
			ps.setString(5, act.getLocation());
			ps.setBoolean(6, false);
			ps.executeUpdate();
			
			ResultSet rs = ps.getGeneratedKeys();
			
			while(rs.next() && rs != null) {
				activityID = rs.getInt(1);
			}
			ps.close();
			
			/*
			 * Fetches the auto generated activity ID and adds it to the activity object
			 */
			act.setId(activityID);
			
			// Add to cache
			actCache.put(act.getId(), act);
			System.out.println("#DB: Adding activity " + act.getId() + " to DB and cache.");
		} catch (SQLException e) {
			System.err.println("Could not add activity.");
			e.printStackTrace();
		}
		
		return activityID;
	}
	
	/**
	 * <p>Adds a meeting to the database from a {@link Meeting} object.</p>
	 * <p>Also adds participants to the participant table through the participant
	 * list in the {@link Meeting} object.</p>
	 * <p>An invited employee's status is set to {@link Participant.Status#AWAITING_REPLY}
	 * by default.</p>
	 * @param m The meeting to be added.
	 * @return The auto generated id.
	 */
	public int addMeeting(Meeting m) {
		int id = -1;
		/*
		 * Stores the meeting in the meeting table.
		 */
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement("INSERT INTO activity " +
					"(starttime,endtime,description,username,roomID,ismeeting) " +
					"VALUES(?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			
			ps.setTimestamp(1, new Timestamp(m.getStartTime().getMillis()));
			ps.setTimestamp(2, new Timestamp(m.getEndTime().getMillis()));
			ps.setString(3, m.getDescription());
			ps.setString(4, m.getOwner().getUsername());
			ps.setInt(5, m.getRoom().getRoomID());
			ps.setBoolean(6, true);
			ps.executeUpdate();
			
			/*
			 * Fetches the auto generated activityID and adds it to the meeting object.
			 */
			ResultSet rs = ps.getGeneratedKeys();
			while(rs.next() && rs != null) {
				m.setId(rs.getInt(1));
				id = m.getId();
			}
			
			// Add to cache
			mtngCache.put(m.getId(), m);
			System.out.println("#DB: Adding meeting " + m.getId() + " to DB and cache.");
			
			ps.close();
		} catch (SQLException e) {
			System.err.println("Could not add meeting.");
			e.printStackTrace();
		}
		
		/*
		 * Stores the invited employees in the participant table.
		 */
		try {
			ps = conn.prepareStatement("INSERT INTO participant VALUES(?,?,?)");
			
			for (Participant p : m.getParticipants()) {
				ps.setInt(1, p.getStatus().ordinal());
				ps.setString(2, p.getEmployee().getUsername());
				ps.setInt(3, m.getId());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			System.err.println("Could not add participants to the meeting.");
			e.printStackTrace();
		}
		
		/*
		 * Adds invite messages
		 */
		try {
			ps = conn.prepareStatement("INSERT INTO message " +
			"(isread,time,message,username,activityID) VALUES (?,?,?,?,?)");
			for (Participant p : m.getParticipants()) {
				ps.setBoolean(1,false); // isRead = false
				ps.setTimestamp(2,new Timestamp(new Date().getTime()));
				ps.setString(3,"Du har blitt invitert til møte den "+
						m.getStartTime()+" av "+m.getOwner().getName()+".");
				ps.setString(4, p.getEmployee().getUsername());
				ps.setInt(5, m.getId());
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
		} catch (SQLException e) {
			System.err.println("Could not add messages.");
			e.printStackTrace();
		}
		return id;
	}
	
	/**
	 * Adds a participant to a meeting, by a {@link Meeting} and an {@link Employee} object.
	 * An invited employee's status is set to {@link Participant.Status#AWAITING_REPLY}
	 * by default.
	 * @param meeting The meeting
	 * @param emp The employee
	 */
	public void addPartcipant(Meeting meeting, Employee emp) {
		addPartcipantByIDs(meeting.getId(), emp.getUsername());
	}
	
	/**
	 * Adds a participant to a meeting, by a meeting's ID and an employee's username.
	 * An invited employee's status is set to {@link Participant.Status#AWAITING_REPLY}
	 * by default.
	 * @param meetingID The meeting's id
	 * @param username The participants username
	 */
	public void addPartcipantByIDs(int meetingID, String username) {
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO participant VALUES (?,?,?)");
			ps.setInt(1,Participant.Status.AWAITING_REPLY.ordinal());
			ps.setString(2,username);
			ps.setInt(3,meetingID);
			ps.executeUpdate();
			ps.close();
			
			// Updating cache
			Meeting m = dbr.getMeeting(meetingID);
			Employee e = dbr.getEmployee(username);
			Participant p = new Participant(e, Participant.Status.AWAITING_REPLY);
			m.addParticipant(p);
			System.out.println("#DB: Adding "+e.getUsername()+" as a meeting "+m.getId()+" participant. In DB and cache");
			
			// Create message
			ps = conn.prepareStatement("INSERT INTO message " +
			"(isread,time,message,username,activity) VALUES (?,?,?,?,?)");
			
			ps.setBoolean(1,false); // isRead = false
			ps.setTimestamp(2,new Timestamp(new Date().getTime()));
			ps.setString(3,"Du er invitert til møtet den " +m.getStartTime()+
						" satt opp av "+m.getOwner().getName()+".");
			ps.setString(4, username);
			ps.setInt(5, meetingID);
			ps.executeUpdate();
			ps.close();
			
		} catch (SQLException e) {
			System.err.println("Could not add participant.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Changes a participants status for a meeting, based on a {@link Meeting} and
	 * a {@link Participant} object.
	 * @param m The meeting
	 * @param p The participant
	 */
	public void changeInviteStatus(Meeting m, Participant p) {
		changeInviteStatusByIDs(m.getId(), p.getEmployee().getUsername(), p.getStatus().ordinal());
	}
	
	/**
	 * Changes a participants status for a meeting, based on a meetings ID, username and status.
	 * @param activityID The ID
	 * @param username The username
	 * @param status The status as an integer
	 */
	public void changeInviteStatusByIDs(int activityID, String username, int status) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE participant SET status = ? " +
					"WHERE activityID = " + activityID + " " +
					"AND username = '" + username + "'");
			ps.setInt(1,status);
			ps.executeUpdate();
			ps.close();
			
			// Update cache
			Meeting m = dbr.getMeeting(activityID);
			for (Participant p : m.getParticipants()) {
				if(p.getEmployee().getUsername() == username) {
					p.setStatus(Participant.intToEnum(status));
					System.out.println("#DB: Changed "+p.getEmployee().getUsername()+" participant status. In DB and cache");
				}
			}
			
			// Create messages
			if(Participant.intToEnum(status) == Participant.Status.NOT_ATTENDING) {
				ps = conn.prepareStatement("INSERT INTO message " +
				"(isread,time,message,username,activityID) VALUES (?,?,?,?,?)");
				Employee decliner = dbr.getEmployee(username);
				for (Participant p : m.getParticipants()) {
					ps.setBoolean(1,false); // isRead = false
					ps.setTimestamp(2,new Timestamp(new Date().getTime()));
					ps.setString(3,decliner.getName()+" har avslått " +
							"invitasjon til møtet den "+m.getStartTime()+".");
					ps.setString(4, p.getEmployee().getUsername());
					System.out.println(p.getEmployee().getUsername());
					ps.setInt(5, activityID);
					ps.addBatch();
				}
				ps.executeBatch();
				ps.close();
			}
		} catch (SQLException e) {
			System.err.println("Could not change participants status.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Marks a meeting, by a {@link Meeting} object,
	 * in the database as canceled (meetings should not be deleted).
	 * @param meeting The meeting to be canceled
	 */
	public void cancelMeeting(Meeting meeting) {
		cancelMeetingByID(meeting.getId());
	}
	
	/**
	 * Marks a meeting, by the meeting's ID,
	 * in the database as canceled (meetings should not be deleted).
	 * @param meetingID The meeting's ID
	 */
	public void cancelMeetingByID(int meetingID) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE activity SET cancelled = ? " +
					"WHERE activityID = " + meetingID);
			ps.setBoolean(1,true);
			ps.executeUpdate();
			
			// Create messages
			ps = conn.prepareStatement("INSERT INTO message " +
			"(isread,time,message,username,activityID) VALUES (?,?,?,?,?)");
			Meeting m = dbr.getMeeting(meetingID);
			for (Participant p : m.getParticipants()) {
				String username = p.getEmployee().getUsername();
				ps.setBoolean(1,false); // isRead = false
				ps.setTimestamp(2,new Timestamp(new Date().getTime()));
				ps.setString(3,"Møte den "+m.getStartTime()+" er avlyst av "+m.getOwner().getName()+".");
				ps.setString(4, username);
				ps.setInt(5, meetingID);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			
			// Remove from cache
			mtngCache.remove(meetingID);
			System.out.println("#DB: Canceling meeting " + meetingID + " in DB and removing from cache.");
		} catch (SQLException e) {
			System.err.println("Could not cancel meeting.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Change a specified meeting.
	 * @param meeting The meeting
	 */
	public void changeMeeting(Meeting meeting) {
		try {
			// Get pre-updated meeting from cache/db
			Meeting m = dbr.getMeeting(meeting.getId());
			
			PreparedStatement ps = conn.prepareStatement("UPDATE activity " +
					"SET starttime = ?, endtime = ?, description = ?, location = ?, " +
					"roomID = ? WHERE activityID = " + meeting.getId());
			ps.setTimestamp(1, new Timestamp(meeting.getStartTime().getMillis()));
			ps.setTimestamp(2, new Timestamp(meeting.getEndTime().getMillis()));
			ps.setString(3, meeting.getDescription());
			ps.setString(4, meeting.getLocation());
			ps.setInt(5, meeting.getRoom().getRoomID());
			ps.executeUpdate();
			ps.close();
			
			// Create messages
			ps = conn.prepareStatement("INSERT INTO message " +
			"(isread,time,message,username,activityID) VALUES (?,?,?,?,?)");
			for (Participant p : m.getParticipants()) {
				String username = p.getEmployee().getUsername();
				ps.setBoolean(1,false); // isRead = false
				ps.setTimestamp(2,new Timestamp(new Date().getTime()));
				ps.setString(3,"Møte den "+m.getStartTime()+" er endret av "+m.getOwner().getName()+".");
				ps.setString(4, username);
				ps.setInt(5, m.getId());
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			
			// Update in cache
			m.setStartTime(meeting.getStartTime());
			m.setEndTime(meeting.getEndTime());
			m.setDescription(meeting.getDescription());
			m.setLocation(meeting.getLocation());
			m.setRoom(meeting.getRoom());
			System.out.println("#DB: Updating meeting "+m.getId()+" in DB and cache");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Marks an activity, by a {@link Activity} object,
	 * in the database as canceled (activities should not be deleted).
	 * @param act The activity to be canceled
	 */
	public void cancelActivity(Activity act) {
		cancelActivityByID(act.getId());
	}
	
	/**
	 * Marks an activity, by the ID,
	 * in the database as canceled (activities should not be deleted).
	 * @param actID The ID
	 */
	public void cancelActivityByID(int actID) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE activity SET cancelled = ? " +
					"WHERE activityID = " + actID);
			ps.setBoolean(1,true);
			ps.executeUpdate();
			ps.close();
			
			// Remove from cache
			actCache.remove(actID);
			System.out.println("#DB: Canceling activity " + actID + " in DB and removing from cache.");
		} catch (SQLException e) {
			System.err.println("Could not cancel meeting.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Change a specified activity.
	 * @param act The activity.
	 */
	public void changeActivity(Activity act) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE activity " +
					"SET starttime = ?, endtime = ?, description = ?, location = ? " +
					"WHERE activityID = " + act.getId());
			ps.setTimestamp(1, new Timestamp(act.getStartTime().getMillis()));
			ps.setTimestamp(2, new Timestamp(act.getEndTime().getMillis()));
			ps.setString(3, act.getDescription());
			ps.setString(4, act.getLocation());
			ps.executeUpdate();
			ps.close();
			
			// Update in cache
			Activity a = actCache.get(act.getId());
			a.setStartTime(act.getStartTime());
			a.setEndTime(act.getEndTime());
			a.setDescription(act.getDescription());
			a.setLocation(act.getLocation());
			System.out.println("#DB: Updated activity "+a.getId()+" in DB and cache");
		} catch (SQLException e) {
			System.err.println("Could not change activity.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Marks a message, by a {@link Meeting} and a {@link Employee} object,
	 * in the database as read.
	 * @param meeting The meeting
	 * @param emp The employee
	 */
	public void markMessageAsRead(Meeting meeting,Employee emp) {
		markMessageAsReadByIDs(meeting.getId(), emp.getUsername());
	}
	
	/**
	 * Marks a message, by the meeting's ID and the employee's username,
	 * in the database as read.
	 * @param meetingID The meeting's ID
	 * @param username The employee's username
	 */
	public void markMessageAsReadByIDs(int meetingID,String username) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE message SET isread = ? " +
					"WHERE activityID = " + meetingID + " AND username = '" + username + "'");
			ps.setBoolean(1,true);
			ps.executeUpdate();
			ps.close();
			System.out.println("#DB: Marked message as read in DB");
		} catch (SQLException e) {
			System.err.println("Could not mark message as read.");
			e.printStackTrace();
		}
	}
	
	public void setDBR(DBRetrieve dbr) {
		this.dbr = dbr;
	}
	
	public void cancelEmpActivities(Employee emp) {
		cancelEmpActivitiesByUsername(emp.getUsername());
	}
	
	public void cancelEmpActivitiesByUsername(String username) {
		try {
			Statement s = conn.createStatement();
			
			s.execute("UPDATE activity SET cancelled = true WHERE username ='" + username + "'");
			s.close();
			
			System.out.println("#DB: Canceling " + username + "'s activities/meetings in DB");
			System.out.println(" - for testing only, please restart to clear cache");
		} catch (SQLException e) {
			System.err.println("Could not cancel activities.");
			e.printStackTrace();
		}
	}
}
