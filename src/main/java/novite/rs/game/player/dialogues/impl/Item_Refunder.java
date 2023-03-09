package novite.rs.game.player.dialogues.impl;

import java.util.ListIterator;

import novite.rs.game.Animation;
import novite.rs.game.ForceTalk;
import novite.rs.game.Graphics;
import novite.rs.game.item.Item;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.utility.Utils;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 31, 2014
 */
public class Item_Refunder extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Hey! You have " + player.getUntradeableItems().size() + " untradeables to claim.", "Would you like to buy them back?");
	}

	@Override
	public void run(int interfaceId, int option) {
		NPC refunder = Utils.findLocalNPC(player, 5026);
		switch (stage) {
			case -1:
				sendOptionsDialogue("Select an Option", "Buy untradeables back", "No.");
				stage = 0;
				break;
			case 0:
				switch (option) {
					case FIRST:
						int coins = 0;
						for (Item item : player.getUntradeableItems()) {
							coins += item.getDefinitions().getValue();
						}
						if (coins == 0) {
							sendNPCDialogue(npcId, ChatAnimation.FURIOUS, "You have no untradeables to claim back.");
							stage = -2;
							return;
						}
						if (player.takeMoney(coins)) {
							if (refunder != null) {
								refunder.setNextAnimation(new Animation(722));
								refunder.setNextGraphics(new Graphics(343));
								refunder.setNextForceTalk(new ForceTalk("I found your untradeable items, " + player.getDisplayName() + "!"));
							}
							ListIterator<Item> it$ = player.getUntradeableItems().listIterator();
							while (it$.hasNext()) {
								Item item = it$.next();
								player.getInventory().addDroppable(item);
								it$.remove();
							}
							sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Thank you for " + Utils.format(coins) + " coins.", "Your untradables have been given to you.");
						} else {
							sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You need " + Utils.format(coins) + " coins for me to give them back.");
						}
						stage = -2;
						break;
					case SECOND:
						sendPlayerDialogue(ChatAnimation.NORMAL, "No...");
						stage = -2;
						break;
				}
				break;
		}
	}

	@Override
	public void finish() {

	}

	int npcId;

}
