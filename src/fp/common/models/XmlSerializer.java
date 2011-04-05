package fp.common.models;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XmlSerializer {
	private static XmlSerializer instance;
	private XStream xStream;
	
	
	private XmlSerializer() {
		xStream = new XStream(new DomDriver());
	}
	
	public static XmlSerializer getInstance() {
		if(instance == null) instance = new XmlSerializer();
		return instance;
	}
	/**
	 * @param obj - Object to serialize into xml
	 * @return (xml) Object serialized in xml
	 */
	public String serialize(Object obj) {
		return xStream.toXML(obj);
	}
	
	/**
	 * @param xml - XML to unserialize
	 * @return unserialized object
	 */
	public Object unSerialize(String xml) {
		return xStream.fromXML(xml);
	}
	
	public XStream getXStream() {
		return xStream;
	}
}