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
		NetworkObject request = retrieve();
		
		NetworkObject response = new NetworkObject();
		
		if(request.getCommand() == NetworkCommand.getEmployees) {
			response.setCommand(NetworkCommand.returnEmployees);
			Employee arne = new Employee("Arne bjarne", "arne@bjarne.no", "shubidubidu");
			response.addObject(arne);
			send(response);
		}
	}
	
	public String toString() {
		return socket.getRemoteSocketAddress().toString();
	}
}