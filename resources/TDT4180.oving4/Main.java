package oving4;

import javax.swing.*;

public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		try {
		//	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		PersonListPanel panel = new PersonListPanel();
		DefaultListModel model = new DefaultListModel();
		panel.setModel(model);
		
		Person arne = new Person("Arne", "arne@stud.ntnu.no", "1. juli 1986", Gender.MALE, 175);
		Person lisa = new Person("Lisa", "lisa@stud.ntnu.no", "5. mai 1989", Gender.FEMALE, 165);		
		Person geir = new Person("Geir", "geir@stud.ntnu.no", "1. september 1989", Gender.MALE, 185);

		model.addElement(arne);
		model.addElement(lisa);
		model.addElement(geir);
		
		frame.setContentPane(panel);

		frame.setTitle("Øving 4");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(frame.getRootPane());
		frame.setResizable(false);
		frame.setVisible(true);
	}
}