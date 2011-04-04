package fp.client.gui.calendar;

import org.joda.time.DateTime;

public class CalendarModel {
	
	private int week;

	public int getWeekNumber() {
		return week;
	}

	public void setWeekNumber(int weekNumber) {
		this.week = weekNumber;
	}
	
	public void setCurrentWeek() {
		week = new DateTime().getWeekOfWeekyear();
	}
	
	public void setNextWeek() {
		
	}
	
	public void setPreviousWeek() {
		
	}

}
