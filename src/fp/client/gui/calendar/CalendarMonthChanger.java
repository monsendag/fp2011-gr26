package fp.client.gui.calendar;

import java.util.Calendar;

/**
 * A subclass of calendar value changer. This class displays the value as a month's name, and not an int.
 *
 */
public class CalendarMonthChanger extends CalendarValueChanger {
	/**
	 * Constructs a calendar month changer.
	 * @param calendar The calendar
	 */
	public CalendarMonthChanger(PropertyChangeCalendar calendar) {
		super(null, Calendar.MONTH, calendar);
	}

	@Override
	protected void updateValue() {
		int monthNumber = calendar.get(valueInt);
		String month;
			switch(monthNumber) {
			case 0: month = "Januar"; break;
			case 1: month = "Februar"; break;
			case 2: month = "Mars"; break;
			case 3: month = "April"; break;
			case 4: month = "Mai"; break;
			case 5: month = "Juni"; break;
			case 6: month = "Juli"; break;
			case 7: month = "August"; break;
			case 8: month = "September"; break;
			case 9: month = "Oktober"; break;
			case 10: month = "November"; break;
			case 11: month = "Desember"; break;
			default: month = ""; break;
		}
		valueLabel.setText(month);
	}
}
