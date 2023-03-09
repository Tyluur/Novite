package novite.rs.utility.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Test {

	public static HashMap<String, Integer> names = new HashMap<String, Integer>();

	public static void main(String[] args) throws Exception {
		File file = new File("C:\\Users\\Jonathan\\Desktop\\tradeables.txt");
		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(file));

		for (;;) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			lines.add(line);
		}
		reader.close();

		for (String line : lines) {
			try {
				if ((line.startsWith("<ul><li><a href=") || line.startsWith("<li><a href=")) && line.contains("title=")) {
					String itemName = removeHtml(line.substring(line.indexOf("\">") + 2, line.indexOf("</a>")));
					names.put(itemName, 0);
				}
			} catch (Exception e) {
				continue;
			}
		}
		write();
	}

	public static String removeHtml(String html) {
		return html.replaceAll("\\<.*?\\>", "");
	}

	public static void write() throws IOException {
		File file = new File("out.txt");
		file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for (String name : names.keySet()) {
			writer.write(name);
			writer.newLine();
			writer.flush();
		}
		writer.close();
	}
}
