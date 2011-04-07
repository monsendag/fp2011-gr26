package fp.client.gui.calendar;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;


import org.joda.time.DateTime;

import fp.common.models.Activity;
import fp.common.models.Employee;

public class CalendarModel {
	
	private DateTime monday;
	private List<Activity> activities;
	private PropertyChangeSupport changeSupport;
	
	public CalendarModel() {
		setCurrentWeek();
		changeSupport = new PropertyChangeSupport(this);
		activities = new ArrayList<Activity>();
	}

	public int getWeekNumber() {
		return monday.getWeekOfWeekyear();
	}

	public void setCurrentWeek() {
		DateTime now = new DateTime();
		monday = now.minusDays((now.getDayOfWeek()-1)).minusMillis(now.getMillisOfDay());  // Monday@00:00.000 
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
	
	public void addActivity(Activity activity) {
		if(!activities.contains(activity)) activities.add(activity);
		changeSupport.firePropertyChange("addActivity", null, activity);
	}
	
	public void addActivities(ArrayList<Activity> a) {
		for(Activity act : a) {
			activities.add(act);
		}
	}

	public void setNextWeek() {
		monday = monday.plusWeeks(1);
		changeSupport.firePropertyChange("week", null, monday);
	}
	
	public void setPreviousWeek() {
		monday = monday.minusWeeks(1);
		changeSupport.firePropertyChange("week", null, monday);
	}
	
	public boolean inWeek(DateTime time) {
		return time.getYear() == monday.getYear() && time.getWeekOfWeekyear() == monday.getWeekOfWeekyear();
	}
	
	/**
	* Adds a listener to this object
	* @param listener Object that will listen.
	*/
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	/**
	* Removes a listener from this object
	* @param listener Object that will stop listening.
	*/
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}
}