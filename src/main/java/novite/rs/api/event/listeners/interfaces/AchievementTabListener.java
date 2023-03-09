package novite.rs.api.event.listeners.interfaces;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 20, 2014
 */
public class AchievementTabListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 1212 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		switch (buttonId) {
			case 6: // info
				player.getAchievementManager().displayInformation(player);
				break;
			case 8: // easy
				player.getAchievementManager().displayAchievements(Types.EASY);
				break;
			case 10: // medium
				player.getAchievementManager().displayAchievements(Types.MEDIUM);
				break;
			case 12: // hard
				player.getAchievementManager().displayAchievements(Types.HARD);
				break;
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
		// TODO Auto-generated method stub
		return false;
	}

}
