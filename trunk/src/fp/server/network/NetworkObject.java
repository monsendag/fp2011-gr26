package fp.server.network;

public class NetworkObject {
	List objects;
	
	public static enum Command {
		getActivities, getActivity, markRead, markUnread, createActivity,
		setActivities, 
		
	}
	
	private int command;
	


}
