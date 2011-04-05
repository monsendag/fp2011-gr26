package fp.common.network;

import java.net.* ;

import fp.common.models.Employee;
import fp.common.models.XmlSerializer;


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
		String line, xml = "";
		while((line = in.readLine()) != null) {
			if(line.equals("EN")) break;
			xml += line;
		}
		
		Employee arne = (Employee) XmlSerializer.getInstance().unSerialize(xml);
		
		System.out.println("Got employee: "+arne.getName());
		
		out.write("Hello. This is server!"+EOL);
		out.write("goodnight"+EOL);
		out.flush();
	}
	
	public String toString() {
		return socket.getRemoteSocketAddress().toString();
	}
}