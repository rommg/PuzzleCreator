package main;
import gui.MainView;
import gui.MainView;
import puzzleAlgorithm.*;
import massiveImport.*;
import Utils.*;
public class PuzzleCreator {

	/**
	 * appDir should end with file separator
	 */
	public static String appDir = ""; 
	public static String homeDir = System.getProperty("user.home");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1){
			Logger.writeErrorToLog("Wrong number of arguments");
			return;
		}
		appDir = args[0];
		
		Logger.initialize(false);
		MainView.start();
		//MassiveImporter.runMassiveImporter();
		//AlgorithmRunner.runAlgorithm();
		//GuiAlgorithmConnector guiAlConnect = new GuiAlgorithmConnector();

	}

}
