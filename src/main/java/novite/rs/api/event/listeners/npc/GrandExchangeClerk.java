package novite.rs.api.event.listeners.npc;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.ArmourSetOpening;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.exchange.ExchangeManagement;
import novite.rs.game.player.dialogues.impl.ClerkDialogue;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 20, 2014
 */
public class GrandExchangeClerk extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 2241 };
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
		if (option == ClickOption.FIRST) {
			player.getDialogueManager().startDialogue(ClerkDialogue.class, npc.getId());
		} else if (option == ClickOption.SECOND) {
			ExchangeManagement.sendSummary(player);
		} else if (option == ClickOption.THIRD) {
			ArmourSetOpening.openSets(player);
		}
		return true;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		return false;
	}

}
