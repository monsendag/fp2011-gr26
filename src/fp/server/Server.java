package fp.server;
import java.awt.Dimension;
import java.io.IOException;
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
	public static int port = 6789;
	public static DefaultListModel clients;
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) {
		
		System.out.println("#NET: Starting server on port "+port);
		ServerSocket srvr;
		try {
			srvr = new ServerSocket(port);
			
			JFrame frame = new JFrame("Calendar SERVER");
			JPanel panel = new JPanel();

			clients = new DefaultListModel();
			
			JList list = new JList(clients);
			panel.add(new JLabel("Connected clients: "));
			panel.add(list);
			frame.setPreferredSize(new Dimension(600,400));
			frame.setContentPane(panel);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocationRelativeTo(null);
			frame.pack();
			frame.setVisible(true);
			while(true) {
				Socket socket;
				ServerConnection client;
					socket = srvr.accept();
					client = new ServerConnection(socket); // Construct an object to process the request
					clients.addElement(client);
					// Create a new thread to process the request.
					Thread thread = new Thread(client);
					// Start the thread.
					thread.start();
			}
		} 
		catch (IOException e) {
			System.err.println("#NET: IO-exception: "+e.getMessage());
		}
		catch (Exception e) {
			
		}
	}
}