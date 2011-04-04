package no.ntnu.fp.model;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XmlSerializer {
	private static XmlSerializer instance;
	private static XStream xStream;
	
	
	private XmlSerializer() {
		XmlSerializer.xStream = new XStream(new DomDriver());
	}
	
	public static XmlSerializer getInstance() {
		if(instance == null) instance = new XmlSerializer();
		return instance;
	}
	/**
	 * @param obj - Object to serialize into xml
	 * @return (xml) Object serialized in xml
	 */
	public static String serialize(Object obj) {
		return xStream.toXML(obj);
	}
	
	/**
	 * @param xml - XML to unserialize
	 * @return unserialized object
	 */
	public static Object unSerialize(String xml) {
		return xStream.fromXML(xml);
	}
	
	public static XStream getXStream() {
		return xStream;
	}
}