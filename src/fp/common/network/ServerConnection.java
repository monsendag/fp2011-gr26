package fp.common.network;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

import org.joda.time.DateTime;

import fp.common.models.*;
import fp.common.storage.DBRetrieve;
import fp.common.storage.DBStore;
import fp.server.Server;


public class ServerConnection extends Connection implements Runnable {

	ArrayList<Message> sentMessages;
	Boolean first = true;
	
	
	public ServerConnection(Socket socket) throws Exception {
		super(socket);
		
		sentMessages = new ArrayList<Message>();
	}

	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.err.println("Could not process request ("+e.getMessage()+")");
			Server.get().removeClient(this);
		}
	}

	private void processRequest() {
		NetworkObject request;
		while((request = receive()) != null) {
			send(getResponse(request));
		}
		try {
			close();
		} catch (IOException e) {
			System.err.println("#NET: Failed to close connection.");
		}
	}
	
	/**
	 * Returns the appropriate response for the given request
	 * @param request - The request object
	 * @return
	 */
	private NetworkObject getResponse(NetworkObject request) {
		NetworkObject response = null;
		DBRetrieve dbr = DBRetrieve.getInstance();
		DBStore dbs = DBStore.getInstance();
		switch(request.getCommand()) {
			case getEmployees: {
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnEmployees);
				response.put("employees", dbr.getAllEmployees());
			} break;
			case getCredentials: {
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnCredentials);
				response.put("employee", dbr.login((String) request.get("username"),(String) request.get("password")));
			} break;
			case getActivities: {
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnActivities);
				Employee user = (Employee) request.get("currentUser");
				response.put("activities", dbr.getEmpActivities(user));
			} break;
			case getMessages: {
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnMessages);
				Employee user = (Employee) request.get("currentUser");		
				response.put("messages", dbr.getEmpMessages(user));
			} break;
			case getNewMessages: {
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnNewMessages);
				Employee user = (Employee) request.get("currentUser");
				

				ArrayList<Message> sendMsgs = dbr.getEmpMessages(user);
				
				
				if (sentMessages.size() != 0){	
					// skjekk 
					// fjern alle messages fra sendmsgs hvis sendmsgs[i].getMessageId() == sentMessages[j].getMessageId(I)
					for (int i = 0; i < sendMsgs.size(); i++){
						for (int j = 0; j < sentMessages.size(); j++) {
							if (sendMsgs.get(i).getMessageID() == sentMessages.get(j).getMessageID()) {
								sendMsgs.remove(i);
								i = i - 1;
								break;
							}
						}
					}
				}
				sentMessages.addAll(sendMsgs);
				if (sendMsgs.size() == 0)
					response.put("newMessages", null);
				else
					response.put("newMessages", sendMsgs);
			}break;
			case getMeetings: {
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnMeetings);
				Employee user = (Employee) request.get("currentUser");
				response.put("meetings", dbr.getEmpMeetings(user));
			} break;
			case getAllActivities:{
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnAllactivities);
				response.put("allActivities", dbr.getAllActivities());
			} break;
			case getAllMessages: {
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnAllMessages);
				response.put("allAlerts", dbr.getAllMessages());
			} break;
			case getAllRooms:{
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnAllRooms);
				response.put("allRooms", dbr.getAllRooms());
			} break;
			case getAllMeetings:{
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnAllMeetings);
				response.put("allMeetings", dbr.getAllMeetings());
			} break;
			case getAvailableRooms: {
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnAvailableRooms);
				DateTime startTime = (DateTime) request.get("startTime");
				DateTime endTime = (DateTime) request.get("endTime");
				response.put("availableRooms", dbr.getAvailableRooms(startTime, endTime));
			} break;
			case getMeeting: {
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnMeeting);
				int meetingID = (Integer)request.get("meetingID");
				response.put("meeting", dbr.getMeeting(meetingID));
			} break;
			case getRoom: {
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnRoom);
				int roomID = (Integer)request.get("roomID");
				response.put("room", dbr.getRoom(roomID));
			} break;
			case addActivity: {
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnActivityID);
				Activity activity = (Activity)request.get("activity");
				response.put("activityID", dbs.addActivity(activity));
			} break;
			case addMeeting: {
				response = new NetworkObject();
				response.setCommand(NetworkCommand.returnMeetingID);
				Meeting meeting = (Meeting)request.get("meeting");
				response.put("meetingID", dbs.addMeeting(meeting));
			} break;
			case cancelActivity: {
				Activity activity = (Activity)request.get("activity");
				dbs.cancelActivity(activity);
			} break;
			case cancelMeeting: {
				Meeting meeting = (Meeting)request.get("meeting");
				dbs.cancelMeeting(meeting);
			} break;
			case changeActivity: {
				Activity activity = (Activity)request.get("activity");
				dbs.changeActivity(activity);
			} break;
			case changeMeeting: {
				Meeting meeting = (Meeting)request.get("meeting");
				dbs.changeMeeting(meeting);
			} break;
			case changeInviteStatus: {
				Meeting meeting = (Meeting)request.get("meeting");
				Employee currentUser = (Employee)request.get("currentUser");
				Participant.Status status = (Participant.Status)request.get("status");
				dbs.changeInviteStatusByIDs(meeting.getId(), currentUser.getUsername(), status);
			} break;
			case markMessageAsRead: {
				Message message = (Message) request.get("message");
				dbs.markMessageAsRead(message);
			} break;
		}
		return response;
	}
	
	public String toString() {
		return socket.getRemoteSocketAddress().toString();
	}
}