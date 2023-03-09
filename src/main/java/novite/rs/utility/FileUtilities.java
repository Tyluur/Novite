package novite.rs.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 'Mystic Flow
 */
public class FileUtilities {

	public static final int BUFFER = 1024;

	public static boolean exists(String name) {
		File file = new File(name);
		return file.exists();
	}

	public static ByteBuffer fileBuffer(String name) throws IOException {
		File file = new File(name);
		if (!file.exists()) {
			return null;
		}
		FileInputStream in = new FileInputStream(name);

		byte[] data = new byte[BUFFER];
		int read;
		try {
			ByteBuffer buffer = ByteBuffer.allocate(in.available() + 1);
			while ((read = in.read(data, 0, BUFFER)) != -1) {
				buffer.put(data, 0, read);
			}
			buffer.flip();
			return buffer;
		} finally {
			if (in != null) {
				in.close();
			}
			in = null;
		}
	}

	public static void writeBufferToFile(String name, ByteBuffer buffer) throws IOException {
		File file = new File(name);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(name);
		out.write(buffer.array(), 0, buffer.remaining());
		out.flush();
		out.close();
	}
	

	public static List<String> getFileText(String file) {
		List<String> text = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.equals("") || line.equals(" "))
					continue;
				text.add(line);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;
	}

	/**
	 * Gets the html code in a page.
	 * 
	 * @param page
	 *            The page link e.g www.google.com
	 * @return A {@code List} {@code Object}
	 */
	public static ArrayList<String> getPageSource(String page) throws IOException {
		ArrayList<String> text = new ArrayList<String>();
		URL url = new URL(page);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		connection.setRequestProperty("User-Agent", "Mozilla Firefox");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		InputStream input;
		if (connection.getResponseCode() >= 400) {
			input = connection.getErrorStream();
		} else {
			input = connection.getInputStream();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line;
		while ((line = reader.readLine()) != null) {
			text.add(line);
		}
		reader.close();
		return text;
	}


	public static LinkedList<String> readFile(String directory) throws IOException {
		LinkedList<String> fileLines = new LinkedList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(directory));
			String string;
			while ((string = reader.readLine()) != null) {
				fileLines.add(string);
			}
		} finally {
			if (reader != null) {
				reader.close();
				reader = null;
			}
		}
		return fileLines;
	}

}