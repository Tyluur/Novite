package novite.rs.cache.scripts;

import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.utility.Utils;

import com.alex.loaders.items.ItemDefinitions;
import com.alex.store.Store;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 18, 2014
 */
public class ItemNameChanger {

	public static void main(String... args) throws IOException {
		Cache.init();
		Store ours = new Store("./data/cache/");
		for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
			for (Object[] element : OBJECTS) {
				if (i == (int) element[0]) {
					ItemDefinitions definitions = ItemDefinitions.getItemDefinition(ours, i);
					String prevName = definitions.getName();
					definitions.setName((String) element[1]);
					System.out.println("Set the name of " + i + "[ " + prevName + "] to " + definitions.getName());
					definitions.write(ours, true);
				}
			}
		}
	}
	
	/*607 - Rigour Scroll
	608 - Augury Scroll
	6769 - SoulSplit Scroll
	786 - Turmoil Scroll*/

	private static final Object[][] OBJECTS = new Object[][] {
		{ 7237, "Rewards Casket" }
	};

}
