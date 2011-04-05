package fp.server;
import java.awt.Dimension;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import fp.common.network.ServerConnection;

public class Server {
	static ArrayList<ServerConnection> clients;
	static int port = 6789;
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		clients = new ArrayList<ServerConnection>();
		
		System.out.println("Starting calendar server on port "+port);
		ServerSocket srvr = new ServerSocket(port);
		
		
		JFrame frame = new JFrame("Calendar SERVER");
		JPanel panel = new JPanel();

		DefaultListModel listModel = new DefaultListModel();
		
		JList list = new JList(listModel);
		panel.add(new JLabel("Tilkoblede klienter: "));
		panel.add(list);
		frame.setPreferredSize(new Dimension(600,400));
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
		while(true) {
			// Listen for a TCP connection request.
			Socket socket = srvr.accept();
			// Construct an object to process the request
			ServerConnection client = new ServerConnection(socket);
			listModel.addElement(client);
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