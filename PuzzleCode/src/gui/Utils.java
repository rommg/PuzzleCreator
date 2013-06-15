package gui;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JOptionPane;

import utils.Logger;

import main.PuzzleCreator;

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
		showMessageAndRestart("Oops! It looks like there is a problem with the database. Application will shutdown.");
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
}
