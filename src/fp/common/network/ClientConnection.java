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
	
	
	
	private Timer timer;
	
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
		timer = new Timer();
		int delay = 30000; // begynner etter 30000ms = 30sec.
		int period = 60000; // periode på 60000ms = 60sec.
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					// hent beskjeder, trenger en metode i modellen for å faktisk legge dem til. ha en referanse til client i denne klassen? må vel ha det.
					Client.get().deliverMessages(getEmpMessages());
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, delay, period);
		}
	
	/**
	 * Tries to login with the given credentials
	 * @param username - The username to search for
	 * @param password - The password to match
	 * @return An employee object if success, null if not
	 * @throws IOException
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
	public ArrayList<Employee> getAllEmployees() throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getEmployees);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Employee>) back.get("employees");
	}
	/**
	 * @return a list of activities for currentUser (stored in Client)
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Activity> getEmpActivities() throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getActivities);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Activity>) back.get("activities");
	}
	
	public ArrayList<Message> getEmpMessages() throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getMessages);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Message>) back.get("messages");
	}
	public ArrayList<Meeting> getEmpMeetings() throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getMeetings);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Meeting>) back.get("meetings");
	}
	public ArrayList<Activity> getAllActivities() throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getAllActivities);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Activity>) back.get("allActivities");
	}
	public ArrayList<Message> getAllAlerts() throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getAllAlerts);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Message>) back.get("allAlerts");
	}
	public ArrayList<Room> getAllRooms() throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getAllRooms);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Room>) back.get("allRooms");
	}
	public ArrayList<Meeting> getAllMeetings() throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getAllAlerts);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Meeting>) back.get("allMeetings");
	}
	public ArrayList<Room> getAvailableRooms(DateTime startTime, DateTime endTime) throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getAvailableRooms);
		n.put("startTime", startTime);
		n.put("endTime", endTime);
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Room>) back.get("availableRooms");
	}
	public Meeting getMeeting(int meetingID) throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getMeeting);
		n.put("meetingID", meetingID);
		send(n);
		NetworkObject back = receive();
		return (Meeting) back.get("meeting");
	}
	public Room getRoom(int roomID) throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getRoom);
		n.put("roomID", roomID);
		send(n);
		NetworkObject back = receive();
		return (Room) back.get("room");
	}
	public void addActivity(Activity activity) throws IOException{
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.addActivity);
		n.put("activity", activity);
		send(n);
		NetworkObject back = receive();
		activity.setId((Integer) back.get("activityId"));
	}
	public void addMeeting(Meeting meeting) throws IOException{
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.addMeeting);
		n.put("meeting", meeting);
		send(n);
		NetworkObject back = receive();
		meeting.setId((Integer) back.get("meetingId"));
	}
	public void cancelActivity(Activity activity) throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.cancelActivity);
		n.put("activity", activity);
		send(n);
	}
	public void cancelMeeting(Meeting meeting) throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.cancelMeeting);
		n.put("meeting", meeting);
		send(n);
	}
	public void changeActivity(Activity activity) throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.changeActivity);
		n.put("activity", activity);
		send(n);
	}
	public void changeMeeting(Meeting meeting) throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.changeMeeting);
		n.put("meeting", meeting);
		send(n);
	}
	public void changeInviteStatus(Meeting meeting, Participant participant) throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.changeInviteStatus);
		n.put("meeting", meeting);
		n.put("participant", participant);
		send(n);
	}
	public void markMessageAsRead(Meeting meeting) throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.markMessageAsRead);
		n.put("meeting", meeting);
		send(n);
	}

}
	