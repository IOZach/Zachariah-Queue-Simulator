package simulationClasses;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class demonstrates the controller method, that kick off the application
 * to the screen. Call methods to setup the application elements and components.
 * Its the main method.
 * 
 * @author Zakaria Abushima
 * @version 1.0
 **/

public class Controller {
	
	/**
	 * This static methods setup application components and kick off the application
	 */
	private static void createAndShowGUI() {

		GUI ThePanel = new GUI(); // Create GUI Panel
		final JFrame frame = new JFrame(ThePanel.ApplicationTitle.getText()); // Create frame with title
		frame.getContentPane().setBackground(Color.white); // set Background of the frame to white.
		frame.getContentPane().add(ThePanel.ApplicationTitle, BorderLayout.NORTH); // add The header component to the frame, North.
		frame.getContentPane().add(ThePanel, BorderLayout.CENTER); // add all component of the Panel to the Center.
		frame.getContentPane().add(ThePanel.CopyRightsLBL, BorderLayout.SOUTH); // add copyrights Footer to the South.
		frame.setSize(1020, 637); // Determine Fixed size of frame.
		frame.setLocationRelativeTo(null); // centers the frame on screen.
		frame.setVisible(true); // set frame to be visible
		frame.setIconImage(ThePanel.mediaList.get(11)); // set icon for the application.
		frame.setResizable(false); // set the frame windows to unresizeable due to application quality.
		
		// Dispose or Exit Application Notification
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				int response = JOptionPane
						.showConfirmDialog(
								null,
								"Are you sure to exit the\nApplication?\n\nMake sure to save your work.",
								"Notification", JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE);
				if (response == JOptionPane.YES_OPTION) {
					frame.dispose();
					System.exit(0); // Close Application
				}
			}
		});

	}

	public static void main(String[] args) {

		// Schedule for the event-dispatching thread
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

	}

}
