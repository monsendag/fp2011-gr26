

package no.ntnu.fp.model;

import java.util.Date;
import java.util.List;

//
class Day
{
	private Date date;
    //bruke linkedlist?
    private List<Activity> activities ;
    
	public Day(){
		date = null;
		activities = null;
	}
	public Day(Date date, List<Activity> acts){
		this.date = date;
		this.activities = acts;
	}

	//
    public void addActivity (Activity a){
    	// skjekke input? eller gjøres det andre steder?
    	activities.add(a);

    }

	//
    public void deleteActivity (Activity a) 
    {
    	activities.remove(a);

    }
}