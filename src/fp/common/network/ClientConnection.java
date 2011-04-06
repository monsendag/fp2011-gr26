package fp.common.network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import fp.client.Client;
import fp.common.models.Activity;
import fp.common.models.Employee;

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
	
	public Employee login(String username, String password) throws IOException {
		NetworkObject n = new NetworkObject();
		
		n.setCommand(NetworkCommand.getCredentials);

		n.put("username", username);
		n.put("password", password);
		send(n);
		NetworkObject back = retrieve();
		return back.get("employee") != null ? ((Employee) back.get("employee")) : null;
	}
	
	public ArrayList<Activity> getEmpActivities() throws IOException {
		NetworkObject n = new NetworkObject();
		n.setCommand(NetworkCommand.getActivities);
		n.put("currentUser", Client.get().getUser());
		send(n);
		NetworkObject back = retrieve();
		return (ArrayList<Activity>) back.get("activities");
	}
}
	