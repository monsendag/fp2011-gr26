package fp.common.network;

import java.util.ArrayList;
import java.util.List;

public class NetworkObject {
	List objects;
	
	public NetworkCommand command;
	
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

	public NetworkCommand getCommand() {
		return command;
	}

	public void setCommand(NetworkCommand command) {
		this.command = command;
	}
}