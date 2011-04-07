package fp.common.network;

import java.net.*;

import org.joda.time.DateTime;

import fp.common.models.*;
import fp.common.storage.DBRetrieve;
import fp.common.storage.DBStore;


public class ServerConnection extends Connection implements Runnable {

	public ServerConnection(Socket socket) throws Exception {
		super(socket);
	}

	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.err.println("Could not process request ("+e.getMessage()+")");
		}
	}

	private void processRequest() throws Exception {
		NetworkObject request;
		while((request = receive()) != null) {
			send(getResponse(request));
		}
		close();
	}
	
	/**
	 * Returns the appropriate response for the given request
	 * @param request - The request object
	 * @return
	 */
	private NetworkObject getResponse(NetworkObject request) {
		NetworkObject response = new NetworkObject();
		DBRetrieve dbr = DBRetrieve.getInstance();
		DBStore dbs = DBStore.getInstance();
		switch(request.getCommand()) {
			case getEmployees: {
				response.setCommand(NetworkCommand.returnEmployees);
				response.put("employees", dbr.getAllEmployees());
			} break;
			case getCredentials: {
				response.setCommand(NetworkCommand.returnCredentials);
				response.put("employee", dbr.login((String) request.get("username"),(String) request.get("password")));
			} break;
			case getActivities: {
				response.setCommand(NetworkCommand.returnActivities);
				Employee user = (Employee) request.get("currentUser");
				response.put("activities", dbr.getEmpActivities(user));
			} break;
			case getMessages: {
				response.setCommand(NetworkCommand.returnMessages);
				Employee user = (Employee) request.get("currentUser");
				response.put("messages", dbr.getEmpMessages(user));
			}
			case getMeetings: {
				response.setCommand(NetworkCommand.returnMeetings);
				Employee user = (Employee) request.get("currentUser");
				response.put("meetings", dbr.getEmpMeetings(user));
			}
			case getAllActivities:{
				response.setCommand(NetworkCommand.returnAllactivities);
				response.put("allActivities", dbr.getAllActivities());
			}
			case getAllAlerts: {
				response.setCommand(NetworkCommand.returnAllAlerts);
				response.put("allAlerts", dbr.getAllMessages());
			}
			case getAllRooms:{
				response.setCommand(NetworkCommand.returnAllRooms);
				response.put("allRooms", dbr.getAllRooms());
			}
			case getAllMeetings:{
				response.setCommand(NetworkCommand.returnAllMeetings);
				response.put("allMeetings", dbr.getAllMeetings());
			}
			case getAvailableRooms: {
				response.setCommand(NetworkCommand.returnAvailableRooms);
				DateTime startTime = (DateTime) request.get("startTime");
				DateTime endTime = (DateTime) request.get("endTime");
				response.put("availableRooms", dbr.getAvailableRooms(startTime, endTime));
			}
			case getMeeting: {
				response.setCommand(NetworkCommand.returnMeeting);
				int meetingID = (Integer)request.get("meetingID");
				response.put("meeting", dbr.getMeeting(meetingID));
			}
			case getRoom: {
				response.setCommand(NetworkCommand.returnRoom);
				int roomID = (Integer)request.get("roomID");
				response.put("room", dbr.getMeeting(roomID));
			}
			case addActivity: {
				response.setCommand(NetworkCommand.returnActivityID);
				Activity activity = (Activity)request.get("activity");
				response.put("activityID", dbs.addActivity(activity));
			}
			case addMeeting: {
				response.setCommand(NetworkCommand.returnMeetingID);
				Meeting meeting = (Meeting)request.get("meeting");
				response.put("meetingID", dbs.addMeeting(meeting));
			}
			case cancelActivity: {
				Activity activity = (Activity)request.get("activity");
				dbs.cancelActivity(activity);
			}
			case cancelMeeting: {
				Meeting meeting = (Meeting)request.get("meeting");
				dbs.cancelMeeting(meeting);
			}
			case changeActivity: {
				Activity activity = (Activity)request.get("activity");
				dbs.changeActivity(activity);
			}
			case changeMeeting: {
				Meeting meeting = (Meeting)request.get("meeting");
				dbs.changeMeeting(meeting);
			}
			case changeInviteStatus: {
				Meeting meeting = (Meeting)request.get("meeting");
				Participant participant = (Participant)request.get("participant");
				dbs.changeInviteStatus(meeting, participant);
			}
			case markMessageAsRead: {
				Employee user = (Employee) request.get("currentUser");
				Meeting meeting = (Meeting)request.get("meeting");
				dbs.markMessageAsRead(meeting, user);
			}
		}
		return response;
	}
	
	public String toString() {
		return socket.getRemoteSocketAddress().toString();
	}
}