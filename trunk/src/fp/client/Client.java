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
	
	
	// usikker om det her fungerer, er for � h�ndtere beskjedene som skal requestes regelmessig i clientconnection -halvor
	public void deliverMessages(ArrayList<Message> messages){		
		if (this.messages == messages)
			return;
		else{// TODO: Si fra til GUI om at det har kommet nye meldinger?
			this.messages = messages;
			gui.receiveMessages();
		}
	}
	/*		TODO: FIX clientconnection, serverconnection og DBStore - > messages burde bli markert as read med message og message ID som parameter, ikke meeting.
	public void markMessageAsRead(Message m) throws IOException {
		try {
			// feil i db? loolol
			connection.markMessageAsRead(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			throw new IOException();
		}
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
	
	/* TODO: lag en metode for � fjerne activity i calendarmodel.
	public void remActivity(Activity a) throws IOException {
		try {
			connection.cancelActivity(a);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException();
		}
		calendarModel.remActivity(a);
	}
	*/
	
	
	/* TODO: Lag en metode for � endre activity i calendarmodel.
	public void chngActivity(Activity a) throws IOException {
		try {
			connection.changeActivity(a);
		} catch (IOException e) {
			e.printStacTrace();
			throw new IOException();
		}
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
		try {
			connection.changeInviteStatus(m, participant...)	
			markMessageAsRead(msg);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new IOException;
		}
		
	}
	*/

	
	/*
	
	// true = ja, false = nei.
	public Result answerInvitation(Message m, boolean answer){ // trengs det noe messageID? isf. legg til
		// send shit til server, vent p� godkjennelse 
		// hvis godkjent:
			m.setRead(true);
			return Result.SUCCESS;
		// hvis fail:
			//return Result.TIMEOUT;
	}
	
	public List<Participant> getParticipants(Meeting m){
		// trenger ikke kontakte server vel?
		return m.getParticipants();
	}
  **/	
}