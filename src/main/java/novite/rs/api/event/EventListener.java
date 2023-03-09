package novite.rs.api.event;

import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;

/**
 * The event listener class that is extended in child classes. A child class
 * will override the {@link #handleButtonClick(Player, int, int, int, int, int)}
 * or
 * {@link #handleObjectClick(Player, int, WorldObject, WorldTile, ClickOption)}
 * to clean up code.
 *
 * @author Tyluur<itstyluur@gmail.com>
 */
public abstract class EventListener {

	/**
	 * The event ids that are associated with this class
	 *
	 * @return
	 */
	public abstract int[] getEventIds();

	/**
	 * The option types that can be clicked on an object
	 *
	 * @author Tyluur<itstyluur@gmail.com>
	 */
	public enum ClickOption {
		FIRST,
		SECOND,
		THIRD,
		FOURTH
	}

	/**
	 * The type of events that can be listened for
	 *
	 * @author Tyluur<itstyluur@gmail.com>
	 */
	public enum EventType {
		INTERFACE,
		OBJECT,
		NPC,
		ITEM
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
	 * @param itemId
	 *            The other slot id
	 * @return {@code True} if the action button got handled, {@code false} if
	 *         not.
	 */
	public abstract boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId);

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
	public abstract boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option);

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
	public abstract boolean handleNPCClick(Player player, NPC npc, ClickOption option);

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
	public abstract boolean handleItemClick(Player player, Item item, ClickOption option);

}
