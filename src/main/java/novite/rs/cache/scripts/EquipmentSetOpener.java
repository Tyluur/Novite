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
public class EquipmentSetOpener {

	public static void main(String... args) throws IOException {
		Cache.init();
		Store ours = new Store("./data/cache/");
		for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
			ItemDefinitions definitions = ItemDefinitions.getItemDefinition(ours, i);
			if (definitions.getName().contains(" set")) {
				System.out.println("Set the first option for " + definitions.getName() + "[" + i + "] to 'Open'");
				definitions.getInventoryOptions()[0] = "Open";
				definitions.write(ours, true);
			}
		}
	}

}
