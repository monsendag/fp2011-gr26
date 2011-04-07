package fp.client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import fp.client.gui.Gui;
import fp.client.gui.calendar.CalendarModel;
import fp.common.models.Activity;
import fp.common.models.Employee;
import fp.common.models.Meeting;
import fp.common.models.Message;
import fp.common.models.Room;
import fp.common.network.ClientConnection;


public class Client {
	private static Client client;
	
	/**
	 * CLIENT main entry point
	 * 
     * @param args the command line arguments
    */
    public static void main(String args[]) {
		client = new Client();
    }
    
    public static Client get() {
    	return client;
    }
    
    public Gui gui;
	public ClientConnection connection;
	public CalendarModel calendarModel;
	
	public ArrayList<Message> messages;
	public Employee currentUser;
	
	
	public Client() {     
		java.awt.EventQueue.invokeLater(new Runnable() {
	        public void run() {
	        	calendarModel = new CalendarModel();
	        	gui = new Gui();
	        }
		});
    }
	
	public Employee getUser() {
		return currentUser;
	}
	
	
	public boolean loginAction(String ip, String username, String password) {
		try {
			connection = new ClientConnection(InetAddress.getByName(ip));
			Thread thread = new Thread(connection);

			if((currentUser = connection.login(username, password)) != null) { // login successful
				calendarModel.addActivities(connection.getEmpActivities());
				return true;
			}
		} catch (IOException e) { // could not connect to server
			return false;
		}
		return false;
	}
	
	public void logoutAction() {
		try {
			connection.close();
		} catch (IOException e) {

		}
		
		currentUser = null;
		calendarModel = new CalendarModel();
	}
	
	
	public void deliverMessages(ArrayList<Message> messages){		
		if (this.messages == messages)
			return;
		else{
			this.messages = messages;
			gui.receiveMessages();
		}
	}

	
	public void addActivity(Activity a) throws IOException {
		connection.addActivity(a);
		calendarModel.addActivity(a);
	}
	

	public void remActivity(Activity a) throws IOException {
	
	connection.cancelActivity(a);
	calendarModel.remActivity(a);
	}

	
	

	public void chngActivity(Activity a) throws IOException {
		connection.changeActivity(a);
		calendarModel.chngActivity(a);
	}

	
	public ArrayList<Room> findRooms(DateTime start, DateTime end) throws IOException { // ikke kapasitet?
		ArrayList<Room> rooms;
		rooms = connection.getAvailableRooms(start, end);
		return rooms;
	}

	public void setRead(Message message) {
		connection.markMessageAsRead(message);
		
		/*
		messages.remove(message);
		message.isread(true);
		messages.add(message);
		
		deliverMessages(message);
		*/
	}
	
	/* TODO: hmm...
	public answerInvitation(Meeting m, Message msg, boolean answer) throws IOException {
		connection.changeInviteStatus(m, participant...)			
	}
	*/

}