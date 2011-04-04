package fp.client.gui.calendar;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
* The panel in which the calendar is drawn
*
*/
public class CalendarPanel extends JPanel {

	private CalendarCanvas calendarCanvas;	// this canvas draws the calendar's appointments in a grid
	private JPanel topLabelPanel;			// this panel holds the labels indicating day of week/month
	private JPanel leftLabelPanel;          // this panel holds the labels indicating time of day
	private int leftLabelWidth = 50;        // the width of the leftLabelPanel
	private int topLabelHeight = 30;        // the height of the topLabelPanel
	private JScrollPane scrollPane;         // allows the canvas to scroll, also has topLabelPanel and leftLabelPanel

	private PropertyChangeCalendar calendar;// sets the week the calendar displays

	private int columnWidth = 120;          // the width of each column
	private int rowHeight = 32;            	// the height of each row

	private int beginHour = 7;              // the hour of day the calendar begins. 6 == 06:00

	private JPanel buttonPanel;             // the panel holding the buttons

	/**
	* Create the CalendarPane
	*/
	
	public CalendarPanel() {

		this.calendar = new PropertyChangeCalendar(Locale.FRANCE);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		buttonPanel.add(new CalendarValueChanger("Dag", Calendar.DAY_OF_MONTH, calendar));
		buttonPanel.add(new CalendarValueChanger("Uke", Calendar.WEEK_OF_YEAR, calendar));
		buttonPanel.add(new CalendarMonthChanger(calendar));
		buttonPanel.add(new CalendarValueChanger("År", Calendar.YEAR, calendar));

		add(buttonPanel);

		scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

		topLabelPanel = new JPanel();
		scrollPane.setColumnHeaderView(topLabelPanel);
		topLabelPanel.setLayout(new BoxLayout(topLabelPanel, BoxLayout.X_AXIS));

		leftLabelPanel = new JPanel();
		scrollPane.setRowHeaderView(leftLabelPanel);
		leftLabelPanel.setLayout(new BoxLayout(leftLabelPanel, BoxLayout.Y_AXIS));

		calendarCanvas = new CalendarCanvas(columnWidth, rowHeight, 3600, beginHour);    
		calendarCanvas.setBackground(Color.WHITE);
		calendarCanvas.setBorder(null);
		scrollPane.setViewportView(calendarCanvas);
		calendarCanvas.setLayout(null);

		calendarCanvas.setPreferredSize(new Dimension(columnWidth*7,rowHeight*(24-beginHour)));
		//scrollPane.setMaximumSize(new Dimension(columnWidth*7+leftLabelWidth+20,rowHeight*(24-beginHour)+topLabelHeight+20));

		scrollPane.getVerticalScrollBar().setValue(226);
		
		placeLabels();

		add(scrollPane);
		add(Box.createGlue());

		calendarCanvas.updateUI();
	}

	/**
	* Places the labels in the scroll pane (the time of day and day of week)
	*/
	private void placeLabels() {
		leftLabelPanel.add(Box.createVerticalStrut(1));
		for(int i=beginHour; i < 24; i++) {
			JPanel l = new CalendarLabel(i+":00", leftLabelWidth, rowHeight);
			leftLabelPanel.add(l);
		}

		topLabelPanel.add(Box.createHorizontalStrut(1));
		for(int offset=0; offset<7; offset++) {
			//JPanel l = new CalendarLabel(days[i],columnWidth,topLabelHeight);
			JPanel l = new CalendarDayLabel(columnWidth,topLabelHeight, calendar, offset);
			topLabelPanel.add(l);
		}
	}
}
