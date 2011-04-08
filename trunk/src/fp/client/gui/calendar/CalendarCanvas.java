package fp.client.gui.calendar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.swing.JPanel;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import fp.client.Client;
import fp.client.gui.Gui;
import fp.common.models.Activity;
import fp.common.models.Meeting;


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
		this.model = Client.get().calendarModel;
		this.model.addPropertyChangeListener(this);
		addMouseListener();
	}

	/**
	* Draws everything
	*/
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//highlight today's column
		DateTime today = new DateTime();
		
		// highlight current day
		if(model.inWeek(today)) {
			int x = (today.getDayOfWeek()-1) * columnWidth;
			int y = 0;
			g.setColor(new Color(Integer.parseInt("fff7d8", 16)));
			g.fillRect(x, y, columnWidth, getHeight());
		}
		
		// draw lines
		g.setColor(Color.GRAY);
		
		// horizontal lines
		g.drawLine(0, (24 * rowHeight)-1, getWidth(), (24 * rowHeight)-1);
		for (int i = 0; i < 24 - beginHour; i++) {
			g.drawLine(0, i * rowHeight, getWidth(), i * rowHeight);
		}

		// vertical lines
		for(int i = 0; i < 7; i++) {
			g.drawLine(i * columnWidth, 0, i * columnWidth, getHeight());
		}
		
		// draw activities
		for(Activity activity : model.getActivities()) {
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
	
			int arcRadius = 5;//Storage.rectangleArcRadius;
			
			g.setColor(new Color(Integer.parseInt("7489b6", 16))); // convert hex color to rgb integer
			
			g.fillRoundRect(x, y+1, width, height-1, arcRadius, arcRadius);
			
			g.setColor(Color.BLACK);
			g.drawRoundRect(x, y+1, width-1, height-1, arcRadius, arcRadius);
			g.drawRoundRect(x, y+1, width-1, height, arcRadius, arcRadius); // looks like a shadow
		}
	}
	
	
	/**
	* Draws the supplied Activity on the graphics object
	* @param g The graphics object to draw on
	* @param a The activity to draw
	*/
	public void drawActivity(Graphics g, Activity a) {
		int height = getActivityHeight(a);
		int width = getActivityWidth(a);
		
		int x = getCoordX(a);
		int y = getCoordY(a);
		
		Color fill, outline = Color.BLACK;
		String meeting = "81B674"; /// green
		String activity = "7489b6"; // blueish
		fill = new Color(Integer.parseInt(a instanceof Meeting ? meeting : activity, 16));
		
		int arcRadius = 5;
		// fill activity 
		g.setColor(fill);
		g.fillRoundRect(x, y+1, width, height-1, arcRadius, arcRadius);
		
		// draw stroke around activity
		g.setColor(outline);
		g.drawRoundRect(x, y+1, width-1, height-1, arcRadius, arcRadius);
		g.drawRoundRect(x, y+1, width-1, height, arcRadius, arcRadius); // looks like a shadow
	
		x += 5; // padding-top:5px;
		y += 5; // padding-left: 5px;
		float wrappingWidth = width - 5; // padding-right: 5px;

		AttributedString str;
		AttributedCharacterIterator attribCharIterator;
		LineBreakMeasurer lbm;
		
		String title = a.getTitle();
		if(title != null && title.length() > 0) {
			str = new AttributedString(title);
			str.addAttribute(TextAttribute.FOREGROUND, Color.BLACK, 0, title.length());
		    str.addAttribute(TextAttribute.FONT, new Font("TimesRoman", Font.BOLD,  13), 0, title.length());
			attribCharIterator = str.getIterator();
		    lbm = new LineBreakMeasurer(attribCharIterator, new FontRenderContext(null, false, false));
		    
		    while(lbm.getPosition() < title.length()) {
	    		TextLayout layout = lbm.nextLayout(wrappingWidth);
	    		y += layout.getAscent();
	    		layout.draw((Graphics2D) g, x, y);
	    		y += layout.getDescent() + layout.getLeading();
		    }
		}
			
		String description = a.getDescription();
		if(description != null && description.length() > 0) {
			str = new AttributedString(description);
			str.addAttribute(TextAttribute.FOREGROUND, Color.BLACK, 0, description.length());
		    str.addAttribute(TextAttribute.FONT, new Font("TimesRoman", Font.PLAIN,  11), 0, description.length());
			attribCharIterator = str.getIterator();
		    lbm = new LineBreakMeasurer(attribCharIterator, new FontRenderContext(null, false, false));
		    
		    while(lbm.getPosition() < description.length()) {
	    		TextLayout layout = lbm.nextLayout(wrappingWidth);
	    		y += layout.getAscent();
	    		layout.draw((Graphics2D) g, x, y);
	    		y += layout.getDescent() + layout.getLeading();
		    }
		}
	}

	/**
	* Gets the Activity at the position (x,y) of the canvas. If no
	* Activity is at the position, null is returned.
	*
	* @param x - The x-coordinate
	* @param y - The y-coordinate
	* @return The Activity at the position, null if none is found
	*/
	public Activity getActivityByPosition(int x, int y) {
		for(Activity a : model.getActivities()) {
			if(model.inWeek(a.getStartTime()) && x >= getCoordX(a) && x <= getCoordX(a) + getActivityWidth(a) && y >= getCoordY(a) && y <= getCoordY(a) + getActivityHeight(a)) {
				return a;
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
	
		for(Activity activity : model.getActivities()) {
			int actY = getCoordY(activity);
			int actX = getCoordX(activity);
			if(model.inWeek(activity.getStartTime()) && start.y <= actY && actY <= end.y && actX / columnWidth == start.x / columnWidth)
				// an Activity is in between the point
				return;
		}
		// there are no activities in between the start and end positions
		
		// finds the start and end time of the Activity
		DateTime day = model.getMonday().plusDays(start.x / columnWidth);
		
		DateTime startTime = day.plusMillis(calculateTimeFromYCoordinate(start.y));
		DateTime endTime = day.plusMillis(calculateTimeFromYCoordinate(end.y));
		
		fp.client.Client.get().gui.dragNewActivity(new Activity(null, startTime, endTime, "", ""));
	}

	/**
	* Calculates the millisecond of day according to the y coordinate. Increment to each quarter
	* @param y - The coordinate
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
	* @param activity
	* @return height in pixels
	*/
	public int getActivityHeight(Activity activity) {
		int duration = (int) new Duration(activity.getStartTime(), activity.getEndTime()).getMillis() / 1000;
		int height = (int) (1.0 * duration / secondsPerRow * rowHeight);
		return Math.max(height, 9);
	}

	/**
	* @param activity
	* @return width in pixels
	*/
	public int getActivityWidth(Activity activity) {
		return columnWidth;
	}

	/**
	* Get the x-coordinate the Activity should be drawn on.
	* @param activity The Activity
	* @return The x-coordinate
	*/
	public int getCoordX(Activity activity) {
		DateTime startTime = activity.getStartTime();
		return (startTime.getDayOfWeek() -1) * columnWidth;
	}

	/**
	* Get the y-coordinate the activity should be drawn on.
	* @param activity 
	* @return The y-coordinate
	*/
	public int getCoordY(Activity activity) {
		DateTime startTime = activity.getStartTime();

		//number of seconds after the start of the day the event begins
		int eventSecond = startTime.secondOfDay().get();

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
					Activity activity = getActivityByPosition((int)pressedPoint.getX(), e.getY());
					if(activity == null) {
						releasedPoint = new Point((int)pressedPoint.getX(), e.getY());
						dragged(pressedPoint, releasedPoint);
					}
				}
				pressedPoint = null; 
				releasedPoint = null;
				isDragging = false;
				repaint();
			}
		
			@Override
			public void mousePressed(MouseEvent e) {
				Activity activity = getActivityByPosition(e.getX(), e.getY());
				pressedPoint = activity == null ? e.getPoint() : null; 
			}
		
			@Override
			public void mouseExited(MouseEvent e) {
			}
		
			@Override
			public void mouseEntered(MouseEvent e) {
			}
		
			@Override
			public void mouseClicked(MouseEvent e) {
				Activity activity = getActivityByPosition(e.getX(), e.getY());
				if(activity != null) {
					fp.client.Client.get().gui.openActivity(activity);
				}
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