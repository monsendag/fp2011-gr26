package fp.client.gui;

import java.awt.Component;
import java.net.URL;

import javax.swing.*;

public class ParticipantRenderer extends DefaultListCellRenderer implements ListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		// get label from superclass
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		// get image path
		String iconName;
		if(participant.getStatus() == attending){ iconName = "attending"; }
		if(participant.getStatus() == notAttending){ iconName = "notAttending"; }
		else 
			iconName = "awatingReply";
		
		URL path = getClass().getResource("ico/"+iconName+"-icon.png");
		// set icon and text
		label.setText(person.getName());
		label.setIcon(new ImageIcon(path));
		return label;
	}
}