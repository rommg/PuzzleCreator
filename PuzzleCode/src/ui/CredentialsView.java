package ui;

import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


import core.PuzzleCreator;

@SuppressWarnings("serial")
public class CredentialsView extends JPanel {

	private JTextField dbServerAddressField;
	private JFormattedTextField dbServerPortField;
	private JTextField username;
	private JPasswordField password;



	/**
	 * Create the panel.
	 */
	public CredentialsView() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		dbServerAddressField = new LimitedTextField(25);
		dbServerAddressField.setText(PuzzleCreator.dbServerAddress);
		dbServerAddressField.selectAll();
		dbServerAddressField.requestFocusInWindow();

		dbServerPortField =  new JFormattedTextField("####");
		dbServerPortField.setText(PuzzleCreator.dbServerPort);
		dbServerPortField.selectAll();

		username = new LimitedTextField(15);
		username.setText(PuzzleCreator.username);
		username.requestFocusInWindow();
		username.selectAll();

		password = new JPasswordField(10);
		password.setText(PuzzleCreator.password);
		password.selectAll();
		
		add(new JLabel("DB Server Address:"));
		add(dbServerAddressField);
		add(new JLabel("DB Server Port:"));
		add(dbServerPortField);
		add(new JLabel("Username:"));
		add(username);
		add(new JLabel("Password:"));
		add(password);
		
	}



	String getDbServerAddressField() {
		return dbServerAddressField.getText();
	}



	String getDbServerPortField() {
		return dbServerPortField.getText();
	}



	String getUsername() {
		return username.getText();
	}



	String getPassword() {
		return String.valueOf(password.getPassword());
	}
}
