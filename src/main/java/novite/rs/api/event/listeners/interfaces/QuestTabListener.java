package novite.rs.api.event.listeners.interfaces;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.quests.Quest;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 14, 2014
 */
public class QuestTabListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 34 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		switch (buttonId) {
		case 9:
			Quest<?> quest = player.getQuestManager().getQuestByIndex(slotId);
			if (quest == null)
				return true;
			player.stopAll();
			player.getPackets().sendConfig(1439, -1);
			player.getQuestManager().sendInformation(quest.getName());
			break;
		}
		return true;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
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
