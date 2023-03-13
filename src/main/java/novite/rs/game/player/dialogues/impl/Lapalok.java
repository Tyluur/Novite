package novite.rs.game.player.dialogues.impl;

import novite.rs.Constants;
import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.slayer.Slayer;
import novite.rs.game.player.content.slayer.SlayerManager;
import novite.rs.game.player.content.slayer.Type;
import novite.rs.game.player.dialogues.BuyItemDialogue;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.utility.game.ChatColors;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 20, 2014
 */
public class Lapalok extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Greetings warrior. I am the slayer assistant of " + Constants.SERVER_NAME + ".", "How may I be of help to you today?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Select an Option", player.getSlayerTask() == null ? "Get Task" : "View Task", "Recharge Ring of Wealth");
			stage = 0;
			break;
		case 0:
			switch (option) {
			case FIRST:
				if (player.getSlayerTask() == null) {
					sendOptionsDialogue("Select an Task Type", "Easy", "Medium", "Hard", "Elite");
					stage = 1;
				} else {
					sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You must kill " + player.getSlayerTask().getAmount() + "x " + player.getSlayerTask().getName() + ".", "This task is <col=" + ChatColors.BLUE + ">" + player.getSlayerTask().getType() + "</col>.", "Navigate to my rewards shop to cancel your task.");
				}
				break;
			case SECOND:
				end();
				player.getDialogueManager().startDialogue(new BuyItemDialogue() {

					@Override
					public void run(int interfaceId, int option) {
						if (option == YES) {
							if (player.takeMoney(250000)) {
								player.getFacade().setRowCharges(100);
								sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You now have " + player.getFacade().getRowCharges() + " charges!");
							} else {
								stage = -2;
								sendPlayerDialogue(ChatAnimation.SAD, "I don't have that much money...");
								return;
							}
						}
						end();
					}
				}, new Item(2572), "This will cost you 250K Coins");
				break;
			}
			break;
		case 1:
			Type type = null;
			switch (option) {
			case FIRST:
				type = Type.EASY;
				break;
			case SECOND:
				type = Type.MEDIUM;
				break;
			case THIRD:
				type = Type.HARD;
				break;
			case FOURTH:
				type = Type.ELITE;
				break;
			}
			giveTask(player, type);
			break;
		case 2:
			sendOptionsDialogue("Select an Option", "Yes, cancel my task.", "No.");
			stage = 3;
			break;
		case 3:
			switch (option) {
			case FIRST:
				player.getSlayerManager().displayRewards(SlayerManager.ASSIGN_INTERFACE);
				break;
			case SECOND:
				break;
			}
			end();
			break;
		}
	}

	/**
	 * Requests a task
	 * 
	 * @param player
	 */
	public void requestTask(Player player) {
		sendOptionsDialogue("Select an Task Type", "Easy", "Medium", "Hard", "Elite");
		stage = 1;
	}

	/**
	 * Gives the task to the player and sends an informative dialogue afterwards
	 * 
	 * @param player
	 *            The player
	 * @param type
	 *            The type of task to give
	 */
	public void giveTask(Player player, Type type) {
		if (type != null) {
			if (player.getSlayerTask() == null) {
				if (!Slayer.giveTask(player, type))
					sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You must kill " + player.getSlayerTask().getAmount() + "x " + player.getSlayerTask().getName() + "s.");
				else {
					sendNPCDialogue(npcId, ChatAnimation.NORMAL, "There were no " + type.name().toLowerCase() + " slayable monsters at this level.", "Please select another task type.");
				}
				stage = -2;
			} else {
				sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You already have a task. You can cancel your task with", "your slayer points. Would you like to do that?");
				stage = 2;
			}
		} else
			System.err.println("Player selected null task type: " + player.getUsername());
	}

	@Override
	public void finish() {

	}

}
