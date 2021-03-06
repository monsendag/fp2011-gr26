﻿package fp.common.models;

import org.joda.time.DateTime;

public class Activity 
{
	private int id;
	private DateTime startTime, endTime;
	private String description, title;
	private Employee owner;
	
	public Activity() {
		startTime = null;
		endTime = null;
		description = "";
		setOwner(null);
	}
	
	public Activity(Employee owner, DateTime startTime, DateTime endTime, String description, String title) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
		this.title = title;
		this.setOwner(owner);
	}
	
	public DateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}
	
	public DateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}
	
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
	
	public void cancelActivity() {
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