package novite.rs.utility.tools;

import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.utility.Utils;

public class NPCCheck {

	public static void main(String[] args) throws IOException {
		Cache.init();
		for (int id = 0; id < Utils.getNPCDefinitionsSize(); id++) {
			NPCDefinitions def = NPCDefinitions.getNPCDefinitions(id);
			if (def.getName().contains("Seaman") || def.getName().contains("Captain ")) {
				System.out.println(id + " - " + def.getName());
			}
		}
	}

}
