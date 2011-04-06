package fp.common.network;

import java.net.*;

import fp.common.storage.DBRetrieve;
import fp.common.storage.DBStore;


public class ServerConnection extends Connection implements Runnable {

	// Constructor
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
				DBRetrieve dbs = DBRetrieve.getInstance();
				System.out.println((String) request.get("username"));
				System.out.println((String) request.get("password"));
				System.out.println(dbs.login((String) request.get("username"),(String) request.get("password")));
				response.put("employee", dbs.login((String) request.get("username"),(String) request.get("password")));
			}
		}
		return response;
	}
	
	public String toString() {
		return socket.getRemoteSocketAddress().toString();
	}
}