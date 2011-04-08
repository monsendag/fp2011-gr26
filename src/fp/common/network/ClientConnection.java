package fp.common.network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;

import fp.client.Client;
import fp.common.models.Activity;
import fp.common.models.Employee;
import fp.common.models.Meeting;
import fp.common.models.Message;
import fp.common.models.Participant;
import fp.common.models.Room;
import fp.common.storage.DBStore;



public class ClientConnection extends Connection implements Runnable {
	
	
	
	private TimerTask timer;
	
	public ClientConnection(InetAddress host) throws IOException {
		super(host);
	}
	
	public ClientConnection() throws IOException {
		this(InetAddress.getLocalHost());
	}
	
	@Override
	public void run() { 
		
	}
	
	// utestet
	private void startTimer(){
		Timer timer = new Timer();
		int delay = 2000; // begynner etter 30000ms = 30sec.
		int period = 10000; // periode på 60000ms = 60sec.
		
		this.timer = new TimerTask() {
			@Override
			public void run() {
				// hent beskjeder, trenger en metode i modellen for å faktisk legge dem til. ha en referanse til client i denne klassen? må vel ha det.
				Client.get().deliverMessages(getEmpMessages());
			}
		};
		timer.scheduleAtFixedRate(this.timer, delay, period);
	}
	
	public void stopTimer() {
		timer.cancel();
	}
	
	/**
	 * Tries to login with the given credentials
	 * @param username - The username to search for
	 * @param password - The password to match
	 * @return An employee object if success, null if not

	 */
	public Employee login(String username, String password) throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getCredentials);
		n.put("username", username);
		n.put("password", password);
		send(n);
		NetworkObject back = receive();
		
		// start timer for å spørre etter meldinger regelmessig
		if (back.get("employee") != null){
			startTimer();
			return ((Employee) back.get("employee"));
		}
		else
			return null;
		//return back.get("employee") != null ? ((Employee) back.get("employee")) : null;
	}
	/**
	 * 
	 * @return a list of all employees
	 */
	public ArrayList<Employee> getAllEmployees() {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getEmployees);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Employee>) back.get("employees");
	}
	/**
	 * @return a list of activities for currentUser (stored in Client)
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Activity> getEmpActivities() {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getActivities);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Activity>) back.get("activities");
	}
	/**
	 * 
	 * @return a list of messages for currentUser
	 */
	public ArrayList<Message> getEmpMessages() {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getMessages);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Message>) back.get("messages");
	}
	/**
	 * 
	 * @return a list of meetings for currentUser
	 */
	public ArrayList<Meeting> getEmpMeetings() {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getMeetings);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Meeting>) back.get("meetings");
	}
	/**
	 * 
	 * @return a list of all activities
	 */
	public ArrayList<Activity> getAllActivities() {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getAllActivities);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Activity>) back.get("allActivities");
	}
	/**
	 * 
	 * @return a list of all messages
	 */
	public ArrayList<Message> getAllMessages() {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getAllMessages);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Message>) back.get("allMessages");
	}
	/**
	 * 
	 * @return a list of all rooms

	 */
	public ArrayList<Room> getAllRooms() {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getAllRooms);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Room>) back.get("allRooms");
	}
	/**
	 * 	
	 * @return a list of all meetings

	 */
	public ArrayList<Meeting> getAllMeetings() {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getAllMeetings);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Meeting>) back.get("allMeetings");
	}
	/**
	 * 
	 * @param startTime
	 * @param endTime
	 * @return a list of all available rooms in the given time interval

	 */
	public ArrayList<Room> getAvailableRooms(DateTime startTime, DateTime endTime) {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getAvailableRooms);
		n.put("startTime", startTime);
		n.put("endTime", endTime);
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Room>) back.get("availableRooms");
	}
	/**
	 * 
	 * @param meetingID
	 * @return a meeting

	 */
	public Meeting getMeeting(int meetingID) {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getMeeting);
		n.put("meetingID", meetingID);
		send(n);
		NetworkObject back = receive();
		return (Meeting) back.get("meeting");
	}
	/**
	 * 
	 * @param roomID
	 * @return a room

	 */
	public Room getRoom(int roomID) {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getRoom);
		n.put("roomID", roomID);
		send(n);
		NetworkObject back = receive();
		return (Room) back.get("room");
	}
	
	public void addActivity(Activity activity){
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.addActivity);
		n.put("activity", activity);
		send(n);
		NetworkObject back = receive();
		activity.setId((Integer) back.get("activityID"));
	}
	public void addMeeting(Meeting meeting){
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.addMeeting);
		n.put("meeting", meeting);
		send(n);
		NetworkObject back = receive();
		meeting.setId((Integer) back.get("meetingId"));
	}
	public void cancelActivity(Activity activity) {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.cancelActivity);
		n.put("activity", activity);
		send(n);
	}
	public void cancelMeeting(Meeting meeting) {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.cancelMeeting);
		n.put("meeting", meeting);
		send(n);
	}
	public void changeActivity(Activity activity) {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.changeActivity);
		n.put("activity", activity);
		send(n);
	}
	public void changeMeeting(Meeting meeting) {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.changeMeeting);
		n.put("meeting", meeting);
		send(n);
	}
	public void changeInviteStatus(Meeting meeting, Participant.Status status) {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.changeInviteStatus);
		n.put("meeting", meeting);
		n.put("currentUser", Client.get().getUser());
		n.put("status", status);
		send(n);
	}
	public void markMessageAsRead(Message message) {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.markMessageAsRead);
		n.put("message", message);
		send(n);
	}

}
	