package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.ImageIcon;
import javax.swing.ButtonGroup;

import massiveImport.YagoFileHandler;


public class MassiveImportView extends JPanel {

	File file = null;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JButton btnOpen;
	private JPanel chooseFilePanel;
	private JButton btnStartImport;
	private JLabel success;


	static MassiveImportView start() {
		return new MassiveImportView();
	}
	/**
	 * Create the panel.
	 */
	private MassiveImportView() {
		setLayout(new BorderLayout(0, 0));

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(2, 1));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, ".TSV Source", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		topPanel.add(panel);

		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel.add(panel_2);

		JRadioButton downloadCheckBox = new JRadioButton("Download Files From Yago Website");
		downloadCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnStartImport.setEnabled(true);
			}
		});
		buttonGroup.add(downloadCheckBox);
		panel_2.add(downloadCheckBox);
		downloadCheckBox.setHorizontalAlignment(SwingConstants.LEFT);

		chooseFilePanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) chooseFilePanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(chooseFilePanel);

		JRadioButton folderCheckBox = new JRadioButton("Choose Folder...");
		folderCheckBox.setSelected(true);
		folderCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					btnOpen.setEnabled(true);
					btnStartImport.setEnabled(false);
					if (success != null)
						success.setEnabled(true);
				}
				else { 
					btnOpen.setEnabled(false);
					btnStartImport.setEnabled(false);

					if (success != null){
						success.setEnabled(false);
					}
				}
			}
		});

		buttonGroup.add(folderCheckBox);
		chooseFilePanel.add(folderCheckBox);

		btnOpen = new JButton("");
		btnOpen.setIcon(new ImageIcon(MassiveImportView.class.getResource("/resources/open.png")));
		btnOpen.addActionListener(new ActionListener() {


			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Create a file chooser
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(MassiveImportView.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) 
					file = fc.getSelectedFile();

				if (success != null)
					chooseFilePanel.remove(success);
				success = new JLabel();
				chooseFilePanel.add(success);

				if (YagoFileHandler.containsFiles(file)) {
					btnStartImport.setEnabled(true);
					success.setIcon(new ImageIcon(MassiveImportView.class.getResource("/resources/check_small.png")));
				}
				else 
					success.setIcon(new ImageIcon(MassiveImportView.class.getResource("/resources/fail_small.png")));
				chooseFilePanel.revalidate();
			}
		});
		chooseFilePanel.add(btnOpen);

		JPanel btnPanel = new JPanel();
		btnStartImport = new JButton("Start Import");
		btnStartImport.setEnabled(false);
		btnPanel.add(btnStartImport);
		topPanel.add(btnPanel);

		add(topPanel, BorderLayout.NORTH);


	}



}
