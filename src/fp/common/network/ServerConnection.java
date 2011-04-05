package fp.common.network;

import java.net.*;

import fp.common.models.Employee;


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
		do {
			request = retrieve();
			send(getResponse(request));
		}
		while(request != null);
		close();
	}
	
	private NetworkObject getResponse(NetworkObject request) {
		NetworkObject response = new NetworkObject();
		switch(request.getCommand()) {
			case getEmployees: {
				response.setCommand(NetworkCommand.returnEmployees);
			} break;
			
		}
		return response;
	}
	
	public String toString() {
		return socket.getRemoteSocketAddress().toString();
	}
}