package novite.rs.api.event.listeners.items;

import novite.rs.Constants;
import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.dialogues.impl.SimpleItemMessage;
import novite.rs.utility.logging.types.FileLogger;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 2, 2014
 */
public class VoteClaimerListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 7775 };
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
		if (player.getInventory().contains(item.getId())) {
			player.getInventory().deleteItem(item);
			int amount = 5;
			if (Constants.isDoubleVotes)
				amount = amount * 2;
			player.getInventory().addItem(10944, amount);
			player.getDialogueManager().startDialogue(SimpleItemMessage.class, item.getId(), "You exchange your Vote Claimer for " + amount + "x Vote Tokens.", "Spend these in the vote shop at Party Pete.");
			FileLogger.getFileLogger().writeLog("votes/", player.getDisplayName() + " claimed their vote for " + amount  + " tokens.", true);
		}
		return true;
	}

}
