package novite.rs.utility.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Jonathan
 *
 */
public class Dumper {

	public static void main(String[] args) {
		ArrayList<Integer> failed = new ArrayList<Integer>();
		for (int i = 1; i < 6000; i++) {
			try {
				URL url = new URL("http://services.runescape.com/m=itemdb_rs/bestiary/beastData.json?beastid=" + i);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				BufferedWriter out = new BufferedWriter(new FileWriter(new File("./beasts/" + i + ".txt")));
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String data = in.readLine();
				if (data == null || data.equals("") || data.length() == 0 || connection.getContentLength() == 0) {
					continue;
				}
				out.write(data);
				out.flush();
				out.close();
				System.out.println("Dumped " + i + " successfully!");
				Thread.sleep(1000L);
			} catch (Exception e) {
				failed.add(i);
				e.printStackTrace();
				continue;
			}
		}
		System.err.println(Arrays.toString(failed.toArray()));
		System.err.println("Failed " + failed.size() + " beasts");
	}
}