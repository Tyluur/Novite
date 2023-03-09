package novite.rs.utility.game.npc;

import java.util.HashMap;
import java.util.Map;

import novite.rs.Constants;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Jul 28, 2013
 */
public enum NPCNames {

	LUMBRIDGE_SAGE(2244, Constants.SERVER_NAME + " Advisor"),
	SURGEON_GENERAL_TAFANI(961, Constants.SERVER_NAME + " Nurse"),
	GYPSY_ARIS(3385, "Gloves Gypsy"),
	THOK(13280, "RuneSlayer Master");

	private final int[] id;
	private final String name;

	NPCNames(int id, String name) {
		this.id = new int[] { id };
		this.name = name;
	}

	NPCNames(int[] id, String name) {
		this.id = id;
		this.name = name;
	}

	private static final Map<Integer, NPCNames> map = new HashMap<Integer, NPCNames>();

	static {
		for (NPCNames names : NPCNames.values()) {
			for (int element : names.id) {
				map.put(element, names);
			}
		}
	}

	public static String getName(int id) {
		return map.get(id) != null ? map.get(id).name : null;
	}

}