package fp.client.gui;

import java.awt.Component;
import java.net.URL;

import javax.swing.*;

import fp.common.models.Message;

public class MessageRenderer extends DefaultListCellRenderer implements ListCellRenderer {

	
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Message message = (Message) value;
		// get label from superclass
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		// get image path
		URL path = getClass().getResource("graphics/"+(message.isRead() ? "read" : "unread")+"Message.png");
		// set icon and text
		label.setIcon(new ImageIcon(path));
		label.setText(message.getTitle());
		return label;
	}
}