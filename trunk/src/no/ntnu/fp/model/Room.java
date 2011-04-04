package no.ntnu.fp.model;

public class Room
{
	
	private int roomID;
    private String name;
    private int capacity;
    
    public Room(int roomID, String name, int capacity) {
    	this.roomID = roomID;
    	this.name = name;
    	this.capacity = capacity;
    }
    
    public Room() {
    	
    }
    
    public void reserve () 
    {

    }

    public void deleteReservation () 
    {

    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
    public int getCapacity()
    {
    	return capacity;
    }

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}

	public int getRoomID() {
		return roomID;
	}
}