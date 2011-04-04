package fp.client.gui.calendar;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
* A panel that displays text and has a set size.
* It can also display the value of a calendar field, and listen for changes to it.
*/
public class CalendarLabel extends JPanel implements PropertyChangeListener {
	protected JLabel label;  // the label that displays the text
	protected CalendarModel model; // the calendarModel
	protected int offset; // offset to add to the value of the field
	
	/**
	* Constructor display some text and a field from a calendar.
	* @param text The text to display
	* @param width The maximum width of the label
	* @param height The maximum height of the label
	* @param calendar The calendar to display a field from
	* @param field The field to display
	*/
	public CalendarLabel(int width, int height, CalendarModel model, int offset) {
		Dimension size = new Dimension(width, height);
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		//this.setBorder(new BevelBorder(BevelBorder.RAISED));
		//this.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		this.offset = offset;
		label = new JLabel(getValue());
		add(label);
		model.addPropertyChangeListener(this);
	}

	/**
	* Returns the value of the field including offset
	* @return The value to be displayed
	*/
	protected String getValue() {
		return (offset < 10 ? "0"+offset : offset) + ":00";
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		label.setText(getValue());
	}
}