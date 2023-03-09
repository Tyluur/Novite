package novite.rs.api.event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import novite.rs.api.event.EventListener.ClickOption;
import novite.rs.api.event.EventListener.EventType;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.utility.tools.FileClassLoader;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 20, 2014
 */
public class EventManager {

	/**
	 * Loads all event listeners
	 */
	public void load() {
		EVENTS_MAP.clear();
		for (Object clazz : FileClassLoader.getClassesInDirectory(EventManager.class.getPackage().getName() + ".listeners.interfaces")) {
			EventListener listener = (EventListener) clazz;
			for (int id : listener.getEventIds()) {
				register(new Event(id, EventType.INTERFACE), listener);
			}
		}
		for (Object clazz : FileClassLoader.getClassesInDirectory(EventManager.class.getPackage().getName() + ".listeners.objects")) {
			EventListener listener = (EventListener) clazz;
			for (int id : listener.getEventIds()) {
				register(new Event(id, EventType.OBJECT), listener);
			}
		}
		for (Object clazz : FileClassLoader.getClassesInDirectory(EventManager.class.getPackage().getName() + ".listeners.npc")) {
			EventListener listener = (EventListener) clazz;
			for (int id : listener.getEventIds()) {
				register(new Event(id, EventType.NPC), listener);
			}
		}
		for (Object clazz : FileClassLoader.getClassesInDirectory(EventManager.class.getPackage().getName() + ".listeners.items")) {
			EventListener listener = (EventListener) clazz;
			for (int id : listener.getEventIds()) {
				register(new Event(id, EventType.ITEM), listener);
			}
		}
	}

	/**
	 * Handles the action button clicked
	 *
	 * @param player
	 *            The player
	 * @param interfaceId
	 *            The interface id
	 * @param buttonId
	 *            The button id
	 * @param packetId
	 *            The packet id
	 * @param slotId
	 *            The slot id
	 * @param slotId2
	 *            The other slot id
	 * @return {@code True} if the action button got handled, {@code false} if
	 *         not.
	 */
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int slotId2) {
		EventListener eventListener = getEventListener(EventType.INTERFACE, interfaceId);
		if (eventListener != null && eventListener.handleButtonClick(player, interfaceId, buttonId, packetId, slotId, slotId2)) {
			return true;
		}
		return false;
	}

	/**
	 * Handles a game object option clicked.
	 *
	 * @param player
	 *            The player.
	 * @param objectId
	 *            The object id.
	 * @param gameObject
	 *            The game object instance.
	 * @param location
	 *            The location of the game object.
	 * @param option
	 *            The option clicked.
	 * @return {@code True} if the game object action got handled, {@code false}
	 *         if not.
	 * @see ClickOption
	 */
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		EventListener eventListener = getEventListener(EventType.OBJECT, objectId);
		if (eventListener != null && eventListener.handleObjectClick(player, objectId, worldObject, tile, option)) {
			return true;
		}
		return false;
	}

	/**
	 * Handles an npc clicked
	 *
	 * @param player
	 *            The player
	 * @param npc
	 *            The npc
	 * @param option
	 *            The option
	 * @return {@code True} if the npc action got handled, {@code false}
	 *         if not.
	 * @see ClickOption
	 */
	public boolean handleNPCClick(Player player, NPC npc, ClickOption option) {
		EventListener eventListener = getEventListener(EventType.NPC, npc.getId());
		if (eventListener != null && eventListener.handleNPCClick(player, npc, option)) {
			return true;
		}
		return false;
	}

	/**
	 * Handles an item clicked
	 *
	 * @param player
	 *            The player
	 * @param item
	 *            The item
	 * @param option
	 *            The option
	 * @return
	 */
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		EventListener eventListener = getEventListener(EventType.ITEM, item.getId());
		if (eventListener != null && eventListener.handleItemClick(player, item, option)) {
			return true;
		}
		return false;
	}

	/**
	 * Registers the event
	 *
	 * @param event
	 *            The event
	 * @param listener
	 *            The listener
	 * @return
	 */
	public void register(Event event, EventListener listener) {
		Iterator<Entry<Event, EventListener>> it = EVENTS_MAP.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Event, EventListener> entry = it.next();
			if (entry.getKey().equals(event)) {
				for (int bind : entry.getValue().getEventIds()) {
					for (int listenerBind : listener.getEventIds()) {
						if (listenerBind == bind) {
							throw new IllegalStateException("There is already an event registered![" + event + "]");
						}
					}
				}
			}
		}
		EVENTS_MAP.put(event, listener);
	}

	/**
	 * @return the manager
	 */
	public static EventManager get() {
		return MANAGER;
	}

	/**
	 * The instance of this class
	 */
	private static final EventManager MANAGER = new EventManager();

	/**
	 * Gets the event listener
	 *
	 * @param type
	 *            The type of event listener
	 * @param id
	 *            The id of event listener
	 * @return
	 */
	public EventListener getEventListener(EventType type, int id) {
		Event e = new Event(id, type);
		Iterator<Entry<Event, EventListener>> it = EVENTS_MAP.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Event, EventListener> entry = it.next();
			if (entry.getKey().equals(e)) {
				return entry.getValue();
			}
		}
		return null;
	}

	private static final Map<Event, EventListener> EVENTS_MAP = new HashMap<Event, EventListener>();

	/**
	 * The event class
	 *
	 * @author Tyluur
	 */
	public static class Event {

		public Event(int id, EventType type) {
			this.id = id;
			this.type = type;
		}

		/**
		 * The event id
		 */
		private final int id;

		/**
		 * The type of event
		 */
		private final EventType type;

		@Override
		public boolean equals(Object object) {
			if (object instanceof Event) {
				Event o = (Event) object;
				if (o.getId() == getId() && o.getType() == getType()) {
					return true;
				}
			}
			return false;
		}

		/**
		 * @return the type
		 */
		public EventType getType() {
			return type;
		}

		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}
	}

}
