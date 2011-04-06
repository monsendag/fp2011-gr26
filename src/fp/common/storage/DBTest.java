package fp.common.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import org.joda.time.DateTime;

import fp.common.models.Activity;
import fp.common.models.Employee;
import fp.common.models.Meeting;
import fp.common.models.Message;
import fp.common.models.Participant;
import fp.common.models.Room;
import fp.common.models.XmlSerializer;


/**
 * <p>Class for testing database input and output.</p>
 * 
 * @author fp2011-gr26
 */
public class DBTest {
	
	/**
	 * SQL-scripts for creating all the tables.
	 */
	public final static String CREATE_EMPLOYEE 	= "CREATE TABLE employee (username VARCHAR(16) NOT NULL PRIMARY KEY,name VARCHAR(30),password VARCHAR(32) NOT NULL)";
	public final static String CREATE_ROOM 		= "CREATE TABLE room (roomID INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),name VARCHAR(30),capacity INT)";
	public final static String CREATE_ACTIVITY 	= "CREATE TABLE activity (activityID INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),starttime TIMESTAMP,endtime TIMESTAMP,description VARCHAR(255),cancelled BOOLEAN,username VARCHAR(16) NOT NULL,roomID INT, location VARCHAR(30), ismeeting BOOLEAN, FOREIGN KEY (username) REFERENCES employee(username),FOREIGN KEY (roomID) REFERENCES room(roomID))";
	public final static String CREATE_ALERT 		= "CREATE TABLE alert (alertID INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),isread BOOLEAN,time TIMESTAMP,message VARCHAR(255),username VARCHAR(16) NOT NULL,activityID INT NOT NULL,FOREIGN KEY (username) REFERENCES employee(username),FOREIGN KEY (activityID) REFERENCES activity(activityID))";
	public final static String CREATE_PARTICIPANT	= "CREATE TABLE participant (status INT,username VARCHAR(16) NOT NULL,activityID INT NOT NULL,PRIMARY KEY (username, activityID),FOREIGN KEY (username) REFERENCES employee(username),FOREIGN KEY (activityID) REFERENCES activity(activityID))";
	
	/**
	 * Used for testing database output.
	 */
	public static void testPrint(DBRetrieve dbr,DBStore dbs) {
		// Change a participants status to a meeting through id's and username
		//dbs.changeInviteStatusByIDs(3, "oleak", 2);
		//dbs.changeInviteStatusByIDs(3, "Johni", 2);
		
		// Cancels the meeting with id = 6
		//dbs.changeMeeting(dbr.getMeeting(9));
		
		// All employees
		System.out.println("All emlpoyees: (getAllEmployees())");
		for (Employee e : dbr.getAllEmployees()) {
			System.out.println(e.getName() + " " + e.getUsername() + " " + e);
		}
		System.out.println();
		// All rooms
		System.out.println("All rooms: (getAllRooms())");
		for (Room r : dbr.getAllRooms()) {
			System.out.println(r.getRoomID() + " " + r.getName() + " " + r.getCapacity());
		}
		System.out.println();
		// All activities
		System.out.println("All activities: (getAllActivities())");
		for (Activity a : dbr.getAllActivities()) {
			System.out.println(a.getId() + " " + a.getOwner().getName() + " " + a.getOwner());
		}
		System.out.println();
		// All meetings with every participant
		Meeting meeting = null;
		System.out.println("All meetings: (getAllMeetings())");
		for (Meeting m : dbr.getAllMeetings()) {
			meeting = m;
			System.out.println("Meeting nr. " + m.getId() + ". Owned by " + m.getOwner().getName() + ". Participants:");
			for (Participant p : m.getParticipants()) {
				System.out.println(p.getStatus() + " - " + p.getEmployee().getUsername() + "\t" + p.getEmployee());
			}
			System.out.println();
		}
		
		// Change a participants status to a meeting through a meeting and it's participant list
		// Changing big carls status in the meeting object
		meeting.getParticipants().get(0).setStatus(Participant.Status.NOT_ATTENDING);
		// bigcarl in meeting with id 6
		//dbs.changeInviteStatusByIDs(10, "superuser", Participant.Status.NOT_ATTENDING.ordinal());
		//dbs.cancelMeetingByID(6);
		
		System.out.println();
		
		// All participants for selected meeting through a meeting object
		System.out.println("All participants for meeting with ID = 6:");
		for (Participant p : dbr.getMeeting(6).getParticipants()) {
			Employee e = p.getEmployee();
			System.out.println(p.getStatus() + "\t" + e.getUsername() + " - " + e);
		}
		
		XmlSerializer.getInstance();
		String xml = XmlSerializer.getInstance().serialize(dbr.getMeeting(6));
		System.out.println(xml);
		Meeting m2 = (Meeting)XmlSerializer.getInstance().unSerialize(xml);
		
		System.out.println(m2.getParticipants().get(0).getStatus());
		
		
		System.out.println();
		
		// All meetings for a specified user (oletobs), and all participants to those meetings
		System.out.println("All meetings for a specified user (oletobs)(" + dbr.getEmpMeetingsByUsername("oletobs").size() +
				" meeting(s)), and all participants to those meetings:" +
				"(getEmpMeetingsByUsername(\"oletobs\") and meeting.getParticipants()\n");
		for (Meeting m : dbr.getEmpMeetingsByUsername("oletobs")) {
			System.out.println("Meeting with id " + m.getId() +
					". Owner " + m.getOwner().getName() + ". Participants:");
			for (Participant p : m.getParticipants()) {
				Employee e = p.getEmployee();
				System.out.println(p.getStatus() + "\t" + e.getUsername() + " - " + e);
			}
			System.out.println();
		}
	
		System.out.println();
		
		// All participants of a meeting through the meetings participants list
		System.out.println("All participants of a meeting through the meetings participants list: (getAllMeetings().get(0).getParticipants()");
		for (Participant p : dbr.getAllMeetings().get(0).getParticipants()) {
			Employee e = p.getEmployee();
			System.out.println(p.getStatus() + "\t" + e.getUsername() + " - " + e);
		}
		
		System.out.println();
		
		// All activities of a employee
		System.out.println("All activities of a selected employee: (getEmpActivitiesByUsername(\"oletobs\")");
		for (Activity a : dbr.getEmpActivitiesByUsername("oletobs")) {
			System.out.println(a.getId() + " " + a.getOwner().getName() + " " + a.getDescription());
		}
		
		System.out.println();
		
		
		//dbs.addMeeting(new Meeting(dbr.getEmployee("Emo"), dbr.getMeeting(3).getParticipants(), dbr.getRoom(2), new DateTime(2001, 1, 1, 12, 0, 0, 0), new DateTime(2001, 1, 1, 14, 0, 0, 0), "Random", "Hjemme"));
		for (Message m : dbr.getAllAlerts()) {
			System.out.println(m.getRead() + " " + m.getCreatedOn() + " " + m.getDescription()
					+ " " + m.getEmployee().getUsername() + " " + m.getMeeting().getId());
		}
		
		//dbs.addEmployee(new Employee("Test Hansen", "test", "1234"));
	}
	
	public static void main(String[] args) {
		
		
		DBStore dbs = DBStore.getInstance();
		DBRetrieve dbr = DBRetrieve.getInstance();
		
		
		
		
		// Cache
		
		testPrint(dbr,dbs);
		//dbr.test();
	}	
}
