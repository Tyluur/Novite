package novite.rs.utility.game.npc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since 2012-11-16
 */
public class Nonmoving {

	/**
	 * The list of ids of npcs that dont move
	 */
	private static final List<Integer> LIST = new ArrayList<Integer>();

	/**
	 * Finding out if the npc is forced to not move
	 * @param id The id to check
	 * @return
	 */
	public static boolean contained(int id) {
		return getList().contains(id);
	}

	/**
	 * Loads all non moving npcs
	 */
	public static void loadList() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("data/npcs/nonwalking.txt"));
			String line;
			while ((line = in.readLine()) != null) {
				int id = Integer.parseInt(line);
				getList().add(id);
			}
			in.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the list
	 */
	public static List<Integer> getList() {
		return LIST;
	}

}