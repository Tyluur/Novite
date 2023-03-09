package novite.rs.api.event.listeners.objects;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ShopsLoader;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 31, 2014
 */
public class RFDChest extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 12309 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		if (option == ClickOption.FIRST) {
			player.getBank().openBank();
			return true;
		} else if (option == ClickOption.THIRD) {
			((ShopsLoader) JsonHandler.getJsonLoader(ShopsLoader.class)).openShop(player, "Culinaromancer's Chest");
			return true;
		}
		return false;
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
