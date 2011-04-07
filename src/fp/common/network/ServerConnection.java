package fp.common.network;

import java.net.*;

import fp.common.models.Employee;
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
		switch(request.getCommand()) {
			case getEmployees: {
				response.setCommand(NetworkCommand.returnEmployees);
				DBStore.getInstance();
				DBRetrieve dbs = DBRetrieve.getInstance();
				response.put("employees", dbs.getAllEmployees());
			} break;
			case getCredentials: {
				response.setCommand(NetworkCommand.returnCredentials);
				DBStore.getInstance();
				DBRetrieve dbr = DBRetrieve.getInstance();
				response.put("employee", dbr.login((String) request.get("username"),(String) request.get("password")));
			} break;
			case getActivities: {
				response.setCommand(NetworkCommand.returnActivities);
				DBStore.getInstance();
				DBRetrieve dbr = DBRetrieve.getInstance();
				Employee user = (Employee) request.get("currentUser");
				response.put("activities", dbr.getEmpActivities(user));
			} break;
			case getMessages: {
				response.setCommand(NetworkCommand.returnMessages);
				DBStore.getInstance();
				DBRetrieve dbr = DBRetrieve.getInstance();
				Employee user = (Employee) request.get("currentUser");
				response.put("messages", dbr.getEmpAlerts(user));
			}
			case getMeetings: {
				response.setCommand(NetworkCommand.returnMeetings);
				DBStore.getInstance();
				DBRetrieve dbr = DBRetrieve.getInstance();
				Employee user = (Employee) request.get("currentUser");
				response.put("meetings", dbr.getEmpMeetings(user));
			}
			case getAllActivities:{
				response.setCommand(NetworkCommand.returnAllactivities);
				DBStore.getInstance();
				DBRetrieve dbr = DBRetrieve.getInstance();
				response.put("allActivities", dbr.getAllActivities());
			}
			case getAllAlerts: {
				response.setCommand(NetworkCommand.returnAllAlerts);
				DBStore.getInstance();
				DBRetrieve dbr = DBRetrieve.getInstance();
				response.put("allAlerts", dbr.getAllAlerts());
			}
			case getAllRooms:{
				response.setCommand(NetworkCommand.returnAllRooms);
				DBStore.getInstance();
				DBRetrieve dbr = DBRetrieve.getInstance();
				response.put("allRooms", dbr.getAllRooms());
			}
			case getAllMeetings:{
				response.setCommand(NetworkCommand.returnAllMeetings);
				DBStore.getInstance();
				DBRetrieve dbr = DBRetrieve.getInstance();
				response.put("allMeetings", dbr.getAllMeetings());
			}
			
		}
		return response;
	}
	
	public String toString() {
		return socket.getRemoteSocketAddress().toString();
	}
}