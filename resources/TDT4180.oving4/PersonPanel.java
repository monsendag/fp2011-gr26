package oving4;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial") 
public class PersonPanel extends JPanel implements PropertyChangeListener {
	private Person model;
	
	private JTextField nameField, emailField, dobField;
	private JComboBox comboBox;
	private JSlider slider;
	
	protected PropertyChangeSupport pcs;
	
	public PersonPanel() {
		pcs = new PropertyChangeSupport(this);
		
		setupComponents();
		addComponents();
		
		setPreferredSize(new Dimension(500, 110));

		addNameListener(new NameListener());
		addEmailListener(new EmailListener());
		addDateOfBirthListener(new DateOfBirthListener());
		addGenderListener(new GenderListener());
		addHeightListener(new HeightListener());
	}
	
	public Person getModel() {
		return model;
	}

	public void setModel(Person model) {
		// if there is a model stored already, remove all listeners from it
		if(this.model != null) {
			this.model.removePropertyChangeListener(this);
			removePropertyChangeListener(this.model);
		}
		
		if(model == null) clear();
		else {
			// set values
			nameField.setText(model.getName());
			dobField.setText(model.getDateOfBirth());
			emailField.setText(model.getEmail());
			comboBox.setSelectedItem(model.getGender());
			slider.setValue(model.getHeight());
			// add listeners
			model.addPropertyChangeListener(this);
			addPropertyChangeListener(model);
		}
		this.model = model;
	}
	
	
	public void clear() {
		nameField.setText("");
		dobField.setText("");
		emailField.setText("");
		comboBox.setSelectedItem(Gender.MALE);
		slider.setValue(170);
	}
	
	private void setupComponents() {
		nameField = new JTextField(30);
		nameField.setName("NamePropertyComponent");
		
		// email
		emailField = new JTextField(30);
		emailField.setName("EmailPropertyComponent");

		// birthday
		dobField = new JTextField(30);
		dobField.setName("dateOfBirthPropertyComponent");

		// gender
		comboBox = new JComboBox();
		comboBox.setName("GenderPropertyComponent");
		comboBox.setModel(new DefaultComboBoxModel(Gender.values()));
		
		// height
		slider = new JSlider();
		slider.setName("HeightPropertyComponent");
		slider.setMinimum(120);
		slider.setMaximum(220);
		slider.setValue(170);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(5);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setPreferredSize(new Dimension(300, 50));
	}
	
	private void addComponents() {
		GridBagConstraints c = new GridBagConstraints();
    	setLayout(new GridBagLayout());
    	c.fill = GridBagConstraints.BOTH;
    	
    	c.gridy = 0; c.gridx = 0; 
		add(new JLabel("Name:"),c);
		c.gridy = 0; c.gridx = 1;
		c.gridwidth = 3;
		add(nameField,c);
		
		c.gridwidth = 1;
		c.gridy = 1; c.gridx = 0;
		add(new JLabel("Email:"),c);
		c.gridwidth = 3;
		c.gridy = 1; c.gridx = 1;
		add(emailField,c);
		
		c.gridwidth = 1;
		c.gridy = 2; c.gridx = 0;
		add(new JLabel("Birthday:  "),c);
		c.gridwidth = 3;
		c.gridy = 2; c.gridx = 1;
		add(dobField,c);
		
		c.gridy = 3; c.gridx = 0;
		add(new JLabel("Gender:"),c);
		c.gridwidth = 1;
		c.gridy = 3; c.gridx = 1;
		add(comboBox,c);
		
		c.gridy = 3; c.gridx = 2;
		add(new JLabel(" Height:"),c);
		c.gridy = 3; c.gridx = 3;
		add(slider,c);
	}
	
	public void addNameListener(KeyListener l) {
		nameField.addKeyListener(l);
	}
	
	public void addEmailListener(KeyListener l) {
		emailField.addKeyListener(l);
	}
	
	public void addDateOfBirthListener(KeyListener l) {
		dobField.addKeyListener(l);
	}
	
	public void addGenderListener(ActionListener l) {
		comboBox.addActionListener(l);
	}
	
	public void addHeightListener(ChangeListener l) {
		slider.addChangeListener(l);
	}

	// a property is changed in the model
	public void propertyChange(PropertyChangeEvent e) {
		if(e.getPropertyName() == Person.NAME_PROPERTY) nameField.setText((String) e.getNewValue());
		if(e.getPropertyName() == Person.DOB_PROPERTY) dobField.setText((String) e.getNewValue());
		if(e.getPropertyName() == Person.EMAIL_PROPERTY) emailField.setText((String) e.getNewValue());
		if(e.getPropertyName() == Person.HEIGHT_PROPERTY) slider.setValue((Integer) e.getNewValue());
		if(e.getPropertyName() == Person.GENDER_PROPERTY) comboBox.setSelectedItem((Gender) e.getNewValue());
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
	
	protected void firePropertyChange(String propertyName, Object newValue) {
		pcs.firePropertyChange(propertyName, null, newValue);
	}

	class NameListener implements KeyListener {
		public void keyReleased(KeyEvent e) {
			firePropertyChange(Person.NAME_PROPERTY, ((JTextField)e.getComponent()).getText());
		}
		public void keyPressed(KeyEvent e) { }
		public void keyTyped(KeyEvent e) {}
	}
	
	class EmailListener implements KeyListener {
		public void keyReleased(KeyEvent e) {
			firePropertyChange(Person.EMAIL_PROPERTY, ((JTextField)e.getComponent()).getText());
		}
		public void keyPressed(KeyEvent e) { }
		public void keyTyped(KeyEvent e) {}
	}
	
	class DateOfBirthListener implements KeyListener {
		public void keyReleased(KeyEvent e) {
			firePropertyChange(Person.DOB_PROPERTY, ((JTextField)e.getComponent()).getText());
		}
		public void keyPressed(KeyEvent e) { }
		public void keyTyped(KeyEvent e) {}
	}

	class GenderListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			firePropertyChange(Person.GENDER_PROPERTY, (Gender)((JComboBox)e.getSource()).getSelectedItem());
		}
	}
	
	class HeightListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			firePropertyChange(Person.HEIGHT_PROPERTY, ((JSlider)e.getSource()).getValue());
		}
	}
}