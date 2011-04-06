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
		while((request = retrieve()) != null) {
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
		}
		return response;
	}
	
	public String toString() {
		return socket.getRemoteSocketAddress().toString();
	}
}