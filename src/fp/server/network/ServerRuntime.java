package fp.server.network;

import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ServerRuntime {
	
	static ArrayList<ClientConnection> clients;
	static int port = 6789;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		clients = new ArrayList<ClientConnection>();
		
		System.out.println("Starting calendar server on port "+port);
		ServerSocket srvr = new ServerSocket(port);
		while(true) {
			// Listen for a TCP connection request.
			Socket socket = srvr.accept();
			// Construct an object to process the request
			ClientConnection client = new ClientConnection(socket);
			
			clients.add(client);
			// Create a new thread to process the request.
			Thread thread = new Thread(client);
			// Start the thread.
			thread.start();
		}
		
		
		/**
		 * 					CLIENT															SERVER
		 * 							få alle aktiviteter						send 				hent fra db
		 * 							få en aktivitet (id)						----""----		
		 * 							få alle meldinger						----""----				
		 * 							få uleste meldinger						----""----
		 * 							få alle employees
		 * 							
		 * 							opprett aktivitet
		 * 							endre aktivitet
		 * 							merk melding som lest
		 * 							
		 */
	}
}