package ui;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JOptionPane;

import core.Logger;


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
		MainView.closeAllDBConnections();
		System.exit(0);
	}

	public static void showDBConnectionErrorMessage() {
		while (MainView.getView() == null) {}; // Wait for GUI to open
		showMessageAndClose("Oops! The supplied credentials are wrong, or there is a problem with the database. \n Application will shutdown.");
	}	

	public static void showMessage(String message) {
		JOptionPane.showMessageDialog(MainView.getView().getFrame(), message);
	}

	public static void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(MainView.getView().getFrame(),message 
				,"DB ERROR", JOptionPane.ERROR_MESSAGE);
		Logger.writeErrorToLog(message);
	}

	public static String[] getCredentials() {
		while (MainView.getView() == null) {}; //wait for frame to open
		
		CredentialsView credentialView = new CredentialsView();

		String[] result = new String[4];
		while ((result[0] == null || result[1] == null || result[2] == null || result[3] == null) ||
				(result[0].isEmpty() || result[1].isEmpty() || result[2].isEmpty()|| result[3].isEmpty())) {

			int retCode = JOptionPane.showConfirmDialog(null, credentialView, 
					"Please Enter DB Connection Details", JOptionPane.OK_CANCEL_OPTION);
			if (retCode == JOptionPane.OK_OPTION) {
				result[0] = credentialView.getDbServerAddressField();
				result[1] = credentialView.getDbServerPortField();
				result[2] = credentialView.getUsername();
				result[3] = credentialView.getPassword();
			}
			else {
				break;
			}
		}
		return result;
	}

}
