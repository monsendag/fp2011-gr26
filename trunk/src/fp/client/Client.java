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
	/*		TODO: FIX clientconnection, serverconnection og DBStore - > messages burde bli markert as read med message og message ID som parameter, ikke meeting.
	public void markMessageAsRead(Message m) throws IOException {
			// feil i db? loolol
		connection.markMessageAsRead(m);
		
		
		
		messages.remove(m);
		m.isRead(true);
		messages.add(m);
		
		deliverMessages(messages);
	}
	*/
	
	public void addActivity(Activity a) throws IOException {
		connection.addActivity(a);
		calendarModel.addActivity(a);
	}
	
	/* TODO: lag en metode for å fjerne activity i calendarmodel.
	public void remActivity(Activity a) throws IOException {
	
	connection.cancelActivity(a);
	calendarModel.remActivity(a);
	}
	*/
	
	
	/* TODO: Lag en metode for å endre activity i calendarmodel.
	public void chngActivity(Activity a) throws IOException {
		connection.changeActivity(a);
		
		calendarModel.chngActivity(a);
	}
	*/
	
	public ArrayList<Room> findRooms(DateTime start, DateTime end) throws IOException { // ikke kapasitet?
		ArrayList<Room> rooms;
		rooms = connection.getAvailableRooms(start, end);
		return rooms;
	}
	
	/* TODO: hmm...
	public answerInvitation(Meeting m, Message msg, boolean answer) throws IOException {
		connection.changeInviteStatus(m, participant...)			
	}
	*/

}