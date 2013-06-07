package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import javax.swing.border.TitledBorder;

public class MassiveImportView extends JPanel {

	/**
	 * Create the panel.
	 */
	public MassiveImportView() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, ".TSV Source", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel.add(panel_2);
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Download Files From Yago Website");
		panel_2.add(rdbtnNewRadioButton);
		rdbtnNewRadioButton.setHorizontalAlignment(SwingConstants.LEFT);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(panel_1);
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Choose Folder...");
		panel_1.add(rdbtnNewRadioButton_1);
		
		JPanel panel_4 = new JPanel();
		add(panel_4, BorderLayout.SOUTH);
		
		JButton btnStartImport = new JButton("Start Import");
		panel_4.add(btnStartImport);

	}

}
