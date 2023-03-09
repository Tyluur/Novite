package novite.rs.api.event.listeners.objects;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.minigames.games.MainGameHandler;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 14, 2014
 */
public class NoviteGamesHandler extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 14315, 14314 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		if (objectId == 14315) { // the tile to enter with
			MainGameHandler.get().enterLobby(player);
		} else if (objectId == 14314) {
			MainGameHandler.get().removeLobby(player);
		}
		return true;
	}

	@Override
	public boolean handleNPCClick(Player player, NPC npc, ClickOption option) {
		return false;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		return false;
	}

}