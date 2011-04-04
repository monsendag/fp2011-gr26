package fp.client.gui.calendar;

import java.util.List;

import no.ntnu.fp.model.Activity;

import org.joda.time.DateTime;

public class CalendarModel {
	
	private DateTime monday;
	private List<Activity> activities;
	
	
	public CalendarModel() {
		setCurrentWeek();
	}
	

	public int getWeekNumber() {
		return monday.getWeekOfWeekyear();
	}

	public void setCurrentWeek() {
		DateTime now = new DateTime();
		monday = now.minusDays((now.getDayOfWeek()-1));  // monday == 1
	}
	
	public DateTime getMonday() {
		return monday;
	}


	public void setMonday(DateTime monday) {
		this.monday = monday;
	}


	public List<Activity> getActivities() {
		return activities;
	}


	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}


	public void setNextWeek() {
		
	}
	
	public void setPreviousWeek() {
		
	}
	
	public boolean inWeek(DateTime time) {
		return time.getYear() == getMonday().getYear() && time.getWeekOfWeekyear() == getMonday().getWeekOfWeekyear();
	}
}
