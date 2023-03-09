package novite.rs.api.event.listeners.npc;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ShopsLoader;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Aug 1, 2014
 */
public class TzhaarHurTel extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 2620 } ;
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		return false;
	}

	@Override
	public boolean handleNPCClick(Player player, NPC npc, ClickOption option) {
		((ShopsLoader) JsonHandler.getJsonLoader(ShopsLoader.class)).openShop(player, "Tokkul Store");
		return true;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		return false;
	}

}
