package novite.rs.utility.game.json.impl;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import novite.rs.utility.game.json.JsonLoader;
import novite.rs.utility.game.npc.NPCBonus;

import com.google.gson.reflect.TypeToken;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 21, 2014
 */
public class NPCBonuses extends JsonLoader<NPCBonus> {

	@Override
	public void initialize() {
		List<NPCBonus> list = load();
		for (NPCBonus clazz : list) {
			BONUS_LIST.add(clazz);
		}
	}

	/**
	 * Gets the bonuses of an npc from its id
	 * @param id The npc id
	 * @return
	 */
	public static int[] getBonuses(int id) {
		for (NPCBonus clazz : BONUS_LIST) {
			if (clazz.getId() == id) {
				return clazz.getBonuses();
			}
		}
		return null;
	}

	@Override
	public String getFileLocation() {
		return "data/json/npcbonuses.json";
	}

	@Override
	protected List<NPCBonus> load() {
		List<NPCBonus> autospawns = null;
		String json = null;
		try {
			File file = new File(getFileLocation());
			if (!file.exists()) {
				return null;
			}
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			json = new String(chars);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		autospawns = gson.fromJson(json, new TypeToken<List<NPCBonus>>() {
		}.getType());
		return autospawns;
	}

	/**
	 * The list populated from the initialization
	 *
	 * @see {@link #initialize()}
	 */
	private static final List<NPCBonus> BONUS_LIST = new ArrayList<>();

}
