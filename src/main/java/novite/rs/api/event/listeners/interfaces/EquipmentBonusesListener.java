package novite.rs.api.event.listeners.interfaces;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.networking.codec.handlers.ButtonHandler;
import novite.rs.utility.ItemExamines;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 27, 2014
 */
public class EquipmentBonusesListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 667 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		if (buttonId == 7) {
			if (slotId >= 14) {
				return false;
			}
			Item item = player.getEquipment().getItem(slotId);
			if (item == null) {
				return false;
			}
			if (packetId == 25) {
				player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
			} else if (packetId == 61) {
				ButtonHandler.sendRemove(player, slotId);
				ButtonHandler.refreshEquipBonuses(player);
			}
		}
		return true;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleNPCClick(Player player, NPC npc, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
	}

}
