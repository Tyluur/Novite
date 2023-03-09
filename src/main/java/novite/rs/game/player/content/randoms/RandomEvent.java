package novite.rs.game.player.content.randoms;

import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.randoms.impl.Dr_Jekyll;
import novite.rs.utility.Utils;

/**
 * 
 * @author Tyluur <itstyluur@gmail.com>
 * @since Nov 26, 2013
 */
public abstract class RandomEvent {

	/**
	 * The name of the random event
	 * 
	 * @return A {@code String} {@code Object}
	 */
	public abstract String getName();

	/**
	 * What happens when the random event starts.
	 */
	public abstract void initiate(Player player);

	/**
	 * What happens when the player is in the random event and it is processing
	 */
	public abstract void process(Player player);

	/**
	 * What happens when the random event has been completed, or the player logs
	 * out causing it to close.
	 */
	public void dispose(Player player) {
		player.setCurrentRandomEvent(null);
	}

	/**
	 * Forcefully gets rid of the current random event
	 * 
	 * @param player
	 */
	public void forceDispose(Player player) {
		if (player.getCurrentRandomEvent() instanceof Dr_Jekyll) {
			Dr_Jekyll event = (Dr_Jekyll) player.getCurrentRandomEvent();
			event.getNpc().finish();
		}
		player.setCurrentRandomEvent(null);
	}
	
	/**
	 * Handles the interaction with an npc
	 * 
	 * @param player
	 *            The player interacting with the npc
	 * @param npc
	 *            The npc being interacted with;
	 * @return
	 */
	public abstract boolean handleNPCInteraction(Player player, NPC npc);

	/**
	 * Handles the interaction with an interface in the random event
	 * 
	 * @param player
	 * @param interfaceId
	 * @param buttonId
	 * @return
	 */
	public abstract boolean handleInterfaceInteraction(Player player, int interfaceId, int buttonId);

	/**
	 * Finds a random tile to send the player to
	 * 
	 * @return A {@code WorldTile} {@code Object} that the player will be sent
	 *         to.
	 */
	public WorldTile getRandomPosition() {
		return tiles[Utils.random(tiles.length)];
	}

	/**
	 * The tiles the player can be teleported to if they do not respond to their
	 * random event
	 */
	private final WorldTile[] tiles = new WorldTile[] { new WorldTile(3052, 3491, 0) };

}
