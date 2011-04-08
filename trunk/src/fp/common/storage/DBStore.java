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
 * <p>Stores and alters data in the database through {@link Storage}.</p>
 * <p>Uses the HashMaps in Storage as cache to avoid duplicate objects
 * in memory</p>
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
	
	/**
	 * <p>Default constructor</p>
	 * <p>Retrieves the connection and all cache objects through {@link Store}</p>
	 */
	public DBStore() {
		Storage s = Storage.getInstance();
		conn = s.getConn();
		empCache = s.empCache;
		roomCache = s.roomCache;
		actCache = s.actCache;
		mtngCache = s.mtngCache;
	}
	
	/**
	 * <p>Used to get the correct instance of this object.</p>
	 * @return this
	 */
	public static DBStore getInstance() {
		if(instance == null) instance = new DBStore();
		return instance;
	}

	/**
	 * <p>Adds an employee to the database from an employee object.</p>
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
	 * <p>Adds a room to the database from a room object.</p>
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
	 * <p>Adds one or multiple messages to the database from a {@link Message} object.</p>
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
	 * <p>Adds an activity to the database from an {@link Activity} object.</p>
	 * @param act The activity to be added.
	 * @return Returns the activity id or -1 if the operation failed.
	 */
	public int addActivity(Activity act) {
		int activityID = -1;
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO activity " +
					"(starttime,endtime,title,description,username,ismeeting) " +
					"VALUES(?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			
			ps.setTimestamp(1, new Timestamp(act.getStartTime().getMillis()));
			ps.setTimestamp(2, new Timestamp(act.getEndTime().getMillis()));
			ps.setString(3, act.getTitle());
			ps.setString(4, act.getDescription());
			ps.setString(5, act.getOwner().getUsername());
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
	 * list in the {@link Meeting} object and a message for each participant in the
	 * message table.</p>
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
					"(starttime,endtime,title,description,username,roomID,ismeeting) " +
					"VALUES(?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			
			System.out.println("######################### "+m.getOwner().getUsername());
			System.out.println("######################### "+m.getRoom().getRoomID());
			
			ps.setTimestamp(1, new Timestamp(m.getStartTime().getMillis()));
			ps.setTimestamp(2, new Timestamp(m.getEndTime().getMillis()));
			ps.setString(3, m.getTitle());
			ps.setString(4, m.getDescription());
			ps.setString(5, m.getOwner().getUsername());
			ps.setInt(6, m.getRoom().getRoomID());
			ps.setBoolean(7, true);
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
				System.out.println("######################### "+p.getEmployee().getUsername());
				System.out.println("######################### "+m.getId());
				
				ps.setInt(1, Participant.Status.AWAITING_REPLY.ordinal());
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
			"(isinvite,isread,time,message,username,activityID) VALUES (?,?,?,?,?,?)");
			for (Participant p : m.getParticipants()) {
				ps.setBoolean(1,true); // isInvite = true
				ps.setBoolean(2,false); // isRead = false
				ps.setTimestamp(3,new Timestamp(new Date().getTime()));
				ps.setString(4,"Du har blitt invitert til møte den "+
						m.getStartTime()+" av "+m.getOwner().getName()+".");
				ps.setString(5, p.getEmployee().getUsername());
				ps.setInt(6, m.getId());
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
	 * <p>Updates the participant list of a meeting to match that of another
	 * {@link ArrayList} with {@link Participant}s.</p>
	 * <p>Also adds an invite message for all new participants, and removes
	 * messages for removed participants.</p>
	 * @param meetingID The meeting's id
	 * @param emps The employees
	 */
	private void updatePartcipantsForMeeting(int meetingID, ArrayList<Participant> newParts) {
		try {
			// The current meeting in cache and its participant list
			Meeting m = dbr.getMeeting(meetingID);
			ArrayList<Participant> oldParts = m.getParticipants();
			
			/*
			 * Removes messages for removed participants
			 */
			/*
			PreparedStatement ps = conn.prepareStatement("DELETE FROM message " +
			"WHERE activityID = ? AND username = ?");
			
			
			for (Participant oldPart : oldParts) {
				boolean match = false;
				String oldEmp = oldPart.getEmployee().getUsername();
				for (Participant newPart : newParts) {
					String newEmp = newPart.getEmployee().getUsername();
					
					// Employee from the old list still in the new
					if(oldEmp.equals(newEmp)) {
						match = true;
					}
				}
				
				// If no match was found, remove old employee's message
				if(!match) {
					ps.setInt(1, meetingID);
					ps.setString(2, oldEmp);
					ps.addBatch();
				}
			}
			ps.executeBatch();
			ps.close();
			*/
			/*
			 * Creates messages for new participants
			 */
			/*
			ps = conn.prepareStatement("INSERT INTO message " +
			"(isinvite,isread,time,message,username,activity) VALUES (?,?,?,?,?,?)");
			
			for (Participant newPart : newParts) {
				boolean match = false;
				String newEmp = newPart.getEmployee().getUsername();
				for (Participant oldPart : oldParts) {
					String oldEmp = oldPart.getEmployee().getUsername();
					
					// New employee is part of the old list
					if(newEmp.equals(oldEmp)) {
						match = true;
					}
				}
				
				// If no match was found, add message for new employee
				if(!match) {
					ps.setBoolean(1,true); // isInvite = true
					ps.setBoolean(2,false); // isRead = false
					ps.setTimestamp(3,new Timestamp(new Date().getTime()));
					ps.setString(4,"Du er invitert til møtet den " +m.getStartTime()+
								" satt opp av "+m.getOwner().getName()+".");
					ps.setString(5, newEmp);
					ps.setInt(6, meetingID);
					ps.addBatch();
				}
			}
			ps.executeBatch();
			ps.close();
			*/
			/*
			 * Removes all old participants
			 */
			PreparedStatement ps = conn.prepareStatement("DELETE FROM participant " +
					"WHERE activityID = ? AND username = ?");
			
			for (Participant p : oldParts) {
				ps.setInt(1, meetingID);
				ps.setString(2, p.getEmployee().getUsername());
				ps.addBatch();
			}
			ps.executeBatch();
			
			/*
			 * Adds all new participants
			 */
			ps = conn.prepareStatement("INSERT INTO participant VALUES(?,?,?)");
			
			for (Participant p : newParts) {
				ps.setInt(1, Participant.Status.AWAITING_REPLY.ordinal());
				ps.setString(2, p.getEmployee().getUsername());
				ps.setInt(3, meetingID);
				ps.addBatch();
			}
			ps.executeBatch();
			
			/*
			 *  Updating current meeting in cache with new participants
			 */
			m.setParticipants(newParts);
			
		} catch (SQLException e) {
			System.err.println("Could not add participant(s).");
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>Changes a participants status for a meeting, based on a {@link Meeting} and
	 * a {@link Participant} object.</p>
	 * <p>If the participants status is changed to
	 * {@link Participant.Status#NOT_ATTENDING} a message will also be created
	 * for each participant invited to the specific meeting.</p>
	 * @param m The meeting
	 * @param p The participant
	 */
	public void changeInviteStatus(Meeting m, Participant p) {
		changeInviteStatusByIDs(m.getId(), p.getEmployee().getUsername(), p.getStatus());
	}
	
	/**
	 * <p>Changes a participants status for a meeting, based on a meetings ID, username and status.</p>
	 * <p>If the participants status is changed to
	 * {@link Participant.Status#NOT_ATTENDING} a message will also be created
	 * for each participant invited to the specific meeting.</p>
	 * @param activityID The ID
	 * @param username The username
	 * @param status The status
	 */
	public void changeInviteStatusByIDs(int activityID, String username, Participant.Status status) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE participant SET status = ? " +
					"WHERE activityID = " + activityID + " " +
					"AND username = '" + username + "'");
			ps.setInt(1,status.ordinal());
			ps.executeUpdate();
			ps.close();
			
			// Update cache
			Meeting m = dbr.getMeeting(activityID);
			for (Participant p : m.getParticipants()) {
				if(p.getEmployee().getUsername() == username) {
					p.setStatus(status);
					System.out.println("#DB: Changed "+p.getEmployee().getUsername()+" participant status. In DB and cache");
				}
			}
			
			// Create messages
			if(status == Participant.Status.NOT_ATTENDING) {
				ps = conn.prepareStatement("INSERT INTO message " +
				"(isinvite,isread,time,message,username,activityID) VALUES (?,?,?,?,?,?)");
				Employee decliner = dbr.getEmployee(username);
				for (Participant p : m.getParticipants()) {
					ps.setBoolean(1,false); // isInvite = false
					ps.setBoolean(2,false); // isRead = false
					ps.setTimestamp(3,new Timestamp(new Date().getTime()));
					ps.setString(4,decliner.getName()+" har avslått " +
							"invitasjon til møtet den "+m.getStartTime()+".");
					ps.setString(5, p.getEmployee().getUsername());
					System.out.println(p.getEmployee().getUsername());
					ps.setInt(6, activityID);
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
	 * <p>Marks a meeting, by a {@link Meeting} object,
	 * in the database as canceled (meetings should not be deleted).</p>
	 * <p>Also creates a message for each participant of the meeting</p>
	 * @param meeting The meeting to be canceled
	 */
	public void cancelMeeting(Meeting meeting) {
		cancelMeetingByID(meeting.getId());
	}
	
	/**
	 * <p>Marks a meeting, by the meeting's ID,
	 * in the database as canceled (meetings should not be deleted).</p>
	 * <p>Also creates a message for each participant of the meeting</p>
	 * @param meetingID The meeting's ID
	 */
	public void cancelMeetingByID(int meetingID) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE activity SET canceled = ? " +
					"WHERE activityID = " + meetingID);
			ps.setBoolean(1,true);
			ps.executeUpdate();
			
			// Create messages
			ps = conn.prepareStatement("INSERT INTO message " +
			"(isinvite,isread,time,message,username,activityID) VALUES (?,?,?,?,?,?)");
			Meeting m = dbr.getMeeting(meetingID);
			for (Participant p : m.getParticipants()) {
				String username = p.getEmployee().getUsername();
				ps.setBoolean(1,false); // isInvite = false
				ps.setBoolean(2,false); // isRead = false
				ps.setTimestamp(3,new Timestamp(new Date().getTime()));
				ps.setString(4,"Møte den "+m.getStartTime()+" er avlyst av "+m.getOwner().getName()+".");
				ps.setString(5, username);
				ps.setInt(6, meetingID);
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
	 * <p>Change a specified meeting.</p>
	 * <p>Also creates a message for each participant of the meeting</p>
	 * @param meeting The meeting
	 */
	public void changeMeeting(Meeting meeting) {
		try {
			// Get pre-updated meeting from cache/db
			Meeting m = dbr.getMeeting(meeting.getId());
			
			PreparedStatement ps = conn.prepareStatement("UPDATE activity " +
					"SET starttime = ?, endtime = ?, title = ?, description = ?, " +
					"roomID = ? WHERE activityID = " + meeting.getId());
			ps.setTimestamp(1, new Timestamp(meeting.getStartTime().getMillis()));
			ps.setTimestamp(2, new Timestamp(meeting.getEndTime().getMillis()));
			ps.setString(3, meeting.getTitle());
			ps.setString(4, meeting.getDescription());
			ps.setInt(5, meeting.getRoom().getRoomID());
			ps.executeUpdate();
			ps.close();
			
			updatePartcipantsForMeeting(meeting.getId(), meeting.getParticipants());
			
			// Create messages
			ps = conn.prepareStatement("INSERT INTO message " +
			"(isinvite,isread,time,message,username,activityID) VALUES (?,?,?,?,?,?)");
			for (Participant p : m.getParticipants()) {
				String username = p.getEmployee().getUsername();
				ps.setBoolean(1,true); // isInvite = true
				ps.setBoolean(2,false); // isRead = false
				ps.setTimestamp(3,new Timestamp(new Date().getTime()));
				ps.setString(4,"Møte den "+m.getStartTime()+" er endret av "+m.getOwner().getName()+".");
				ps.setString(5, username);
				ps.setInt(6, m.getId());
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			
			// Update in cache
			m.setStartTime(meeting.getStartTime());
			m.setEndTime(meeting.getEndTime());
			m.setDescription(meeting.getDescription());
			m.setTitle(meeting.getTitle());
			m.setRoom(meeting.getRoom());
			System.out.println("#DB: Updating meeting "+m.getId()+" in DB and cache");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>Marks an activity, by a {@link Activity} object,
	 * in the database as canceled (activities should not be deleted).</p>
	 * <p>Also creates a message for each participant of the meeting</p>
	 * @param act The activity to be canceled
	 */
	public void cancelActivity(Activity act) {
		cancelActivityByID(act.getId());
	}
	
	/**
	 * <p>Marks an activity, by the ID,
	 * in the database as canceled (activities should not be deleted).</p>
	 * <p>Also creates a message for each participant of the meeting</p>
	 * @param actID The ID
	 */
	public void cancelActivityByID(int actID) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE activity SET canceled = ? " +
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
	 * <p>Change a specified activity.</p>
	 * @param act The activity.
	 */
	public void changeActivity(Activity act) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE activity " +
					"SET starttime = ?, endtime = ?, title = ?, description = ?" +
					"WHERE activityID = " + act.getId());
			ps.setTimestamp(1, new Timestamp(act.getStartTime().getMillis()));
			ps.setTimestamp(2, new Timestamp(act.getEndTime().getMillis()));
			ps.setString(3, act.getTitle());
			ps.setString(4, act.getDescription());
			ps.executeUpdate();
			ps.close();
			
			// Update in cache
			Activity a = actCache.get(act.getId());
			a.setStartTime(act.getStartTime());
			a.setEndTime(act.getEndTime());
			a.setDescription(act.getDescription());
			a.setTitle(act.getTitle());
			System.out.println("#DB: Updated activity "+a.getId()+" in DB and cache");
		} catch (SQLException e) {
			System.err.println("Could not change activity.");
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>Marks a message, by a {@link Message} object,
	 * in the database as read.</p>
	 * @param message The message
	 */
	public void markMessageAsRead(Message message) {
		markMessageAsReadByIDs(message.getMessageID());
	}
	
	/**
	 * <p>Marks a message, by the message's ID,
	 * in the database as read.</p>
	 * @param messageID The message's ID
	 */
	public void markMessageAsReadByIDs(int messageID) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE message SET isread = ? " +
					"WHERE messageID = " + messageID);
			ps.setBoolean(1,true);
			ps.executeUpdate();
			ps.close();
			System.out.println("#DB: Marked message as read in DB");
		} catch (SQLException e) {
			System.err.println("Could not mark message as read.");
			e.printStackTrace();
		}
	}
	
	/**
	 * </p>Sets the database retrieval object for this store object.</p>
	 * @param dbr
	 */
	public void setDBR(DBRetrieve dbr) {
		this.dbr = dbr;
	}
	
	/**
	 * <p>Cancels all activities and meeting for a specific employee</p>
	 * <p>Strictly for testing. Restart program after.</p>
	 * @param emp
	 */
	public void cancelEmpActivities(Employee emp) {
		cancelEmpActivitiesByUsername(emp.getUsername());
	}
	
	/**
	 * <p>Cancels all activities and meeting for a specific employee, by username</p>
	 * <p>Strictly for testing. Restart program after.</p>
	 * @param username
	 */
	public void cancelEmpActivitiesByUsername(String username) {
		try {
			Statement s = conn.createStatement();
			
			s.execute("UPDATE activity SET canceled = true WHERE username ='" + username + "'");
			s.close();
			
			System.out.println("#DB: Canceling " + username + "'s activities/meetings in DB");
			System.out.println(" - for testing only, please restart to clear cache");
		} catch (SQLException e) {
			System.err.println("Could not cancel activities.");
			e.printStackTrace();
		}
	}
}
