package novite.rs.api.event.listeners.items;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.dialogues.impl.SimpleItemMessage;
import novite.rs.utility.game.Scrolls;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 6, 2014
 */
public class PrayerScrollListeners extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { Scrolls.AUGURY_SCROLL, Scrolls.RIGOUR_SCROLL, Scrolls.SOULSPLIT_SCROLL, Scrolls.TURMOIL_SCROLL };
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
		return false;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		int book = -1;
		int prayer = -1;
		switch(item.getId()) {
		case Scrolls.RIGOUR_SCROLL:
			book = 0;
			prayer = 28;
			break;
		case Scrolls.AUGURY_SCROLL:
			book = 0;
			prayer = 29;
			break;
		case Scrolls.SOULSPLIT_SCROLL:
			book = 1;
			prayer = 18;
			break;
		case Scrolls.TURMOIL_SCROLL:
			book = 1;
			prayer = 19;
			break;
		}
		if (!player.getInventory().contains(item.getId())) {
			return false;
		}
		if (book == -1 || prayer == -1)
			return false;
		player.getPrayer().getUnlockedPrayers()[book][prayer] = true;	
		player.getDialogueManager().startDialogue(SimpleItemMessage.class, item.getId(), "You read the scroll...", "", "You feel more connected to the gods.");
		player.getInventory().deleteItem(item);
		return true;
	}

}
