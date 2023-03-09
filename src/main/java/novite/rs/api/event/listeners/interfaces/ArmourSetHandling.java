package novite.rs.api.event.listeners.interfaces;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.ArmourSetOpening;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.networking.protocol.game.DefaultGameDecoder;

public class ArmourSetHandling extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 644, 645 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		if (interfaceId == 645) {
			if (buttonId == 16) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET)
					ArmourSetOpening.sendComponents(player, itemId);
				else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET)
					ArmourSetOpening.exchangeSet(player, itemId);
				else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET)
					ArmourSetOpening.examineSet(player, itemId);
			}
		} else if (interfaceId == 644) {
			if (buttonId == 0) {
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET)
					ArmourSetOpening.sendComponentsBySlot(player, slotId, itemId);
				else if (packetId == DefaultGameDecoder.ACTION_BUTTON2_PACKET)
					ArmourSetOpening.exchangeSet(player, slotId, itemId);
				else if (packetId == DefaultGameDecoder.ACTION_BUTTON3_PACKET)
					player.getInventory().sendExamine(slotId);
			}
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
