package novite.rs.utility.game.item;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.utility.Config;
import novite.rs.utility.Utils;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 19, 2014
 */
public class UntradeablesFinder {
	
	public static void main(String... args) {
		Config.get().load();
		try {
			Cache.init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("data/items/untradeable_list.txt"), true));
			for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
				ItemDefinitions def = ItemDefinitions.getItemDefinitions(i);
				if (!def.isTradeable()) {
					writer.write(def.getName() + " is untradeable [" + i + "]\n");
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
