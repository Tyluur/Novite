package novite.rs.api.event.listeners.objects;

import novite.rs.api.event.EventListener;
import novite.rs.api.event.listeners.interfaces.TeleportationSelectListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.dialogues.impl.TeleportTreeDialogue;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since May 4, 2014
 */
public class SpiritTree extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 49227, 1295 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		if (option == ClickOption.FIRST) {
			player.getDialogueManager().startDialogue(TeleportTreeDialogue.class);
		} else if (option == ClickOption.SECOND) {
			TeleportationSelectListener.display(player);
		}
		return true;
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
