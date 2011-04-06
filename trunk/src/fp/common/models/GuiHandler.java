package fp.common.models;

import java.util.List;

import org.joda.time.DateTime;

import fp.client.gui.Gui;

public class GuiHandler {

	private Gui gui;
	
	private List<Activity> activities;
	private List<Message> messages;
	
	String usr, pwd;
	
	public GuiHandler (Gui gui){
		this.gui = gui;
	}
	
	enum Result{
		SUCCESS, TIMEOUT, WRONGPWD; 
	}
	public Result login(String usr, String pwd){
		
		this.usr = usr;
		this.pwd = pwd;
		// send inn til server, faa svar.
		//hvis success:
			init();
				// hvis success:
					return Result.SUCCESS;
	}
	
	public void init(){
		// importer alt, lag modeller for alt!
		// dagene importeres i CalendarModel?
		// lagre activities og meetings
		// lagre messages
	}
	
	
	
	// noen metoder for å lytte til endringer fra nettverket, som igjen fyrer av endringer i guien.
	// for meldinger og møteinnkallinger.
	
	
	
	
	// håndtere hver enkelt knapp:
	public void logout(){
		// slett alle modeller.
		usr = "";
		pwd = "";
		activities = null;
		messages = null;
	}
	
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
	
	public List<Participant> getParticipants(Activity a){
		// trenger ikke kontakte server vel?
	}
	
}
