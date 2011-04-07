package fp.common.models;

import org.joda.time.DateTime;

public class Message
{
    //
    private String description;
   
    //
    private String title;

    //
    private Boolean read;

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

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
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
}