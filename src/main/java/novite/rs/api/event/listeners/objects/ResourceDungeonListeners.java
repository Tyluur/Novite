package novite.rs.api.event.listeners.objects;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.Magic;
import novite.rs.utility.game.TeleportLocations;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Aug 1, 2014
 */
public class ResourceDungeonListeners extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 52866, 52855 } ;
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		if (objectId == 52866) {
			Magic.sendTeleportSpell(player, 13288, 13285, 2516, 2517, 0, 0, new WorldTile(3034, 9772, 0), 1, false, Magic.MAGIC_TELEPORT);
		} else {
			Magic.sendTeleportSpell(player, 13288, 13285, 2516, 2517, 0, 0, TeleportLocations.FALADOR_RESOURCE_DUNG, 1, false, Magic.MAGIC_TELEPORT);
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
