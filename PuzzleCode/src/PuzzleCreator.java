import gui.MainView;
import gui.MainView;
import puzzleAlgorithm.*;
import massiveImport.*;
import Utils.*;
public class PuzzleCreator {

	public static String appDir = ""; 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger.initialize(false);
		if (args.length != 1){
			Logger.writeErrorToLog("Wrong number of arguments");
			return;
		}
		appDir = args[0];
		MainView.startGUI();
		//MassiveImporter.runMassiveImporter();
		//AlgorithmRunner.runAlgorithm();
		//GuiAlgorithmConnector guiAlConnect = new GuiAlgorithmConnector();

	}

}
