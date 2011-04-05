package fp.client.gui.calendar;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
	private int topLabelHeight = 22;        // the height of the topLabelPanel
	private JScrollPane scrollPane;         // allows the canvas to scroll, also has topLabelPanel and leftLabelPanel

	private int columnWidth = 120;          // the width of each column
	private int rowHeight = 32;            	// the height of each row

	private int beginHour = 0;				// the hour of day the calendar begins. 6 == 06:00

	private CalendarModel model;

	/**
	* Create the CalendarPane
	*/
	public CalendarPanel() {
		this.model = new CalendarModel();


		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(rowHeight);
		
		topLabelPanel = new JPanel();
		scrollPane.setColumnHeaderView(topLabelPanel);
		topLabelPanel.setLayout(new BoxLayout(topLabelPanel, BoxLayout.X_AXIS));

		leftLabelPanel = new JPanel();
		scrollPane.setRowHeaderView(leftLabelPanel);
		leftLabelPanel.setLayout(new BoxLayout(leftLabelPanel, BoxLayout.Y_AXIS));
		
		calendarCanvas = new CalendarCanvas(columnWidth, rowHeight, 3600, beginHour, model);  
		calendarCanvas.setBackground(Color.WHITE);
		scrollPane.setBackground(new Color(240,240,240));
		calendarCanvas.setBorder(null);
		scrollPane.setBorder(null);
		calendarCanvas.setLayout(null);
		calendarCanvas.setPreferredSize(new Dimension(columnWidth*7,rowHeight*(24-beginHour)));
		scrollPane.setViewportView(calendarCanvas);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			   public void run() { 
				   scrollPane.getVerticalScrollBar().setValue(7 * rowHeight);
			   }
			});
		
		setPreferredSize(new Dimension(columnWidth * 7, 400));
		
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
		JPanel label;
		for(int offset=beginHour; offset < 24; offset++) {
			label = new CalendarLabel(leftLabelWidth, rowHeight, model, offset);
			leftLabelPanel.add(label);
		}

		topLabelPanel.add(Box.createHorizontalStrut(1));
		for(int offset=0; offset<7; offset++) {
			label = new CalendarDayLabel(columnWidth, topLabelHeight, model, offset);
			label.setBackground(new Color(240,240,240));
			topLabelPanel.add(label);
		}
	}
}