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
import java.util.ArrayList;
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

	private int secondsPerRow;
	private int beginHour;

	private Point pressedPoint;
	private Point releasedPoint;
	private Point mousePoint;
	private boolean isDragging;
	
	private CalendarModel model;

	List<Activity> activities = new ArrayList<Activity>();// = Storage.getPersonsActivity(person);
	
	/**
	* Initializes the canvas
	* @param columnWidth - The width in pixels of each column
	* @param rowHeight - The height in pixels of each row
	* @param secondsPerRow - The number of seconds in each row
	* @param beginHour - The first hour of the day, i.e. the first displayed hour
	* @param beginDay - The first day in the week. Sunday = 0, Monday = 1...
	* @param week - The week to be displayed
	*/
	public CalendarCanvas(int columnWidth, int rowHeight, int secondsPerRow, int beginHour) {
		this.rowHeight = rowHeight;
		this.columnWidth = columnWidth;
		this.secondsPerRow = secondsPerRow;
		this.beginHour = beginHour;
		this.model = new CalendarModel();
	
		addMouseListener();
		
		activities.add(new Activity(null, new DateTime(), new DateTime().plusHours(1), "Arne bjarne", "her"));
	}

	/**
	* Draws everything
	*/
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	
		//highlight today's column
		DateTime today = new DateTime();
		
		if(model.inWeek(today)) {
			int x = today.getDayOfWeek() * columnWidth;
			int y = 0;
			g.setColor(new Color(Integer.parseInt("fff7d8", 16)));
			g.fillRect(x, y, columnWidth, getHeight());
		}
		
		g.setColor(Color.GRAY);
		// horizontal lines
		for (int i = 1; i < 24 - beginHour; i++) {
			g.drawLine(0, i * rowHeight, getWidth(), i * rowHeight);
		}
	
		// vertical lines
		for(int i = 1; i < 7; i++) {
			g.drawLine(i * columnWidth, 0, i * columnWidth, getHeight());
		}
		
		// draw activities
		for(int i = 0; i < activities.size(); i++) {
			Activity activity = activities.get(i);
			DateTime startTime = activity.getStartTime();
			if(model.inWeek(startTime)) drawActivity(g, activity);
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
	
		int arc = 5;//Storage.rectangleArcRadius;
	//	g.setColor(Storage.colorCalendarDragFill);
		g.fillRoundRect(x+1, y+1, width-1, height-1, arc, arc);
	
	//	g.setColor(Storage.colorCalendarDragOutline);
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
		
		for (int i = 0; i < activities.size(); i++) {
			Activity activity = activities.get(i);
			if (x >= getCoordX(activity) && x <= getCoordX(activity) + getWidth(activity) && y >= getCoordY(activity)
			&& y <= getCoordY(activity) + getHeight(activity)) {
				return activity;
			}
		}
		return null;
	}
	

	/**
	* Launches the Activity wizard if the registered drag is valid.
	* Automatically finds the correct start and end time.
	*
	* @param start - The point in the canvas where the drag began
	* @param end - The point in the canvas where the drag ended
	*/
	private void dragged(Point start, Point end) {
		if(start.y > end.y) {
			// start is after end, switch y-coordinates
			int a = start.y;
			start.y = end.y;
			end.y = a;
		}
	
		for(int i = 0; i < activities.size(); i++) {
			Activity act = activities.get(i);
			int actY = getCoordY(act);
			int actX = getCoordX(act);
			if(start.y <= actY && actY <= end.y && actX / columnWidth == start.x / columnWidth)
				// an Activity is in between the point
				return;
		}
		// there are no Activitys in between the start and end
		// positions
	
		// finds the start and end time of the Activity
		//int day = (int) (start.x / columnWidth + calendar.get(Calendar.DAY_OF_MONTH));
		int startMillis = calculateTimeFromYCoordinate(start.y);
		int endMillis = calculateTimeFromYCoordinate(end.y);
	/*
		Calendar startCal = new GregorianCalendar();
		//startCal.setTimeInMillis(calendar.getTimeInMillis());
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
	*/
		//ActivityWizard wiz = new ActivityWizard(startCal, endCal);
		//wiz.setVisible(true);
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
	
	/**
	* Draws the supplied Activity on the graphics object
	* @param g The graphics object to draw on
	* @param activity The activity to draw
	*/
	public void drawActivity(Graphics g, Activity activity) {
		int height = getHeight(activity);
		int width = getWidth(activity);
		
		int x = getCoordX(activity);
		int y = getCoordY(activity);
		
		Color outline, fill;
		
		outline = Color.RED;
		fill = new Color(Integer.parseInt("7489b6", 16));
		
		int arcRadius = 5;
		
		// draw stroke around activity
		g.setColor(outline);
		g.drawRoundRect(x+2, y+2, width-3, height-3, arcRadius, arcRadius);
	
		// fill activity 
		g.setColor(fill);
		g.fillRoundRect(x+1, y+1, width-1, height-1, arcRadius, arcRadius);
	
		g.setColor(Color.black);
		x += 10;
		y += 20;
		g.drawString(activity.getDescription(), x, y);
		y+=20;
		//if(res.getRoom() != null)
		//g.drawString(res.getRoom().getName(), x, y);
		y += 20;
	}

	/**
	* @param activity
	* @return height in pixels
	*/
	public int getHeight(Activity activity) {
		int duration = (int) ((activity.getEndTime().getMillis() - activity.getStartTime().getMillis()) / 1000);
		int height = (int) (1.0 * duration / secondsPerRow * rowHeight);
		return height;
	}


	/**
	* @param app Any Activity
	* @return width in pixels
	*/
	public int getWidth(Activity activity) {
		return columnWidth;
	}

	/**
	* Get the x-coordinate the Activity should be drawn on.
	* @param activity The Activity
	* @return The x-coordinate
	*/
	public int getCoordX(Activity activity) {
		DateTime startTime = activity.getStartTime();
	
		// calculate the x-coordinate
		int offset = startTime.getDayOfWeek();
		if(offset < 0)
		offset += 7;
	
		int x = offset * columnWidth;
	
		return x;
	}

	/**
	* Get the y-coordinate the activity should be drawn on.
	* @param activity 
	* @return The y-coordinate
	*/
	public int getCoordY(Activity activity) {
		DateTime startTime = activity.getStartTime();

		//number of seconds after the start of the day the event begins
		int eventSecond = startTime.secondOfDay().get();//startTime.get(Calendar.SECOND) + startTime.get(Calendar.MINUTE)*60 + startTime.get(Calendar.HOUR_OF_DAY) * 3600;

		//number of seconds since start of the day the calendar begins
		int beginSecond = beginHour * 3600;

		//if the event
		if(eventSecond < beginSecond)  beginSecond -= 3600 * 24;
		
		int y = (int) (1.0 * (eventSecond - beginSecond) * rowHeight / secondsPerRow);

		return y;
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
				//if(app != null) app.requestEdit();
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