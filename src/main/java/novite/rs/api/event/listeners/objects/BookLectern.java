package novite.rs.api.event.listeners.objects;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.dialogues.impl.Book_Switcher;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 28, 2014
 */
public class BookLectern extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 16599 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		player.getDialogueManager().startDialogue(Book_Switcher.class);
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
