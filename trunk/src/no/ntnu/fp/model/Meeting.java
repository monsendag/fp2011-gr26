package no.ntnu.fp.model;

import java.util.ArrayList;
import org.joda.time.DateTime;

public class Meeting extends Activity {
	
	private ArrayList<Participant> participants;
	private Room room;
	
	public Meeting()
	{
		super();
	}
	
	public Meeting(Employee owner, ArrayList<Participant> participants, Room room, DateTime startTime, DateTime endTime, String description, String location)
	{
		super(owner, startTime, endTime, description, location);
		this.room = room;
		this.participants = participants;
	}
	
	public void setParticipants(ArrayList<Participant> participants)
	{
		this.participants = participants;
	}
	
	public ArrayList<Participant> getParticipants()
	{
		return participants;
	}
	
	public Room getRoom()
	{
		return room;
	}
	
	public void setRoom(Room room)
	{
		this.room = room;
	}
	
	public void removeParticipant(Employee participant)
	{
		if (participants.contains(participant))
			participants.remove(participant);
		else
			System.out.println("ERROR ERROR ERRORR ALERT ALERT");
	}
	
	public void addParticipant(Participant participant)
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