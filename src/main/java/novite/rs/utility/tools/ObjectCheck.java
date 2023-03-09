package novite.rs.utility.tools;

import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.cache.loaders.ObjectDefinitions;
import novite.rs.utility.Utils;

public class ObjectCheck {

	public static void main(String[] args) throws IOException {
		Cache.init();
		for (int i = 0; i < Utils.getObjectDefinitionsSize(); i++) {
			ObjectDefinitions def = ObjectDefinitions.getObjectDefinitions(i);
			if (def.name.equalsIgnoreCase("Obelisk")) {
				System.out.println(def.id + " - " + def.name);
			}
		}
	}

}
