

package no.ntnu.fp.model;

import java.util.Date;

//
public class Activity 
{
	private Date startTime, endTime;
	private String  description, location;
	
	public Activity()
	{
		startTime = null;
		endTime = null;
		description = "";
		location = "";
		
	}
	
	
	public Activity(Date startTime, Date endTime, String description, String location)
	{
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
		this.location = location;
	}
	
	public Date getStartTime()
	{
		return startTime;
	}
	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}
	
	public Date getEndTime()
	{
		return endTime;
	}
	public void setEndTime()
	{
		this.endTime = endTime;
	}
	
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getLocation()
	{
		return location;
	}
	public void setLocation(String location)
	{
		this.location = location;
	}
	
	
	public void cancelActivity()
	{
		// DESTROOYY
	}
	
	
}