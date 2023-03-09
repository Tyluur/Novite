package novite.rs.game.player.content.randoms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;
import novite.rs.utility.tools.FileClassLoader;

/**
 * 
 * @author Tyluur <itstyluur@gmail.com>
 * @since Nov 26, 2013
 */
public class RandomEventManager {

	/**
	 * Populates the random event map
	 */
	public void initialize() {
		for (Object clazz : FileClassLoader.getClassesInDirectory(RandomEventManager.class.getPackage().getName() + ".impl")) {
			RandomEvent event = (RandomEvent) clazz;
			randomEvents.put(event.getName(), event);
		}
	}

	/**
	 * Handles the interaction with an npc in the event class
	 * 
	 * @param player
	 * @param npc
	 * @return
	 */
	public boolean handleNPCInteraction(Player player, NPC npc) {
		RandomEvent event = player.getCurrentRandomEvent();
		if (event != null) {
			return event.handleNPCInteraction(player, npc);
		}
		return false;
	}

	/**
	 * Handles the interaction with an interface in the random event
	 * 
	 * @param player
	 * @param interfaceId
	 * @param buttonId
	 * @return
	 */
	public boolean handleInterfaceInteraction(Player player, int interfaceId, int buttonId) {
		RandomEvent event = player.getCurrentRandomEvent();
		if (event != null) {
			return event.handleInterfaceInteraction(player, interfaceId, buttonId);
		}
		return false;
	}

	/**
	 * Selects a random event from the map and starts it for the player
	 * 
	 * @param player
	 *            The player to start the event
	 */
	public void startRandomEvent(Player player) {
		if (!player.controlerAvailable())
			return;
		List<String> keys = new ArrayList<String>(randomEvents.keySet());
		RandomEvent event = randomEvents.get(keys.get(Utils.random(keys.size())));
		if (event != null) {
			if (player.getCurrentRandomEvent() != null) {
				player.getCurrentRandomEvent().forceDispose(player);
			}
			player.setCurrentRandomEvent(event);
			player.getCurrentRandomEvent().initiate(player);
		} else {
			System.out.println(player + " attempted to start random event which was invalid:\t");
		}
	}

	/**
	 * Starts the random event for the player by the name of the random event
	 * 
	 * @param player
	 *            The player to start the random event for
	 * @param name
	 *            The name of the random event to start
	 */
	public void start(Player player, String name) {
		RandomEvent event = randomEvents.get(name);
		if (event != null) {
			player.setCurrentRandomEvent(event);
			player.getCurrentRandomEvent().initiate(player);
		} else {
			System.out.println(player + " attempted to start random event which was invalid:\t" + name);
		}
	}

	/**
	 * Starts the random event for a player with a random event paramater
	 * 
	 * @param player
	 *            The player to start the random event for
	 * @param event
	 *            The random event to start
	 */
	public void start(Player player, RandomEvent event) {
		player.setCurrentRandomEvent(event);
		player.getCurrentRandomEvent().initiate(player);
	}

	/**
	 * Stops the current random event the player is in
	 * 
	 * @param player
	 *            The player to stop the random event for
	 */
	public void stop(Player player) {
		if (player.getCurrentRandomEvent() != null) {
			player.getCurrentRandomEvent().dispose(player);
		}
	}

	/**
	 * The getter
	 * 
	 * @return
	 */
	public static RandomEventManager get() {
		return INSTANCE;
	}

	/**
	 * The map of random events, which are found by the name of the event.
	 */
	private Map<String, RandomEvent> randomEvents = new HashMap<String, RandomEvent>();

	/**
	 * The instance of the random event manager.
	 */
	private static final RandomEventManager INSTANCE = new RandomEventManager();

}
