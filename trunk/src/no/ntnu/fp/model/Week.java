

package no.ntnu.fp.model;

import java.util.List;

//
public class Week 
{
	private Day[] days;
	private int weekNumber;
	
	public Week()
	{
		days = new Day[7];
		weekNumber = 0;
	}
	public Week(Day[] days, int weekNumber)
	{
		this.days = days;
		this.weekNumber = weekNumber;
	}
}
	