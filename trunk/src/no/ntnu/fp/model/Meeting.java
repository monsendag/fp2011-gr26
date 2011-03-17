

package no.ntnu.fp.model;

import java.util.ArrayList;
import java.util.List;

//
public class Meeting extends Activity {
	
	
	private Employee admin;
	private ArrayList<Employee> participants;
	private Room room;
	
	public Meeting()
	{
		super();
	}
	
	public Meeting(Employee admin, ArrayList<Employee> participants, Room room, Date startTime, Date endTime, String description, String location)
	{
		super(startTime, endTime, description, location);
		this.admin = admin;
		this.room = room;
		this.participants = participants;
	}
	
	public void setAdmin(Employee admin)
	{
		this.admin = admin;
	}
	public Employee getAdmin()
	{
		return admin;
	}
	
	public void setParticipants(ArrayList<Employee> participants)
	{
		this.participants = participants;
	}
	public ArrayList<Employee> getParticipants()
	{
		return participants;
	}
	
	public Room getRoom()
	{
		return room;
	}
	
	
	
	
	public void removeParticipant(Employee participant)
	{
		if (participants.contains(participant))
			participants.remove(participant);
		else
			System.out.println("ERROR ERROR ERRORR ALERT ALERT");
	}
	
	public void addParticipant(Employee participant)
	{
		if (!(participants.contains(participant)))
			participants.add(participant);
		else
			System.out.println("EREROROREOROEROO ER ALERT RED ALERT");
	}
	
	public void cancelMeeting()
	{
		// destroy!!
	}
	public void changeRooms(Room room)
	{
		if (participants.size() <= room.getCapacity())
			this.room = room;
		else
			System.out.println("ERROR ERROR ERROR");
	}
	
	

}