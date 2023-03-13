package novite.rs.game.player.dialogues.impl;

import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.utility.Utils;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ShopsLoader;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 14, 2014
 */
public class Queen_of_Snow extends Dialogue {
	
	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Hello traveller! How may I be of assistance to you?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch(stage) {
		case -1:
			sendOptionsDialogue("Select an Option", "I want to buy some rewards.", "Explain the Novite Games to me.");
			stage = 0;
			break;
		case 0:
			switch(option) {
			case FIRST:
				((ShopsLoader) JsonHandler.getJsonLoader(ShopsLoader.class)).openShop(player, "Novite Games Rewards");
				sendDialogue("You currently have: " + Utils.format(player.getFacade().getNoviteGamePoints()) + " novite game points.");
				stage = -2;
				break;
			case SECOND:
				sendPlayerDialogue(ChatAnimation.NORMAL, "Can you explain the novite games to me?");
				stage = 1;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, ChatAnimation.NORMAL, "The Novite Games are where the most skilled players", "come to prove their abilities. You will use your main skills", "and your combat abilities in this minigame. The longer you", "survive, the more points you get.");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(ChatAnimation.NORMAL, "So all I have to do is survive for a long time?");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(npcId, ChatAnimation.LAUGHING, "Hahaha! Yes, that is it! But trust me, it", "is much harder than it sounds...");
			stage = -2;
			break;
		}
	}

	@Override
	public void finish() {
	}

}
