package fp.common.network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import fp.client.Client;
import fp.common.models.Activity;
import fp.common.models.Employee;
import fp.common.models.Message;

public class ClientConnection extends Connection implements Runnable {
	
	public ClientConnection(InetAddress host) throws IOException {
		super(host);
	}
	
	public ClientConnection() throws IOException {
		this(InetAddress.getLocalHost());
	}
	
	@Override
	public void run() { 
		
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
		return back.get("employee") != null ? ((Employee) back.get("employee")) : null;
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
		n.setCommand(NetworkCommand.getActivities);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = receive();
		return (ArrayList<Message>) back.get("messages");
	}
	
}
	