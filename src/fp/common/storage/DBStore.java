package fp.common.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;

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
		} catch (SQLException e) {
			System.err.println("Could not add room.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds one or multiple alerts/messages to the database from a {@link Message} object.
	 * @param message The message/alert to be added.
	 */
	public void addAlerts(Message message) {
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO alert VALUES (?,?,?,?,?)");
			
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
			System.err.println("Could not add alert(s).");
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
	 */
	public void addMeeting(Meeting m) {
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
			}
			
			// Add to cache
			mtngCache.put(m.getId(), m);
			
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
			ps.close();
		} catch (SQLException e) {
			System.err.println("Could not add participants to the meeting.");
			e.printStackTrace();
		}
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
			ps.close();
			
			// Remove from cache
			mtngCache.remove(meetingID);
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
			
			// Update in cache
			Meeting m = mtngCache.get(meeting.getId());
			m.setStartTime(meeting.getStartTime());
			m.setEndTime(meeting.getEndTime());
			m.setDescription(meeting.getDescription());
			m.setLocation(meeting.getLocation());
			m.setRoom(meeting.getRoom());
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
		} catch (SQLException e) {
			System.err.println("Could not change activity.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Marks an alert(message), by a {@link Meeting} and a {@link Employee} object,
	 * in the database as read.
	 * @param meeting The meeting
	 * @param emp The employee
	 */
	public void markAlertAsRead(Meeting meeting,Employee emp) {
		markAlertAsReadByIDs(meeting.getId(), emp.getUsername());
	}
	
	/**
	 * Marks an alert(message), by the meeting's ID and the employee's username,
	 * in the database as read.
	 * @param meetingID The meeting's ID
	 * @param username The employee's username
	 */
	public void markAlertAsReadByIDs(int meetingID,String username) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE alert SET read = ? " +
					"WHERE activityID = " + meetingID + " AND username = '" + username + "'");
			ps.setBoolean(1,true);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			System.err.println("Could not mark alert as read.");
			e.printStackTrace();
		}
	}
}
