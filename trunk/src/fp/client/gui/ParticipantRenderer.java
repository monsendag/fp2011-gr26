package fp.client.gui;

import java.awt.Component;
import java.net.URL;

import javax.swing.*;

import fp.common.models.Employee;
import fp.common.models.Participant;
import fp.common.models.Participant.Status;

public class ParticipantRenderer extends DefaultListCellRenderer implements ListCellRenderer {

	
	
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		// get label from superclass
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		// get image path
		String iconName;
		Participant participant = (Participant) value;
		
		if(participant.getStatus() == Participant.Status.ATTENDING){ iconName = "attending"; }
		else if(participant.getStatus() == Participant.Status.NOT_ATTENDING){ iconName = "notAttending"; }
		else iconName = "awatingReply";
		
		URL path = getClass().getResource("graphics/"+iconName+".png");
		// set icon and text
		label.setText(participant.getEmployee().getName());
		label.setIcon(new ImageIcon(path));
		return label;
	}
}