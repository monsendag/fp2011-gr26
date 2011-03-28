package oving4;

import java.awt.Component;
import java.net.URL;

import javax.swing.*;

public class PersonRenderer extends DefaultListCellRenderer implements ListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		// get label from superclass
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		// cast value to Person
		Person person = (Person) value;
		// get image path based on gender (default to female)
		URL path = getClass().getResource("gfx/"+(person.getGender() == Gender.MALE ? "male" : "female")+"-icon.png");
		// set icon and text
		label.setIcon(new ImageIcon(path));
		label.setText(person.getName()+ " <" + person.getEmail()+">");
		return label;
	}
}