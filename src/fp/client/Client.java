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
import fp.common.models.Participant;
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
	private List<Activity> activities;
	private List<Message> messages;
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
	
	enum Result{
		SUCCESS, TIMEOUT, WRONGPWD; 
	}
	
	public boolean loginAction(String ip, String username, String password) {
		try {
			connection = new ClientConnection(InetAddress.getByName(ip), this);
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
	
	
	// usikker om det her fungerer, er for å håndtere beskjedene som skal requestes regelmessig i clientconnection -halvor
	public void deliverMessages(ArrayList<Message> messages){		
		if (this.messages == messages)
			return;
		else// TODO: Si fra til GUI om at det har kommet nye meldinger?
			this.messages = messages;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	/*
	public List<Room> getRooms(DateTime start, DateTime end){
		// hent rom fra server ledige i gitt tidsrom, return liste med rom.
		
		// problemer, hvis ingen rom er ledige, hvordan skal dette håndteres i guien? være en mulighet for å se alle reservasjoner for en dag?
		return null;
	}
	
	
	public Result remActivity(int activityID){
		// send shit til server, vent på godkjennelse
		// hvis OK:
			activities.remove(activityID);
			return Result.SUCCESS;
		// else:
			//return Result.TIMEOUT;
	}
	public Result addActivity(Activity a){
		// send inn shit til server, vent på godkjennelse
		// hvis godkjent:
			activities.add(a);
			return Result.SUCCESS;
		// else:
			//return Result.TIMEOUT;
	}
	
	public Result chngActivity(Activity a, int activityID){
		// send shit til server, vent på godkjennelse, fjern activity med ID og legg til ny activity
		// hvis godkjent:
			for (int i = 0; i < activities.size(); i++){
				if (activities.get(i).getId() == activityID)
					activities.remove(i);
			}
			activities.add(a);
			return Result.SUCCESS;
		// else
			// return Result.TIMEOUT;
	}
	
	
	// true = ja, false = nei.
	public Result answerInvitation(Message m, boolean answer){ // trengs det noe messageID? isf. legg til
		// send shit til server, vent på godkjennelse 
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