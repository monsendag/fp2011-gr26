package fp.client.gui.calendar;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
* A panel that displays text and has a set size.
* It can also display the value of a calendar field, and listen for changes to it.
*/
public class CalendarLabel extends JPanel implements PropertyChangeListener {
	protected JLabel label;  // the label that displays the text
	protected Calendar cal; // the calendar
	protected String text; // the text to display
	protected int field; // the field of the calendar to display
	protected int offset; // offset to add to the value of the field
	
	/**
	* Constructor to create a simple calendar label, displaying only some text
	* @param text The text to display
	﻿* @param width The maximum width of the label
	* @param height The maximum height of the label
	*/
	public CalendarLabel(String text, int width, int height) {
		super(); 
		this.text = text;
		label = new JLabel(text);
		add(label);
		Dimension size = new Dimension(width, height);
		this.setPreferredSize(size);
		this.setBorder(new BevelBorder(BevelBorder.RAISED));
		//this.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		this.setMaximumSize(size);
	}
	
	/**
	﻿*
	﻿* Constructor display some text and a field from a calendar.
	﻿* @param text The text to display
	﻿* @param width The maximum width of the label
	﻿* @param height The maximum height of the label
	﻿* @param calendar The calendar to display a field from
	﻿* @param field The field to display
	﻿*/
	public CalendarLabel(String text, int width, int height, PropertyChangeCalendar calendar, int field) {
		this(text, width, height);
		label.setText(text + " " + calendar.get(field));
		this.cal = calendar;
		this.field = field;
		offset = 0;

		calendar.addPropertyChangeListener(this);
	}
	/**
	* Constructor to display some text and a field from a calendar.
	﻿* The supplied offset is added to the fields value when it is displayed.
	* @param text The text to display
	* @param width The maximum width of the label
	* @param height The maximum height of the label
	﻿* @param calendar The calendar to display a field from
	﻿* @param field The field to display
	﻿* @param offset An offset to the field's value
	﻿*/
	public CalendarLabel(String text, int width, int height, PropertyChangeCalendar calendar, int field, int offset) {
		this(text, width, height, calendar, field);
		this.offset = offset;
		label.setText(text + " " + getFieldValue());
	}

	/**
	﻿* Returns the value of the field including offset
	﻿* @return The value to be displayed
	﻿*/
	protected int getFieldValue() {
		GregorianCalendar cal2 = new GregorianCalendar(Locale.FRANCE);
		cal2.setTimeInMillis(cal.getTimeInMillis());
		cal2.add(field, offset);
		return cal2.get(field);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		label.setText(text + " " + getFieldValue());
	}
}