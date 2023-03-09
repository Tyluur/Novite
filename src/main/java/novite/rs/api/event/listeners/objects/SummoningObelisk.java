package novite.rs.api.event.listeners.objects;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.actions.summoning.Summoning;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 5, 2014
 */
public class SummoningObelisk extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 28716 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		switch (option) {
			case FIRST:
				Summoning.openInfusionInterface(player);
				break;
			case SECOND:
				break;
			default:
				break;
		}
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
