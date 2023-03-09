package novite.rs.api.event.listeners.npc;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.slayer.SlayerManager;
import novite.rs.game.player.dialogues.DialogueHandler;
import novite.rs.game.player.dialogues.impl.Lapalok;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ShopsLoader;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 20, 2014
 */
public class LapalokListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 8467 };
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
		if (option == ClickOption.SECOND) {
			Lapalok lapalok = (Lapalok) DialogueHandler.getDialogue(Lapalok.class.getSimpleName());
			player.getDialogueManager().startDialogue(lapalok, npc.getId());
			lapalok.requestTask(player);
			return true;
		} else if (option == ClickOption.THIRD) {
			player.getDialogueManager().finishDialogue();
			player.getSlayerManager().displayRewards(SlayerManager.BUY_INTERFACE);
			return true;
		} else if (option == ClickOption.FOURTH) {
			player.getDialogueManager().finishDialogue();
			((ShopsLoader) JsonHandler.getJsonLoader(ShopsLoader.class)).openShop(player, "Slayer Rewards");
			return true;
		}
		return false;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		return false;
	}

}
