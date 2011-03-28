package oving4;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PersonListPanel extends JPanel implements PropertyChangeListener {
	JList list;
	PersonPanel panel;
	DefaultListSelectionModel selectionModel;
	
	public PersonListPanel() {
		list = new JList();
		list.setName("PersonList");
		
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(250, 110));
		add(scrollPane);
		
		panel = new PersonPanel();
		panel.setName("PersonPanel");
		panel.addPropertyChangeListener(this);
		add(panel);
		
		selectionModel = new ToggleSelectionModel();
	    selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    list.setSelectionModel(selectionModel);
		list.addListSelectionListener(new SelectionListener());
		list.setCellRenderer(new PersonRenderer());
		
		
		JPanel buttonPanel = new JPanel();
		
	 	GridBagConstraints c = new GridBagConstraints();
    	buttonPanel.setLayout(new GridBagLayout());
    	c.fill = GridBagConstraints.BOTH;
    	c.gridy = 0; c.gridx = 0;
		
		JButton newPerson = new JButton("New Person");
		newPerson.setName("NewPersonButton");
		newPerson.addActionListener(new NewPersonListener());
		buttonPanel.add(newPerson, c);
		
		JButton delPerson = new JButton("Delete Person");
		delPerson.setName("DeletePersonButton");
		delPerson.addActionListener(new DeletePersonListener());
		
		c.gridy = 1;
		buttonPanel.add(delPerson, c);
		add(buttonPanel);
	}
	
	public DefaultListModel getModel() {
		return (DefaultListModel) list.getModel();
	}

	public void setModel(DefaultListModel model) {
		list.setModel(model);
	}
	
	class NewPersonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Person p = new Person();
			getModel().addElement(p);
			panel.setModel(p);
			panel.clear();
			int index = getModel().indexOf(p);
			selectionModel.setSelectionInterval(index, index);
		}
	}
	
	class DeletePersonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int newPos, pos = getModel().indexOf(panel.getModel());
			getModel().removeElement(panel.getModel());
			panel.setModel(null);
			if(!getModel().isEmpty()) {
				newPos = pos > 0 ? --pos : 0;
				selectionModel.setSelectionInterval(newPos, newPos);
				panel.setModel((Person)getModel().getElementAt(newPos));
			}
		}
	}
	
	class SelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if(list.isSelectionEmpty()) {
				panel.setModel(null);
			}
			else {
				panel.setModel((Person) getModel().elementAt(selectionModel.getAnchorSelectionIndex()));
			}
		}
	}
	
	class ToggleSelectionModel extends DefaultListSelectionModel
	{
	    public void setSelectionInterval(int index0, int index1) {
			if(isSelectedIndex(index0)) {
				removeSelectionInterval(index0, index1);
			}
			else {
			    super.setSelectionInterval(index0, index1);
			}
	    }
	}

	public void propertyChange(PropertyChangeEvent e) {
		list.repaint();
	}  
}
