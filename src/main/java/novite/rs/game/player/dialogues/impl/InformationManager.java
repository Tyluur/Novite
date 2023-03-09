package novite.rs.game.player.dialogues.impl;

import novite.rs.Constants;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.utility.Utils;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 21, 2014
 */
public class InformationManager extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Hello, adventurer! I provide you with information about", Constants.SERVER_NAME + ". What would you like to know?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
			case -1:
				sendOptionsDialogue("Select an Option", "Can I recharge my ring of wealth?", "How do I buy items?", "How do I teleport around the game?", "What are achievements?");
				stage = 0;
				break;
			case 0:
				switch (option) {
					case FIRST:
						sendNPCDialogue(npcId, ChatAnimation.LAUGHING, "This will cost you 100K coins", "are you sure you want to do this?");
						stage = 1;
						break;
					case SECOND:
						sendNPCDialogue(npcId, ChatAnimation.NORMAL, Constants.SERVER_NAME + " has an easy-to-use shop system. Talk to the", "shopkeeper to the left of me to view any shops.");
						NPC shopkeeper = Utils.findLocalNPC(player, 520);
						if (shopkeeper != null) {
							player.getHintIconsManager().addHintIcon(shopkeeper, 1, 1, false);
							player.setCloseInterfacesEvent(new Runnable() {

								@Override
								public void run() {
									player.getHintIconsManager().removeUnsavedHintIcon();
								}
							});
						}
						stage = -1;
						break;
					case THIRD:
						sendNPCDialogue(npcId, ChatAnimation.NORMAL, Constants.SERVER_NAME + "'s teleporting system is the best! Talk to the", "teleportation manager 2 steps to the left of me", "and click wherever you want to go!");
						NPC teleportationManager = Utils.findLocalNPC(player, 872);
						if (teleportationManager != null) {
							player.getHintIconsManager().addHintIcon(teleportationManager, 1, 1, false);
							player.setCloseInterfacesEvent(new Runnable() {

								@Override
								public void run() {
									player.getHintIconsManager().removeUnsavedHintIcon();
								}
							});
						}
						stage = -1;
						break;
					case FOURTH:
						sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Click your achievement tab and click on any achievements", "to see achievements you can complete. You will receive", "a notification while you progress in the achievement", "and a reward when it is complete.");
						player.getAchievementManager().sendFlashingTab();
						stage = -1;
						break;
				}
				break;
			case 1:
				sendOptionsDialogue("Select an Option", "Yes, I'm sure.", "No, never mind.");
				stage = 2;
				break;
			case 2:
				switch (option) {
					case FIRST:
						if (player.takeMoney(100000)) {
							player.getFacade().setRowCharges(100);
							sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You now have " + player.getFacade().getRowCharges() + " charges!");
						} else {
							sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You don't have that much money on you.");
						}
						stage = -2;
						break;
					case SECOND:
						sendPlayerDialogue(ChatAnimation.LAUGHING, "No, never mind...");
						stage = -2;
						break;
				}
				break;
		}
	}

	@Override
	public void finish() {
		player.getHintIconsManager().removeUnsavedHintIcon();
	}

	int npcId;

}
