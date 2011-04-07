package fp.common.storage;

import java.io.IOException;

import fp.client.Client;
import fp.common.models.Activity;

public class ActivityStorage {
	
	
	
	
	public static void createActivity(Activity a) throws IOException{
		Client.get().calendarModel.addActivity(a);
		Client.get().connection.addActivity(a);
	}
	
}
