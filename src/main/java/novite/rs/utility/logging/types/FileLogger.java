package novite.rs.utility.logging.types;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import novite.rs.Constants;

/**
 * Logs key events of the game into specified files and folders.
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Feb 16, 2013
 */
public class FileLogger {

	/** The instance of this class */
	private final static FileLogger SINGLETON = new FileLogger(Constants.FILES_PATH + "logs/");

	/** The folder location for the logs */
	private final String location;

	/**
	 * The constructor for this class.
	 *
	 * @param fileLocation
	 */
	public FileLogger(String location) {
		this.location = location;
	}

	/** Get an instance of this class */
	public static FileLogger getFileLogger() {
		return SINGLETON;
	}

	/**
	 * Writes the data to a file.
	 */
	@SuppressWarnings("deprecation")
	public void writeLog(String place, String text, boolean append) {
		place = place + "" + DateFormatSymbols.getInstance().getMonths()[new Date().getMonth()] + "/" + new Date().getDate() + ".txt";
		File writeFile = new File(location + place);
		if (!writeFile.getParentFile().exists()) {
			writeFile.getParentFile().mkdirs();
		}
		try {
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(location + place, append));
			fileWriter.write("[" + new Date().toLocaleString() + "] " + text.toString());
			fileWriter.newLine();
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void writeDropboxLog(String place, String text, boolean append) {
		place = place + "" + DateFormatSymbols.getInstance().getMonths()[new Date().getMonth()] + "/" + new Date().getDate() + ".txt";
		File writeFile = new File("data/logs/" + place);
		if (!writeFile.getParentFile().exists()) {
			writeFile.getParentFile().mkdirs();
		}
		try {
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter("data/logs/" + place, append));
			fileWriter.write("[" + new Date().toLocaleString() + "] " + text.toString());
			fileWriter.newLine();
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will get the text in the game logging folder for the file
	 * @param file The file that we will read from the log folder
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public List<String> getFileText(String file) {
		file = file + "" + DateFormatSymbols.getInstance().getMonths()[new Date().getMonth()] + "/" + new Date().getDate() + ".txt";
		List<String> text = new ArrayList<String>();
		File writeFile = new File(location + file);
		if (!writeFile.exists()) {
			return text;
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(location + file));
			String line;
			while ((line = reader.readLine()) != null) {
				text.add(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}

	/**
	 * Gets the location of the folder.
	 *
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

}
