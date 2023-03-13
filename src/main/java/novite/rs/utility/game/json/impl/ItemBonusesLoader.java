package novite.rs.utility.game.json.impl;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import novite.rs.utility.game.item.ItemBonus;
import novite.rs.utility.game.json.JsonLoader;

import com.google.gson.reflect.TypeToken;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 9, 2014
 */
public class ItemBonusesLoader extends JsonLoader<ItemBonus> {

	@Override
	public void initialize() {
		for (ItemBonus bonus : generateList()) {
			ITEM_BONUS_LIST.put(bonus.getItemId(), bonus);
		}
	}

	@Override
	public String getFileLocation() {
		return "data/json/itembonuses.json";
	}

	@Override
	protected List<ItemBonus> load() {
		List<ItemBonus> autospawns = null;
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
		autospawns = gson.fromJson(json, new TypeToken<List<ItemBonus>>() {
		}.getType());
		return autospawns;
	}

	/**
	 * Gets the bonuses of the item
	 * 
	 * @param itemId
	 *            The item to get the bonuses of
	 * @return
	 */
	public static final int[] getItemBonuses(int itemId) {
		ItemBonus bonus = ITEM_BONUS_LIST.get(itemId);
		if (bonus == null)
			return null;
		else {
			return bonus.getBonuses();
		}
	}

	private static final Map<Integer, ItemBonus> ITEM_BONUS_LIST = new HashMap<>();

}
