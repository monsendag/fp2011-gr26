package fp.client.gui;

import java.awt.Component;
import java.net.URL;

import javax.swing.*;

import fp.client.Client;
import fp.common.models.Message;

public class MessageRenderer extends DefaultListCellRenderer implements ListCellRenderer {

	
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Message message = (Message) value;
		// get label from superclass
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		// set icon and text
		label.setIcon(Client.get().graphics.get(message.isRead() ? "readMessage" : "unreadMessage"));
		label.setText(message.getTitle());
		return label;
	}
}