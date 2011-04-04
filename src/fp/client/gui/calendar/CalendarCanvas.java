package fp.client.gui.calendar;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.JPanel;

import org.joda.time.DateTime;

import no.ntnu.fp.model.Activity;
import no.ntnu.fp.model.Person;

/**
* Draws activities corresponding to the current week of the supplied calendar.
*/
public class CalendarCanvas extends JPanel implements PropertyChangeListener {	
	private int rowHeight;
	private int columnWidth;
	private List<Activity> activities;

	private int secondsPerRow;
	private int beginHour;

	private Point pressedPoint;
	private Point releasedPoint;
	private Point mousePoint;
	private boolean isDragging;

	private PropertyChangeCalendar calendar;

	/**
	* Initializes the canvas
	* @param columnWidth - The width in pixels of each column
	* @param rowHeight - The height in pixels of each row
	* @param secondsPerRow - The number of seconds in each row
	* @param beginHour - The first hour of the day, i.e. the first displayed hour
	* @param beginDay - The first day in the week. Sunday = 0, Monday = 1...
	* @param week - The week to be displayed
	*/
	public CalendarCanvas(int columnWidth, int rowHeight, int secondsPerRow, int beginHour, PropertyChangeCalendar week) {
		this.rowHeight = rowHeight;
		this.columnWidth = columnWidth;
		this.secondsPerRow = secondsPerRow;
		this.beginHour = beginHour;
		//activities = Storage.Activitys;
		//	activities.addPropertyChangeListener(this);
	
		calendar = week;
		calendar.addPropertyChangeListener(this);
	
		addMouseListener();
	}

	/**
	* Draws everything
	*/
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	
		//highlight today's column
		{
			Calendar today = new GregorianCalendar(Locale.FRANCE);
			int todayDays = (int) Math.floor(1.0 * today.getTimeInMillis() / 1000 / 3600 / 24);
			int calendarDays = (int) Math.floor(1.0 * calendar.getTimeInMillis() / 1000 / 3600 / 24);
			int difference = todayDays - calendarDays;
			if(difference >= 0 && difference < 7) {          //fits inside calendar
			int x = difference * columnWidth;
			int y = 0;
			int width = columnWidth;
			int height = getHeight();
		
			g.setColor(Color.BLUE);
			g.fillRect(x, y, width, height);
			}
		}

		g.setColor(Color.black);
		// horizontal lines
		for (int i = 1; i < 24 - beginHour; i++) {
			g.drawLine(0, i * rowHeight, getWidth(), i * rowHeight);
		}
	
		// vertical lines
		for (int i = 1; i < 7; i++) {
			g.drawLine(i * columnWidth, 0, i * columnWidth, getHeight());
		}
	
	
		// check which activities should be drawn
		ActivityGraphics graph = new ActivityGraphics(columnWidth, rowHeight, secondsPerRow, beginHour, calendar);
	
		List<Activity> list; = Storage.getPersonsActivity(person);
	
		for (int i = 0; i < list.size(); i++) {
			Activity activity = list.get(i);
			DateTime appStart = activity.getStartTime();
			int appDays = (int) Math.floor(1.0 * appStart.getMillis() / 1000 / 3600 / 24);    // calculates number of days since the epoch
			int shownDays = (int) Math.floor(1.0 * calendar.getTimeInMillis() / 1000 / 3600 / 24);    // calculates number of days since the epoch
			int difference = appDays - shownDays;
			if(difference >= 0 && difference < 7)
			graph.draw(g, activity);
		}
	
	
		if(isDragging) {
			int x = ((int) pressedPoint.getX() / columnWidth) * columnWidth;
			int y = (int) pressedPoint.getY();
			int width = columnWidth;
			int height = (int)mousePoint.getY() - y;
			if(height < 0) {
			y += height;
			height *= -1;
		}
	
		int arc = Storage.rectangleArcRadius;
		g.setColor(Storage.colorCalendarDragFill);
		g.fillRoundRect(x+1, y+1, width-1, height-1, arc, arc);
	
		g.setColor(Storage.colorCalendarDragOutline);
		g.drawRoundRect(x+1, y+1, width-2, height-2, arc, arc);
		g.drawRoundRect(x+2, y+2, width-4, height-4, arc, arc);
		}
	}

	/**
	* Gets the Activity at the position (x,y) of the canvas. If no
	* Activity is at the position, null is returned.
	*
	* @param x
	*            The x-coordinate
	* @param y
	*            The y-coordinate
	* @return The Activity at the position, null if none is found
	*/
	public Activity getActivityByPosition(int x, int y) {
		ActivityGraphics graph = new ActivityGraphics(columnWidth,
		rowHeight, secondsPerRow, beginHour, calendar);
		for (int i = 0; i < activities.size(); i++) {
			Activity activity = activities.get(i);
			if (x >= graph.getCoordX(activity)
			&& x <= graph.getCoordX(activity) + graph.getWidth(activity)
			&& y >= graph.getCoordY(activity)
			&& y <= graph.getCoordY(activity) + graph.getHeight(activity)) {
				return activity;
			}
		}
		return null;
	}

	/**
	* Launches the Activity wizard if the registered drag is valid.
	* Automatically finds the correct start and end time.
	*
	* @param start
	*            The point in the canvas where the drag began
	* @param end
	*            The point in the canvas where the drag ended
	*/
	private void dragged(Point start, Point end) {
		ActivityGraphics graph = new ActivityGraphics(columnWidth, rowHeight, secondsPerRow, beginHour, calendar);
	
		if (start.y > end.y) {
			// start is after end, switch y-coordinates
			int a = start.y;
			start.y = end.y;
			end.y = a;
		}
	
		for (int i = 0; i < activities.size(); i++) {
			Activity app = activities.get(i);
			int appY = graph.getCoordY(app);
			int appX = graph.getCoordX(app);
			if (start.y <= appY && appY <= end.y && appX / columnWidth == start.x / columnWidth)
				// an Activity is in between the point
				return;
		}
		// there are no Activitys in between the start and end
		// positions
	
		// finds the start and end time of the Activity
		int day = (int) (start.x / columnWidth + calendar.get(Calendar.DAY_OF_MONTH));
		int startMillis = calculateTimeFromYCoordinate(start.y);
		int endMillis = calculateTimeFromYCoordinate(end.y);
	
		Calendar startCal = new GregorianCalendar();
		startCal.setTimeInMillis(calendar.getTimeInMillis());
		startCal.set(Calendar.DAY_OF_MONTH, day);
		startCal.set(Calendar.HOUR_OF_DAY, 0);
		startCal.set(Calendar.MINUTE, 0);
		startCal.add(Calendar.MILLISECOND, startMillis);
	
		Calendar endCal = new GregorianCalendar();
		endCal.setTimeInMillis(calendar.getTimeInMillis());
		endCal.set(Calendar.DAY_OF_WEEK, day);
		endCal.set(Calendar.HOUR_OF_DAY, 0);
		endCal.set(Calendar.MINUTE, 0);
		endCal.add(Calendar.MILLISECOND, endMillis);
	
		ActivityWizard wiz = new ActivityWizard(startCal, endCal);
		wiz.setVisible(true);
	}

	/**
	* Calculates the millisecond of day according to the y coordinate
	*
	* @param y
	*            The coordinate
	* @return The amount of milliseconds of the day before the coordinate
	*/
	private int calculateTimeFromYCoordinate(int y) {
		int startSecond = (int) ((1.0 * y / rowHeight + beginHour) * secondsPerRow);
		double startMinute = 1.0 * startSecond / 60;
		double minuteOffset = startMinute % 15;
		if (minuteOffset > 7.5)
		startSecond += (15 - minuteOffset) * 60;
		else if (minuteOffset <= 7.5)
		startSecond -= (minuteOffset) * 60;
	
		return startSecond * 1000;
	}

	private void addMouseListener() {
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
			}
		
			@Override
			public void mouseDragged(MouseEvent e) {
				if(pressedPoint != null) {
					isDragging = true;
					mousePoint = e.getPoint();
					repaint();
				}
			}
		});
	
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(pressedPoint != null) {
					Activity app = getActivityByPosition((int)pressedPoint.getX(), e.getY());
					if(app == null) {
						releasedPoint = new Point((int)pressedPoint.getX(), e.getY());
						dragged(pressedPoint, releasedPoint);
					}
				}
				pressedPoint = null; releasedPoint = null;
				isDragging = false;
				repaint();
		
			}
		
			@Override
			public void mousePressed(MouseEvent e) {
				Activity app = getActivityByPosition(e.getX(), e.getY());
				pressedPoint = app == null ? e.getPoint() : null; 
			}
		
			@Override
			public void mouseExited(MouseEvent e) {
			}
		
			@Override
			public void mouseEntered(MouseEvent e) {
			}
		
			@Override
			public void mouseClicked(MouseEvent e) {
				Activity app = getActivityByPosition(e.getX(), e.getY());
				if(app != null) app.requestEdit();
				pressedPoint = null; 
				releasedPoint = null;
			}
		});
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		this.updateUI();
	}
}