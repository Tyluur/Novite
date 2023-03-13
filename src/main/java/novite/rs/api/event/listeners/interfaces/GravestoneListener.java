package novite.rs.api.event.listeners.interfaces;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 24, 2014
 */
public class GravestoneListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 652 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		if (buttonId == 31)
			handleSelectionInterface(player, slotId / 6);
		else if (buttonId == 34)
			confirmSelection(player);
		return true;
	}

	public static void openSelectionInterface(Player player) {
		player.getInterfaceManager().sendInterface(652);
		player.getPackets().sendUnlockIComponentOptionSlots(652, 31, 0, 78, 0, 1);
		player.getPackets().sendUnlockIComponentOptionSlots(652, 34, 0, 13, 0, 1);
		player.getVarsManager().sendVar(1146, player.getGraveStone() | 262112);
	}

	public static void handleSelectionInterface(Player player, int slot) {
		player.getTemporaryAttributtes().put("grave_selected_slot", slot);
		player.getTemporaryAttributtes().put("grave_selected_price", -1);
		if (slot == 1) {
			player.getTemporaryAttributtes().put("grave_selected_price", 50);
		} else if (slot == 2) {
			player.getTemporaryAttributtes().put("grave_selected_price", 500);
		} else if (slot == 3) {
			player.getTemporaryAttributtes().put("grave_selected_price", 5000);
		} else if (slot > 3 && slot < 12) {
			player.getTemporaryAttributtes().put("grave_selected_price", 50000);
		} else if (slot > 11) {
			player.getTemporaryAttributtes().put("grave_selected_price", 500000);
		}
	}

	public static void confirmSelection(Player player) {
		int selectedSlot = (int) player.getTemporaryAttributtes().get("grave_selected_slot");
		int selectedPrice = (int) player.getTemporaryAttributtes().get("grave_selected_price");
		if (selectedSlot != -1) {
			if (selectedPrice != -1) {
				if (player.takeMoney(selectedPrice)) {
					player.setGraveStone(selectedSlot);
				} else {
					player.getPackets().sendGameMessage("You don't have enough coins to purchase this gravestone.");
					return;
				}
			}
			player.closeInterfaces();
		}
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
