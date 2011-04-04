package fp.client.gui.calendar;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Calendar;
import org.joda.time.DateTime;

import no.ntnu.fp.model.Activity;


/**
* Draws an Activity on a graphics object in the correct position.
*/
public class ActivityGraphics {
	private int secondsPerRow;	//milliseconds in each row of the calendar, 3600 == 1 hour
	private int columnWidth;	//width of each column in pixels
	private int rowHeight;		//height of each row in pixels
	private int beginHour;		//the first hour of the day
	private DateTime monday;	//calendar giving the first day to be displayed in the canvas

	/**
	* Initializes the drawer
	* @param columnWidth The width in pixels of each column
	* @param rowHeight The height in pixels of each row
	* @param secondsPerRow The number of seconds in each row
	* @param beginHour The first hour of the day, i.e. the first displayed hour
	* @param calendar A calendar set to the first day displayed on the calendar
	*/
	public ActivityGraphics(int columnWidth, int rowHeight, int secondsPerRow, int beginHour, DateTime monday) {
		this.columnWidth = columnWidth;
		this.rowHeight = rowHeight;
		this.secondsPerRow = secondsPerRow;
		this.beginHour = beginHour;
		this.monday = monday;
	}

	/**
	* Draws the supplied Activity on the graphics object
	* @param g The graphics object to draw on
	* @param activity The Activity to draw
	*/
	public void draw(Graphics g, Activity activity) {
		int height = getHeight(activity);
		int width = getWidth(activity);
		
		int x = getCoordX(activity);
		int y = getCoordY(activity);
		
		Color outline, fill;
		
		outline = Color.RED;
		fill = Color.CYAN;
		
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
		int eventSecond = startTime.get(Calendar.SECOND) + startTime.get(Calendar.MINUTE)*60 + startTime.get(Calendar.HOUR_OF_DAY) * 3600;

		//number of seconds since start of the day the calendar begins
		int beginSecond = beginHour * 3600;

		//if the event
		if(eventSecond < beginSecond)  beginSecond -= 3600 * 24;
		
		int y = (int) (1.0 * (eventSecond - beginSecond) * rowHeight / secondsPerRow);

		return y;
	}
}
