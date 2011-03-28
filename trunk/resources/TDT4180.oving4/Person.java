package oving4;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Person implements PropertyChangeListener {
	private PropertyChangeSupport pcs;
	private String name;
	private String email;
	private String dateOfBirth;
	private Gender gender;
	private int height;
	
	private boolean debug = false;
	
	public static String NAME_PROPERTY = "name", GENDER_PROPERTY = "gender", 
						DOB_PROPERTY = "dateOfBirth", EMAIL_PROPERTY = "email",
						HEIGHT_PROPERTY = "height";

	public Person() {
		pcs = new PropertyChangeSupport(this);
	}
	
	public Person(String name, String email, String dateOfBirth, Gender gender, int height) {
		pcs = new PropertyChangeSupport(this);
		this.name = name;
		this.email = email;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
		this.height = height;
	}
		
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		pcs.firePropertyChange(NAME_PROPERTY, this.name, name);
		this.name = name;
	}
	
	public Gender getGender() {
		return gender;
	}
	
	public void setGender(Gender gender) {
		pcs.firePropertyChange(GENDER_PROPERTY, this.gender, gender);
		this.gender = gender;
	}
	
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(String dateOfBirth) {
		pcs.firePropertyChange(DOB_PROPERTY, this.dateOfBirth, dateOfBirth);
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		pcs.firePropertyChange(EMAIL_PROPERTY, this.email, email);
		this.email = email;
	}
	
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		pcs.firePropertyChange(HEIGHT_PROPERTY, this.height, height);
		this.height = height;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
	
	public String toString() {
		return this.name;
	}

	public void propertyChange(PropertyChangeEvent e) {
		if(e.getPropertyName() == Person.NAME_PROPERTY) this.name = (String)e.getNewValue();
		if(e.getPropertyName() == Person.DOB_PROPERTY) this.dateOfBirth = (String) e.getNewValue();
		if(e.getPropertyName() == Person.EMAIL_PROPERTY) this.email = (String) e.getNewValue();
		if(e.getPropertyName() == Person.HEIGHT_PROPERTY) this.height = (Integer) e.getNewValue();
		if(e.getPropertyName() == Person.GENDER_PROPERTY) this.gender = (Gender) e.getNewValue();
	}
}