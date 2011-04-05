package fp.client;

import fp.client.gui.Gui;


public class Client {
	public static Gui gui;
   /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                gui = new Gui();
                gui.setVisible(true);
            }
        });
    }
}