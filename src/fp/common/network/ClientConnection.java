package fp.common.network;

import java.io.IOException;
import java.net.InetAddress;

import fp.common.models.Employee;

public class ClientConnection extends Connection {
	
	  public static void main(String args[]) {
	        java.awt.EventQueue.invokeLater(new Runnable() {
	            public void run() {
	                try {
						new ClientConnection();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
	        });
	    }
	public ClientConnection() throws IOException {
		super(InetAddress.getLocalHost());
		
		NetworkObject n = new NetworkObject();
		
		
		n.setCommand(NetworkCommand.getEmployees);
		
		send(n);
		NetworkObject back = retrieve();
		
		for(Object o : back.getObjects()) {
			System.out.println(((Employee) o).getName());
		}
		
		// Close streams and socket.
		out.close();
		in.close();
	}
}
