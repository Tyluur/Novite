package novite.rs.api.event.listeners.interfaces;

import novite.rs.Constants;
import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.content.slayer.Abilities;
import novite.rs.game.player.content.slayer.SlayerManager;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.game.player.dialogues.SimpleNPCMessage;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 21, 2014
 */
public class SlayerRewardsInterface extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { SlayerManager.BUY_INTERFACE, SlayerManager.ASSIGN_INTERFACE, SlayerManager.LEARN_INTERFACE };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		switch (interfaceId) {
		case SlayerManager.BUY_INTERFACE:
			switch (buttonId) {
			case 32:
			case 24:
				if (player.getSlayerManager().removePoints(400)) {
					player.getSkills().addExpNoModifier(Skills.SLAYER, 40000);
					player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 8467, "You are more enlightened in the slayer skill.");
				}
				break;
			case 26:
			case 33:
				if (player.getSlayerManager().removePoints(75)) {
					player.getInventory().addItemDrop(13281, 1);
					player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 8467, "I quickly forged this for you, I hope it'll do.");
				}
				break;
			case 36:
			case 28:
				if (player.getSlayerManager().removePoints(35)) {
					player.getInventory().addItemDrop(560, 250);
					player.getInventory().addItemDrop(558, 1000);
					player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 8467, "Here are your runes. Use them wisely.");
				}
				break;
			case 37:
			case 34:
				if (player.getSlayerManager().removePoints(35)) {
					player.getInventory().addItemDrop(13280, 250);
					player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 8467, "Here are your bolts. Use them wisely.");
				}
				break;
			case 39:
			case 35:
				if (player.getSlayerManager().removePoints(35)) {
					player.getInventory().addItemDrop(4160, 250);
					player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 8467, "Here are your arrows. Use them wisely.");
				}
				break;
			case 16:
				player.getSlayerManager().displayRewards(SlayerManager.LEARN_INTERFACE);
				break;
			case 17:
				player.getSlayerManager().displayRewards(SlayerManager.ASSIGN_INTERFACE);
				break;
			}
			break;
		case SlayerManager.LEARN_INTERFACE:
			switch (buttonId) {
			case 29:
			case 22:
			case 23:
			case 30:
				player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 8467, "There is no need for these on " + Constants.SERVER_NAME + ".");
				break;
			case 14:
				player.getSlayerManager().displayRewards(SlayerManager.ASSIGN_INTERFACE);
				break;
			case 24:
			case 31:
				if (player.getSlayerManager().removePoints(400)) {
					if (player.getSlayerManager().unlockAbility(Abilities.CRAFT_SLAYER_HELMETS)) {
						player.getSlayerManager().displayRewards(interfaceId);
						player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 8467, "You can now combine the nosepeg, earmuffs, spiny helm", "facemask, and uncharged black mask to make a Slayer helmet.", "This process requires 55 crafting, though.");
					} else {
						player.sendMessage("You have already unlocked this ability.");
					}
				}
				break;
			case 15:
				player.getSlayerManager().displayRewards(SlayerManager.BUY_INTERFACE);
				break;
			}
			break;
		case SlayerManager.ASSIGN_INTERFACE:
			switch (buttonId) {
			case 23:
			case 26:
				player.getDialogueManager().startDialogue(new Dialogue() {
					
					@Override
					public void start() {
						sendOptionsDialogue("Select an Option", "Pay fee with: 30 Slayer Points", "Pay fee with: 500K Coins");
					}
					
					@Override
					public void run(int interfaceId, int option) {
						if (player.getSlayerTask() == null) {
							sendNPCDialogue(8467, ChatAnimation.NORMAL, "You have no slayer task to reset.");
							stage = -2;
							return;
						}
						switch(option) {
						case FIRST:
							if (player.getSlayerManager().removePoints(30)) {
								player.setSlayerTask(null);
								stage = -2;
								sendNPCDialogue(8467, ChatAnimation.NORMAL, "Your slayer task has been reset.");
								player.getSlayerManager().sendPoints(SlayerManager.ASSIGN_INTERFACE);
							} else {
								end();
							}
							break;
						case SECOND:
							if (player.takeMoney(500000)) {
								player.setSlayerTask(null);
								stage = -2;
								sendNPCDialogue(8467, ChatAnimation.NORMAL, "Your slayer task has been reset.");
							} else {
								sendNPCDialogue(8467, ChatAnimation.SAD, "You don't have enough coins to pay the fee.");
								stage = -2;
							}
							break;
						}
					}
					
					@Override
					public void finish() {
						
					}
				});
				break;
			case 14:
				player.getSlayerManager().displayRewards(SlayerManager.LEARN_INTERFACE);
				break;
			case 15:
				player.getSlayerManager().displayRewards(SlayerManager.BUY_INTERFACE);
				break;
			}
			break;
		}
		player.getSlayerManager().sendPoints(interfaceId);
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
		return false;
	}

}
