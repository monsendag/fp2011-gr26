package fp.client.gui.calendar;

import java.beans.PropertyChangeEvent;
import java.util.Calendar;

/**
 * Extends CalendarLabel to display day of week correctly. Day of week is displayed as a string, and day of month as int
 *
 */
public class CalendarDayLabel extends CalendarLabel {

	private String[] days = {"Mandag","Tirsdag","Onsdag","Torsdag","Fredag","Lørdag","Søndag"};
	private int beginDay = Calendar.MONDAY;

	/**
	* Constructor to display day of week and day of month according to offset.
	* The supplied offset is added to the fields value when it is displayed.
	* @param text The text to display
	* @param width The maximum width of the label
	* @param height The maximum height of the label
	* @param calendar The calendar to display a field from
	* @param field The field to display
	* @param offset An offset to the field's value
	*/
	public CalendarDayLabel(int width, int height, PropertyChangeCalendar calendar, int offset) {
		super("", width, height, calendar, Calendar.DAY_OF_MONTH);
		this.offset = offset;
		label.setText(days[getDayIndex()] + " " + getFieldValue());
	}

	private int getDayIndex() {
		int dayOfWeek = (cal.get(Calendar.DAY_OF_WEEK)+offset - beginDay) % 7;
		if(dayOfWeek < 0) {
			dayOfWeek += 7;
		}

		return dayOfWeek;
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		label.setText(days[getDayIndex()] + " " + getFieldValue());
	}

}
