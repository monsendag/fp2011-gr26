package fp.client.gui.calendar;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * A GregorianCalendar with added property change support
 *
 */
public class PropertyChangeCalendar extends GregorianCalendar {


	private PropertyChangeSupport changeSupport;
	  
	/**
	* Creates a calendar set for the current date with the specified locale
	*/
	public PropertyChangeCalendar(Locale locale) {
		super(locale);
		changeSupport = new PropertyChangeSupport(this);
	}
	  
	@Override
	public void set(int field, int value) {
		super.set(field, value);
		changeSupport.firePropertyChange("set"+field, null, value);
	}

	  
	@Override
	public void add(int field, int amount) {
		super.add(field, amount);
		changeSupport.firePropertyChange("add"+field, null, amount);
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
