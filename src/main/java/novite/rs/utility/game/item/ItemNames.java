package novite.rs.utility.game.item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.item.Item;
import novite.rs.game.item.ItemConstants;
import novite.rs.utility.Utils;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since 2012-11-15
 */
public class ItemNames {

	private static Map<Integer, String> NAMES = new HashMap<Integer, String>();

	public static void loadNames() {
		for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
			NAMES.put(i, ItemDefinitions.getItemDefinitions(i).getName());
		}
	}

	public static int getId(String name) {
		Iterator<Entry<Integer, String>> it = NAMES.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, String> entry = it.next();
			int entryId = entry.getKey();
			String entryName = entry.getValue();
			if (entryName.equalsIgnoreCase(name)) {
				return entryId;
			}
		}
		return -1;
	}

	public static int getTradeableId(String name) {
		for (int i = 0; i < NAMES.size(); i++) {
			if (!ItemConstants.isTradeable(new Item(i))) {
				continue;
			}
			ItemDefinitions definitions = ItemDefinitions.getItemDefinitions(i);
			if (definitions.getName().toLowerCase().contains(name.toLowerCase())) {
				return i;
			}
		}
		return -1;
	}

	public static int forceCacheId(String name) {
		for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
			ItemDefinitions def = ItemDefinitions.getItemDefinitions(i);
			if (def.name.equalsIgnoreCase(name)) {
				return def.id;
			}
		}
		return -1;
	}

}
