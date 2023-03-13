package novite.rs.api.event.listeners.interfaces;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ShopsLoader;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 28, 2014
 */
public class DonationShopListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 6 };
	}

	/**
	 * Displays the shop interface
	 * 
	 * @param player
	 *            The player to display it to
	 */
	public static void display(Player player) {
		player.closeInterfaces();

		int interfaceId = 6;

		int[] toHide = { 23, 6, 7, 10, 11, 4, 5, 3, 2, 22 };

		for (int comp : toHide) {
			player.getPackets().sendHideIComponent(interfaceId, comp, true);
		}

		player.getPackets().sendIComponentText(interfaceId, 20, "Armour");
		player.getPackets().sendIComponentText(interfaceId, 19, "Weapons");
		player.getPackets().sendIComponentText(interfaceId, 16, "Untradeables");
		player.getPackets().sendIComponentText(interfaceId, 14, "Rares");
		player.getPackets().sendIComponentText(interfaceId, 43, "Select a Shop");

		player.getPackets().sendItemOnIComponent(interfaceId, 21, 20139, 1);
		player.getPackets().sendItemOnIComponent(interfaceId, 18, 14484, 1);
		player.getPackets().sendItemOnIComponent(interfaceId, 17, 6570, 1);
		player.getPackets().sendItemOnIComponent(interfaceId, 15, 1055, 1);

		player.getInterfaceManager().sendInterface(interfaceId);
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		switch (buttonId) {
		case 25:
			JsonHandler.<ShopsLoader> getJsonLoader(ShopsLoader.class).openShop(player, "Gold Points Armours");
			break;
		case 26:
			JsonHandler.<ShopsLoader> getJsonLoader(ShopsLoader.class).openShop(player, "Gold Points Weapons");
			break;
		case 27:
			JsonHandler.<ShopsLoader> getJsonLoader(ShopsLoader.class).openShop(player, "Gold Points Untradeables");
			break;
		case 28:
			JsonHandler.<ShopsLoader> getJsonLoader(ShopsLoader.class).openShop(player, "Gold Points Rares");
			break;
		}
		return true;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
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
