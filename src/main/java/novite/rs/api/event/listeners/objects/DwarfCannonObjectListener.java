package novite.rs.api.event.listeners.objects;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.OwnedObjectManager;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.cannon.CannonAlgorithms;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 22, 2014
 */
public class DwarfCannonObjectListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 6 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		if (option == ClickOption.FIRST) {
			CannonAlgorithms.toggleFiring(player, worldObject);
		} else if (option == ClickOption.SECOND) {
			Player owner = OwnedObjectManager.getOwner(worldObject);
			if (owner != null && owner.equals(player) && player.getDwarfCannon() != null) {
				player.getDwarfCannon().finish(false);
			} else {
				player.getDialogueManager().startDialogue("SimpleMessage", "That is not your cannon!");
			}
		}
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
