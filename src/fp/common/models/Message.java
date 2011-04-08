package fp.common.models;

import org.joda.time.DateTime;

public class Message
{
    //
    private String description;
   
    //
    private String title;
    
    private int type = 0;
    //
    private Boolean read;
    
    private Boolean invitation;
    
    private int messageID;

    //
    private DateTime createdOn;

    //
    private Meeting meeting;
    
    // Beskjeden meldingen skal til(mulig denne er unødvendig, klarer ikke tenke logisk - tobias)
    private Employee employee;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean isRead() {
		return read;
	}

	public void isRead(Boolean read) {
		this.read = read;
	}

	public DateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(DateTime createdOn) {
		this.createdOn = createdOn;
	}

	public Meeting getMeeting() {
		return meeting;
	}

	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void isInvitation(Boolean invitation) {
		this.invitation = invitation;
	}

	public Boolean isInvitation() {
		return invitation;
	}

	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}

	public int getMessageID() {
		return messageID;
	}
	
	public int getType()
	{
		return type;
		
	}
	public void setType(int type){
		this.type = type;
	}
}