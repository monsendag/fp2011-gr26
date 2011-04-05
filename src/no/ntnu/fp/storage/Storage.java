package no.ntnu.fp.storage;

import java.util.HashMap;

import no.ntnu.fp.model.Activity;
import no.ntnu.fp.model.Employee;
import no.ntnu.fp.model.Meeting;
import no.ntnu.fp.model.Room;

public class Storage {
	private static Storage instance;
	
	public static volatile HashMap<String,Employee> empCache;
	public static volatile HashMap<Integer,Room> roomCache;
	public static volatile HashMap<Integer,Activity> actCache;
	public static volatile HashMap<Integer,Meeting> mtngCache;
	
	private DBConnection connection;
	
	private Storage() {
		connection = new DBConnection();
	}
	
	public static Storage getInstance() {
		if(instance == null) instance = new Storage();
		return instance;
	}
}
