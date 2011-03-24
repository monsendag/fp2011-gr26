package no.ntnu.fp.model;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class XmlSerializer {
	private static XmlSerializer instance;
//	private 
	
	//private XmlSerializer() {
		
	//}
	
	public static XmlSerializer getInstance() {
		if(instance == null) instance = new XmlSerializer();
		return instance;
	}
	
	public static void main(String[] args) {
		System.out.println("initialize..");
		
		Person p = new Person();
		p.setDateOfBirth(Calendar.getInstance().getTime());
		p.setName("arne bjarne");
		p.setEmail("dageinm@stud.ntnu.no");
		
		
		XStream xStream = new XStream(new DomDriver());
		
		String xml = xStream.toXML(p);

		System.out.println(xml);
		
		System.out.println(".......");
		
		Person person = (Person)xStream.fromXML(xml);
		
		System.out.println(person.getName());
		
	}

}

