package Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import main.PuzzleCreator;

public class Logger {

	public static String logFilename = "log.txt";
	public static String logFileDir = PuzzleCreator.appDir;
	private static File logFile;
	private static BufferedWriter logWriter;

	/**
	 * The method creates the log file.
	 * If deleteOld is true - the log file will create a new file and delete old log 
	 * 
	 * @return
	 */
	public static int initialize(boolean deleteOld) {
		logFile = new File(logFileDir, logFilename);
		boolean bRes;
		try {
			if (!logFile.getParentFile().exists())
				// create directory of logFile
				logFile.getParentFile().mkdirs();
			if (logFile.exists() && deleteOld) {
				logFile.delete();
			}

			logFile.createNewFile();
			bRes = writeToLog("****************************************************************************");
			bRes = writeToLog("****************************************************************************");
			bRes = writeToLog("Logger Initialized");
			if (!bRes)
				throw new IOException();
		} catch (IOException ex) {
			System.out.println("failed to create log file");
			return 0;
		}
		return 1;
	}

	/**
	 * This method appends the logString to the log file, with a time stamp
	 * before it. <br>
	 * The method appends a new line symbol at the end of the string
	 * 
	 * @param logString
	 * @return
	 */
	public static boolean writeToLog(String logString) {
		try {
			logWriter = new BufferedWriter(new FileWriter(logFile, true));
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date date = new Date();

			logWriter = new BufferedWriter(new FileWriter(logFile, true));
			logWriter.write((dateFormat.format(date)) + "  " + logString + System.getProperty("line.separator"));
			logWriter.close();
		} catch (IOException ex) {
			return false;
		}
		return true;
	}

	/**
	 * This method writes the error string to the log file, and addes an error
	 * prefix to it.
	 * 
	 * @param errorString
	 * @return
	 */
	public static boolean writeErrorToLog(String errorString) {
		errorString = "**********************  Error : " + errorString;
		return writeToLog(errorString);
	}
}
