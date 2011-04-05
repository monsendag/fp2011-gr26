package fp.client.gui.calendar;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import no.ntnu.fp.model.Activity;
import no.ntnu.fp.model.Person;

import org.joda.time.DateTime;

public class CalendarModel {
	
	private DateTime monday;
	private List<Activity> activities;
	private PropertyChangeSupport changeSupport;
	
	public CalendarModel() {
		setCurrentWeek();
		changeSupport = new PropertyChangeSupport(this);
		activities = new ArrayList<Activity>();
		DateTime t = new DateTime(2011, 4, 6, 10, 0, 0, 0);
		activities.add(new Activity(null, t, t.plusHours(2).plusMinutes(15), "Arne bjarne", "her"));
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
	
	public void addPersonActivities(Person p) {
		for(Activity a : p.getActivities()) {
			activities.add(a);
		}
	}


	public void setNextWeek() {
		DateTime newTime = monday.plusWeeks(1);
		changeSupport.firePropertyChange("week", monday, newTime);
		monday = monday.plusWeeks(1);
	}
	
	public void setPreviousWeek() {
		DateTime newTime = monday.minusWeeks(1);
		changeSupport.firePropertyChange("week", monday, newTime);
		monday = monday.minusWeeks(1);
	}
	
	public boolean inWeek(DateTime time) {
		return time.getYear() == getMonday().getYear() && time.getWeekOfWeekyear() == getMonday().getWeekOfWeekyear();
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
