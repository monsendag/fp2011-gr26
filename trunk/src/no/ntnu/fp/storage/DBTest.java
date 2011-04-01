package no.ntnu.fp.storage;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import no.ntnu.fp.model.Activity;
import no.ntnu.fp.model.Employee;
import no.ntnu.fp.model.Meeting;
import no.ntnu.fp.model.Message;
import no.ntnu.fp.model.Participant;
import no.ntnu.fp.model.Room;

/**
 * <p>Class for testing database input and output.</p>
 * 
 * @author fp2011-gr26
 */
public class DBTest {
	
	/**
	 * SQL-scripts for creating all the tables.
	 */
	public final String CREATE_EMPLOYEE 	= "CREATE TABLE employee (username VARCHAR(16) NOT NULL PRIMARY KEY,name VARCHAR(30),password VARCHAR(32) NOT NULL)";
	public final String CREATE_ROOM 		= "CREATE TABLE room (roomID INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),name VARCHAR(30),capacity INT)";
	public final String CREATE_ACTIVITY 	= "CREATE TABLE activity (activityID INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),starttime TIMESTAMP,endtime TIMESTAMP,description VARCHAR(255),cancelled BOOLEAN,username VARCHAR(16) NOT NULL,roomID INT, location VARCHAR(30), ismeeting BOOLEAN, FOREIGN KEY (username) REFERENCES employee(username),FOREIGN KEY (roomID) REFERENCES room(roomID))";
	public final String CREATE_PARTICIPANT 	= "CREATE TABLE alert (isread BOOLEAN,time TIMESTAMP,message VARCHAR(255),username VARCHAR(16) NOT NULL,activityID INT NOT NULL,PRIMARY KEY (username, activityID),FOREIGN KEY (username) REFERENCES employee(username),FOREIGN KEY (activityID) REFERENCES activity(activityID))";
	public final String CREATE_ALERT 		= "CREATE TABLE participant (status INT,username VARCHAR(16) NOT NULL,activityID INT NOT NULL,PRIMARY KEY (username, activityID),FOREIGN KEY (username) REFERENCES employee(username),FOREIGN KEY (activityID) REFERENCES activity(activityID))";
	
	/**
	 * Used for testing database output.
	 */
	public static void testPrint(DBRetrieve dbr,DBStore dbs) {
		// Change a participants status to a meeting through id's and username
		//dbs.changeInviteStatusByIDs(3, "oleak", 2);
		//dbs.changeInviteStatusByIDs(3, "Johni", 2);
		
		// Cancels the meeting with id = 6
		//dbs.cancelMeetingByID(6);
		
		// All employees
		System.out.println("All emlpoyees: (getAllEmployees())");
		for (Employee e : dbr.getAllEmployees()) {
			System.out.println(e.getName() + " " + e.getUsername() + " " + e.getPassword());
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
			System.out.println(a.getId() + " " + a.getOwner().getName() + " " + a.getDescription());
		}
		
		System.out.println();
		
		// All meetings
		Meeting meeting = null;
		System.out.println("All meetings: (getAllMeetings())");
		for (Meeting m : dbr.getAllMeetings()) {
			meeting = m;
			System.out.println(m.getId() + " " + m.getOwner().getName() + " " + m.getDescription());
		}
		
		// Change a participants status to a meeting through a meeting and it's participant list
		// Changing big carls status in the meeting object
		meeting.getParticipants().get(0).setStatus(Participant.Status.NOT_ATTENDING);
		// bigcarl in meeting with id 6
		dbs.changeInviteStatus(meeting, meeting.getParticipants().get(0));
		
		System.out.println();
		
		// All participants for selected meeting through ID
		System.out.println("All participants for meeting with ID = 6: (getParticipantsByMeetingID(6))");
		for (Participant p : dbr.getParticipantsByMeetingID(5)) {
			Employee e = p.getEmployee();
			System.out.println(p.getStatus() + "\t" + e.getUsername() + "\t" + e.getPassword() + " " + e.getName());
		}
		
		System.out.println();
		
		// All participants for selected meeting through a meeting object
		System.out.println("All participants for a meeting object: (getParticipants(Meeting))");
		for (Participant p : dbr.getParticipants(dbr.getAllMeetings().get(1))) {
			Employee e = p.getEmployee();
			System.out.println(p.getStatus() + "\t" + e.getUsername() + "\t" + e.getPassword() + " " + e.getName());
		}
		
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
				System.out.println(p.getStatus() + "\t" + e.getUsername() + "\t" + e.getPassword() + " " + e.getName());
			}
			System.out.println();
		}
	
		System.out.println();
		
		// All participants of a meeting through the meetings participants list
		System.out.println("All participants of a meeting through the meetings participants list: (getAllMeetings().get(0).getParticipants()");
		for (Participant p : dbr.getAllMeetings().get(0).getParticipants()) {
			Employee e = p.getEmployee();
			System.out.println(p.getStatus() + "\t" + e.getUsername() + "\t" + e.getPassword() + " " + e.getName());
		}
		
		System.out.println();
		
		// All activities of a employee
		System.out.println("All activities of a selected employee: (getEmpActivitiesByUsername(\"oletobs\")");
		for (Activity a : dbr.getEmpActivitiesByUsername("oletobs")) {
			System.out.println(a.getId() + " " + a.getOwner().getName() + " " + a.getDescription());
		}
		
		System.out.println();
	}
	
	public static void main(String[] args) {
		
		DBRetrieve dbr = new DBRetrieve();
		DBStore dbs = new DBStore();
		DBGetModels dbgm = new DBGetModels();
		
		ArrayList<Employee> allEmployees = new ArrayList<Employee>();
		ArrayList<Room>	allRooms = new ArrayList<Room>();
		ArrayList<Meeting> allMeetings = new ArrayList<Meeting>();
		ArrayList<Activity> allActivities = new ArrayList<Activity>();
		ArrayList<Message> allAlerts = new ArrayList<Message>();
		
		allEmployees = dbgm.getAllEmployees();
		allRooms = dbgm.getAllRooms();
		allActivities = dbgm.getAllActivities(allEmployees);
		allMeetings = dbgm.getAllMeetings(allEmployees);
		allAlerts = dbgm.getAllAlerts(allMeetings);
		
		for (Employee e : allEmployees) {
			//System.out.println(e.getUsername());
			System.out.println(e + " - " + e.getUsername());
		}
		System.out.println();
		for (Room r : allRooms) {
			System.out.println(r.getName());
		}
		System.out.println();
		for (Activity a : allActivities) {
			System.out.println("Activity " + a.getId() + " owned by " + a.getOwner().getUsername());
		}
		System.out.println();
		for (Meeting m : allMeetings) {
			System.out.println("Meeting " + m.getId() + " owned by " + m.getOwner().getUsername() + ". Participants:");
			for (Participant p : m.getParticipants()) {
				//System.out.println(p.getStatus() + " - " + p.getEmployee().getUsername());
				System.out.println(p.getEmployee() + " - " + p.getEmployee().getUsername());
			}
			System.out.println();
		}
		//testPrint(dbr,dbs);
	}	
}
