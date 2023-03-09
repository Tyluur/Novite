package novite.rs.engine.process.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import novite.rs.engine.process.TimedProcess;
import novite.rs.utility.Saving;
import novite.rs.utility.logging.types.FileLogger;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 27, 2014
 */
public class BackupGeneratorProcess implements TimedProcess {

	@Override
	public Timer getTimer() {
		return new Timer(1, TimeUnit.MINUTES);
	}

	@Override
	public void execute() {
		try {
			long timeFromFile = getTimeFromFile();
			/**
			 * Checking to see if there should be a backup generated
			 */
			if (timeFromFile == -1 || TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - getTimeFromFile()) >= 24) {
				PrintWriter writer;
				backupCharacters();
				try {
					writer = new PrintWriter(file);
					writer.print(System.currentTimeMillis());
					writer.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				System.out.println("Generated a backup!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the long time from the file
	 *
	 * @return A {@code Long} {@code Object}
	 */
	private long getTimeFromFile() {
		long time = -1;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				time = Long.parseLong(line);
				break;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * Zips the directory to a .zip file
	 *
	 * @param f
	 *            The folder to zip
	 * @param zf
	 *            The zip file
	 * @throws IOException
	 *             Exception thrown
	 */
	private void zipDirectory(File f, File zf) throws IOException {
		ZipOutputStream z = new ZipOutputStream(new FileOutputStream(zf));
		zip(f, f, z);
		z.close();
	}

	@SuppressWarnings("deprecation")
	private void backupCharacters() {
		File characters = new File(Saving.PATH);
		String gucci = "" + DateFormatSymbols.getInstance().getMonths()[new Date().getMonth()] + "/" + new Date().getDate();
		File directory = new File(FileLogger.getFileLogger().getLocation() + "backups/" + gucci + "/archive.zip");
		if (!directory.getParentFile().exists()) {
			directory.getParentFile().mkdirs();
		}
		if (!directory.exists()) {
			try {
				if (characters.list().length == 0) {
					System.out.println("[Auto-Backup] The characters folder is empty.");
					return;
				}
				zipDirectory(characters, directory);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Zips a directory into an archive
	 *
	 * @param directory
	 *            The directory to zip to
	 * @param base
	 *            The base folder
	 * @param zos
	 *            The outputstream
	 * @throws IOException
	 *             Exception thrown
	 */
	private void zip(File directory, File base, ZipOutputStream zos) throws IOException {
		File[] files = directory.listFiles();
		byte[] buffer = new byte[8192];
		int read = 0;
		for (File file2 : files) {
			if (file2.isDirectory()) {
				zip(file2, base, zos);
			} else {
				FileInputStream in = new FileInputStream(file2);
				ZipEntry entry = new ZipEntry(file2.getPath().substring(base.getPath().length() + 1));
				zos.putNextEntry(entry);
				while (-1 != (read = in.read(buffer))) {
					zos.write(buffer, 0, read);
				}
				in.close();
			}
		}
	}

	private File file = new File(FileLogger.getFileLogger().getLocation() + "/backup.txt");
}
