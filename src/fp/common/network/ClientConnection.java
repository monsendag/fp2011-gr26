package fp.common.network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

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
		System.out.println("returned value in map:" + back.get("employee"));
		return back.get("employee") != null ? ((Employee) back.get("employee")) : null;
	}
	
}
	