package ui;

import java.awt.Component;
import java.awt.Container;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import core.Logger;
import core.PuzzleCreator;




public class Utils {

	static void enableComponents(Container container, boolean enable) {
		Component[] components = container.getComponents();
		for (Component component : components) {
			component.setEnabled(enable);
			if (component instanceof Container) {
				enableComponents((Container)component, enable);
			}
		}
	}

	public static void showMessageAndClose(String message) {
		showMessage(message);
		PuzzleCreator.closeAllDBConnections();
		System.exit(0);
	}

	public static void showDBConnectionErrorMessage() {
		while (MainView.getView() == null) {}; // Wait for GUI to open
		showMessageAndRestart("Oops! The supplied credentials are wrong, or there is a problem with the database. \n Application will shutdown.");
	}	

	public static void showMessageAndRestart(String message) {
		showMessage(message);
		//restart application
		MainView.getView().Dispose();
		MainView.start();
	}

	public static void showMessage(String message) {
		JOptionPane.showMessageDialog(MainView.getView().getFrame(), message);
	}

	public static void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(MainView.getView().getFrame(),message 
				,"DB ERROR", JOptionPane.ERROR_MESSAGE);
		Logger.writeErrorToLog(message);
	}

	public static String getPassword() {
		while (MainView.getView() == null) {}; //wait for frame to open
		String code = null;
		while (code == null || code.isEmpty()) {
			code = JOptionPane.showInputDialog(MainView.getView().getFrame(), 
					"Schema: " + PuzzleCreator.schemaName +"\n" +
							"User: " + PuzzleCreator.username +"\n" +
							"Enter database password to continue:",
							"User password needed", 
							JOptionPane.WARNING_MESSAGE);
		}
		return code;
	}

	public static String getAppDir() {
		while (MainView.getView() == null) {}; //wait for frame to open
		int code = -1;
		File selectedFile = null;
		while (code != JFileChooser.APPROVE_OPTION) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setDialogTitle("Choose the application directory");
			code = chooser.showOpenDialog(MainView.getView().getFrame());
			if (code == JFileChooser.APPROVE_OPTION) {
			}
			selectedFile= chooser.getSelectedFile();
		}
		return selectedFile.getPath();
	}
}
