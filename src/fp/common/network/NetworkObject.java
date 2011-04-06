package fp.common.network;

import java.util.HashMap;


public class NetworkObject extends HashMap<String, Object> {
	
	public NetworkCommand command;
	
	public NetworkObject() {
		super();
	}
	
	public NetworkCommand getCommand() {
		return command;
	}

	public void setCommand(NetworkCommand command) {
		this.command = command;
	}
}