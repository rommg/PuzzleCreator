package gui;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JOptionPane;

class Utils {

	static void enableComponents(Container container, boolean enable) {
		Component[] components = container.getComponents();
		for (Component component : components) {
			component.setEnabled(enable);
			if (component instanceof Container) {
				enableComponents((Container)component, enable);
			}
		}
	}

	public static void showDBConnectionErrorMessage() {
		if (MainView.getView().getFrame() != null) // GUI is open at this stage
			JOptionPane.showMessageDialog(MainView.getView().getFrame(), "We're sorry, but A DB error occured. Please try again.");
	}


}
