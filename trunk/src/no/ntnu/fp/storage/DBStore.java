package no.ntnu.fp.storage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import no.ntnu.fp.model.Activity;
import no.ntnu.fp.model.Employee;
import no.ntnu.fp.model.Meeting;
import no.ntnu.fp.model.Message;
import no.ntnu.fp.model.Participant;
import no.ntnu.fp.model.Room;

/**
 * <p>Stores and alters data in the database through {@link DBConnection}.</p>
 * @author fp2011-gr26
 */
public class DBStore extends DBConnection {

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
		} catch (SQLException e) {
			System.err.println("Could not add room.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds an alert/message to the database from a {@link Message} object.
	 * @param message The message/alert to be added.
	 */
	public void addAlert(Message message) {
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO alert VALUES (?,?,?,?,?)");
			ps.setBoolean(1,message.getRead());
			ps.setTimestamp(2,new Timestamp(message.getCreatedOn().getTime()));
			ps.setString(3,message.getDescription());
			ps.setString(4,message.getEmployee().getUsername());
			ps.setInt(5,message.getMeeting().getId());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			System.err.println("Could not add alert.");
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
			
			ps.setTimestamp(1, new Timestamp(act.getStartTime().getTime()));
			ps.setTimestamp(2, new Timestamp(act.getEndTime().getTime()));
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
			
			ps.setTimestamp(1, new Timestamp(m.getStartTime().getTime()));
			ps.setTimestamp(2, new Timestamp(m.getEndTime().getTime()));
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
		} catch (SQLException e) {
			System.err.println("Could not cancel meeting.");
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
