﻿package no.ntnu.fp.model;

import java.util.Date;

public class Activity 
{
	private int id;
	private Date startTime, endTime;
	private String description, location;
	private Employee owner;
	
	public Activity()
	{
		startTime = null;
		endTime = null;
		description = "";
		location = "";
		setOwner(null);
	}
	
	public Activity(Employee owner, Date startTime, Date endTime, String description, String location)
	{
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
		this.location = location;
		this.setOwner(owner);
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
	public void setEndTime(Date endTime)
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


	public void setId(int id) {
		this.id = id;
	}


	public int getId() {
		return id;
	}

	public void setOwner(Employee owner) {
		this.owner = owner;
	}

	public Employee getOwner() {
		return owner;
	}
	
	
}