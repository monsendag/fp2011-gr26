package fp.client.gui;

import java.awt.Component;
import java.net.URL;

import javax.swing.*;

public class MeetingRenderer extends DefaultListCellRenderer implements ListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		// get label from superclass
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		// get image path
		URL path = getClass().getResource("ico/"+(read ? "read" : "unread")+"-icon.png");
		// set icon and text
		label.setIcon(new ImageIcon(path));
		label.setText(message.getTitle());
		return label;
	}
}