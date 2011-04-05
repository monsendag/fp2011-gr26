package fp.network;

import java.util.ArrayList;
import java.util.List;

public class NetworkObject {
	List objects;
	
	public Command command;
	
	public static enum Command {
		getActivities, getActivity, markRead, markUnread, createActivity,
		setActivities
	}
	
	
	public NetworkObject() {
		objects = new ArrayList<Object>();
	}
	
	public void addObject(Object o) {
		if(!objects.contains(o)) objects.add(o);
	}
	
	public void removeObject(Object o) {
		objects.remove(o);
	}
	
	public List getObjects() {
		return objects;
	}

	public void setObjects(List objects) {
		this.objects = objects;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}
}