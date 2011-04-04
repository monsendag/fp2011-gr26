package fp.client.gui.calendar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Changes the value of a specific field in a calendar.
 * Displays the name of the value to the left, the current value in the middle
 * and buttons to increment and decrement the value to the right
 *
*/
public class CalendarValueChanger extends JPanel implements PropertyChangeListener {

	protected String valueName;
	protected int valueInt;
	protected Calendar calendar;
	protected JLabel valueLabel;

	/**
	* Constructs the value changer
	* @param valueName The name of the value as it should be displayed
	* @param valueInt The value of the field to be displayed
	* @param calendar The calendar that holds the value
	*/
	public CalendarValueChanger(String valueName, int valueInt, PropertyChangeCalendar calendar) {
		super();
		this.valueName = valueName;
		this.valueInt = valueInt;
		this.calendar = calendar;
		calendar.addPropertyChangeListener(this);

		//setup buttons and label
		JButton previousButton = new JButton("<");
		previousButton.setFocusable(false);
		previousButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				decrement();
			}
		});

		JButton nextButton = new JButton(">");
		nextButton.setFocusable(false);
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				increment();
			}
		});

		valueLabel = new JLabel();

		add(valueLabel);
		add(previousButton);
		add(nextButton);

		updateValue();
	}

	protected void updateValue() {
		valueLabel.setText(valueName + " " + calendar.get(valueInt));
		return;
	}

	private void increment() {
		//    int hasThisVariableToEnsureTheDayOfWeekDoesNotChange = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(valueInt, 1);
		//    calendar.set(Calendar.DAY_OF_WEEK, hasThisVariableToEnsureTheDayOfWeekDoesNotChange);
		updateValue();
	}

	private void decrement() {
		//    int hasThisVariableToEnsureTheDayOfWeekDoesNotChange = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(valueInt, -1);
		//    calendar.set(Calendar.DAY_OF_WEEK, hasThisVariableToEnsureTheDayOfWeekDoesNotChange);
		updateValue();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		updateValue();
	}

}
