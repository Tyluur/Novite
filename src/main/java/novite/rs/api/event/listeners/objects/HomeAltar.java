package novite.rs.api.event.listeners.objects;

import novite.rs.api.event.EventListener;
import novite.rs.game.Animation;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.dialogues.SimpleNPCMessage;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since May 4, 2014
 */
public class HomeAltar extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 13199 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		player.setNextAnimation(new Animation(645));
		/** Refreshing all player characteristics to optimal settings */
		player.getCombatDefinitions().setSpecialAttack(100);
		player.getPoison().reset();
		player.getPrayer().setPrayerpoints((int) ((player.getSkills().getLevelForXp(Skills.PRAYER) * 10) * 1.15));
		player.getPrayer().refreshPrayerPoints();
		player.heal(player.getMaxHitpoints(), (int) ((player.getSkills().getLevelForXp(Skills.HITPOINTS) * 10) * 0.05));
		/** Sending surgeon dialogue */
		player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 961, "I have restored your character to extreme health!");
		player.sendMessage("You feel refreshed, past your normal health levels.");
		return true;
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
