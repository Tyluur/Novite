package novite.rs.utility.game.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import novite.rs.utility.game.json.impl.ExchangeItemLoader;
import novite.rs.utility.game.json.impl.ExchangePriceLoader;
import novite.rs.utility.game.json.impl.ItemBonusesLoader;
import novite.rs.utility.game.json.impl.NPCAutoSpawn;
import novite.rs.utility.game.json.impl.NPCBonuses;
import novite.rs.utility.game.json.impl.NPCDropManager;
import novite.rs.utility.game.json.impl.ObjectSpawnLoader;
import novite.rs.utility.game.json.impl.PunishmentLoader;
import novite.rs.utility.game.json.impl.ShopsLoader;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 1, 2014
 */
public class JsonHandler {

	/**
	 * Initializes all json loaders
	 */
	public static void initialize() {
		try {
			addJsonLoaders();
		} catch (InstantiationException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		try {
			for (JsonLoader<?> loader : CLASSES) {
				loader.initialize();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOADED = Boolean.TRUE;
	}

	/**
	 * Waits for the json loaders to be loaded
	 */
	public static void waitForLoad() {
		while (!LOADED) {
			System.out.flush();
		}
	}

	/**
	 * Adds all json loaders to the map
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void addJsonLoaders() throws InstantiationException, IllegalAccessException {
		CLASSES.add(ShopsLoader.class.newInstance());
		CLASSES.add(NPCAutoSpawn.class.newInstance());
		CLASSES.add(ObjectSpawnLoader.class.newInstance());
		CLASSES.add(NPCBonuses.class.newInstance());
		CLASSES.add(PunishmentLoader.class.newInstance());
		CLASSES.add(NPCDropManager.class.newInstance());
		CLASSES.add(ExchangePriceLoader.class.newInstance());
		CLASSES.add(ExchangeItemLoader.class.newInstance());
		CLASSES.add(ItemBonusesLoader.class.newInstance());
	}

	/**
	 * Gets a {@link #JsonLoader} by the class
	 * 
	 * @param <T>
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getJsonLoader(Class<?> clazz) {
		try {
			JsonLoader<?> loader = CACHED_LOADERS.get(clazz.getSimpleName());
			if (loader != null) {
				return (T) loader;
			} else {
				for (JsonLoader<?> listLoader : CLASSES) {
					if (listLoader.getClass().getSimpleName().equals(clazz.getSimpleName())) {
						CACHED_LOADERS.put(listLoader.getClass().getSimpleName(), listLoader);
						return (T) listLoader;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * If all loaders have loaded
	 */
	public static boolean LOADED = Boolean.FALSE;

	/** The cached loaders */
	private static Map<String, JsonLoader<?>> CACHED_LOADERS = new HashMap<String, JsonLoader<?>>();

	/**
	 * Adds all of the loaders to the map
	 */
	private static final List<JsonLoader<?>> CLASSES = new ArrayList<JsonLoader<?>>();
}
