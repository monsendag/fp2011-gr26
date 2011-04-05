package fp.client.gui.calendar;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * Extends CalendarLabel to display day of week correctly. Day of week is displayed as a string, and day of month as int
 *
 */
public class CalendarDayLabel extends JPanel implements PropertyChangeListener {
	private String[] days = {"Mandag","Tirsdag","Onsdag","Torsdag","Fredag","Lørdag","Søndag"};
	private int offset;
	private JLabel label;
	private CalendarModel model;
	
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
	public CalendarDayLabel(int width, int height, CalendarModel model, int offset) {
		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setMaximumSize(size);
		setBackground(new Color(240,240,240));
	//	this.setBorder(new BevelBorder(BevelBorder.RAISED));
//		this.setBorder(new BevelBorder(BevelBorder.RAISED));
		this.offset = offset;
		this.model = model;
		label = new JLabel(getValue());
		add(label);
		model.addPropertyChangeListener(this);
	}
	
	public String getValue() {
		return days[offset] + " "+model.getMonday().plusDays(offset).getDayOfMonth()+ ". " + monthToString(model);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		label.setText(getValue());
	}
	
	public String monthToString(CalendarModel model){
		switch (model.getMonday().plusDays(offset).getMonthOfYear()) {
		case 1: return "Januar";
		case 2: return "Februar";
		case 3: return "Mars";
		case 4: return "April";
		case 5: return "Mai";
		case 6: return "Juni";
		case 7: return "Juli";
		case 8: return "August";
		case 9: return "September";
		case 10: return "Oktober";
		case 11: return "November";
		case 12: return "Desember";
		default: return "fail";
		}
		
	}
}