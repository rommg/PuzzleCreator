package ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import javax.swing.border.TitledBorder;
import javax.swing.ImageIcon;
import javax.swing.ButtonGroup;
import parsing.YagoFileHandler;
import core.Logger;
import core.PuzzleCreator;


@SuppressWarnings("serial")
public class MassiveImportView extends JPanel {

	File file = null;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JButton btnOpen;
	private JPanel chooseFilePanel;
	private JButton btnStartImport;
	private JLabel success;
	private JPanel progressPanel;
	private JButton stopButton;
	private ImportWorker worker;
	private JRadioButton downloadCheckBox;


	static MassiveImportView start() {
		return new MassiveImportView();
	}
	/**
	 * Create the panel.
	 */
	private MassiveImportView() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setBorder(new TitledBorder(null, ".TSV Source", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setLayout(new GridLayout(3, 1, 0, 0));

		JPanel downloadPanel = new JPanel();
		FlowLayout fl_downloadPanel = (FlowLayout) downloadPanel.getLayout();
		fl_downloadPanel.setAlignment(FlowLayout.LEFT);
		panel.add(downloadPanel);

		downloadCheckBox = new JRadioButton("Download Files From Yago Website");
		downloadCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnStartImport.setEnabled(true);
			}
		});
		buttonGroup.add(downloadCheckBox);
		downloadPanel.add(downloadCheckBox);
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
		btnOpen.setIcon(new ImageIcon(getClass().getClassLoader().getResource("resources/open.png")));
		btnOpen.addActionListener(new ActionListener() {


			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Create a file chooser
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(PuzzleCreator.homeDir));
				fc.setDialogTitle("Choose A Directory With TSV Files");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(MassiveImportView.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) 
					file = fc.getSelectedFile();

				if (success != null)
					chooseFilePanel.remove(success);
				success = new JLabel();
				chooseFilePanel.add(success);

				if (file == null) // no directory chosen
					return;

				if (YagoFileHandler.containsFiles(file)) {
					btnStartImport.setEnabled(true);
					success.setIcon(new ImageIcon(getClass().getClassLoader().getResource("resources/check_small.png")));
				}
				else 
					success.setIcon(new ImageIcon(getClass().getClassLoader().getResource("resources/fail_small.png")));
				chooseFilePanel.revalidate();
			}
		});
		chooseFilePanel.add(btnOpen);

		JPanel btnPanel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) btnPanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel.add(btnPanel);
		btnStartImport = new JButton("Start Import");
		btnStartImport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//default icon, custom title
				int n = JOptionPane.showConfirmDialog(
						MainView.getView().getFrame(),
						"Massive import will erase all old data. Would you like to proceed?",
						"Erase warning",
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					if (downloadCheckBox.isSelected())
						startImportBtnClicked(true);
					else 
						startImportBtnClicked(false);
				}
			}
		});

		btnStartImport.setEnabled(false);
		btnPanel.add(btnStartImport);

		stopButton = new JButton("Stop Import");
		stopButton.setEnabled(false);
		stopButton.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (worker != null) {
					worker.cancel(true);
					setProgressMessage("Cancelled!");
					MassiveImportView.this.stopButton.setEnabled(false);
					MassiveImportView.this.btnStartImport.setEnabled(false);
				}
			}
		});
		btnPanel.add(stopButton);

		progressPanel = new JPanel();
		add(progressPanel, BorderLayout.CENTER);
		progressPanel.setBorder(new TitledBorder(null, "Progress", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
	}

	private void startImportBtnClicked(boolean download) {
		progressPanel.removeAll();
		progressPanel.revalidate();
		progressPanel.repaint();
		stopButton.setEnabled(true);

		worker = new ImportWorker(file, download);
		worker.execute();
	}

	void setProgressMessage(String message) {
		progressPanel.add(new JLabel(message));
		progressPanel.revalidate();
	}

	private class ImportWorker extends SwingWorker<Void,String> {

		File directory;
		boolean download;

		public ImportWorker(File directory, boolean download) {
			this.directory = directory;
			this.download = download;
		}

		@Override
		protected Void doInBackground() throws Exception {

			Logger.writeToLog("Starting importing process...");
			publish("Starting importing process...");

			YagoFileHandler y = new YagoFileHandler(directory);

			if (download) {
				Logger.writeToLog("Downloading and extracting yago files from website...");
				publish("Downloading and extracting yago files from website...");
				y.getFilesFromURL(); // download yago files 
			}

			Logger.writeToLog("Filtering TSV files...");
			publish("Filtering TSV files...");

			y.createFilteredYagoFiles(); // create TSVs with relevant data only

			Logger.writeToLog("Deleteing old data...");
			publish("Deleting old data...");

			try {
				y.cleanDataTables(); // clean tables with old data
			}
			catch (SQLException exception){
				publish("ERROR while cleaning old tables.");
				return null;
			}
			Logger.writeToLog("Importing TSV files to DB...");
			publish("Importing TSV files to DB...");

			try {
				y.importFilesToDB();
				//TODO: change 
			}
			catch (SQLException e) {
				publish("ERROR while loading filtered TSV File to DB!");
				return null;
			}

			Logger.writeToLog("Populating DB...");
			publish("Populating DB...");

			try {
				y.populateDB();
			}
			catch (SQLException e) {
				publish("ERROR while populating DB!");
				return null;
			}

			Logger.writeToLog("Deleting created files...");
			publish("Deleting created files...");
			//y.deleteAllYagoFiles(); // delete all temporary files and folders

			Logger.writeToLog("Finished!");
			publish("Finished!");

			return null;
		}

		@Override
		public void process(List<String> messages){
			for (String message : messages)
				MassiveImportView.this.setProgressMessage(message);
		}

		@Override
		protected void done() {
			MassiveImportView.this.stopButton.setEnabled(false);
			MassiveImportView.this.btnStartImport.setEnabled(false);
			this.cancel(true);
		}

	}
}

