package novite.rs.api.event.listeners.npc;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.dialogues.impl.InformationManager;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 21, 2014
 */
public class InformationManagerListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 747 };
	}

	@Override
	public boolean handleNPCClick(Player player, NPC npc, ClickOption option) {
		player.getDialogueManager().startDialogue(InformationManager.class, npc.getId());
		return true;
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
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
	}

}
