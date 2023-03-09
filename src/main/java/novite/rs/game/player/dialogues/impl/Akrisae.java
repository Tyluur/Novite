package novite.rs.game.player.dialogues.impl;

import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.utility.game.ChatColors;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 25, 2014
 */
public class Akrisae extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Hello, adventurer. I am Akrisae, the last of the brothers.", "Would you like to engage in my minigame?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Select an Option", "Tell me more about the minigame.", "Yes, I would like to start.");
			stage = 0;
			break;
		case 0:
			switch (option) {
			case FIRST:
				sendNPCDialogue(npcId, ChatAnimation.NORMAL, "My brothers betrayed me when we had grown up", "I plotted my revenge on them ever since.", "I need you to kill the last of them in my minigame.");
				stage = 1;
				break;
			case SECOND:
				end();
				player.getControllerManager().startController("Akrisae");
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, ChatAnimation.NORMAL, "They are pretty difficult to defeat, but a skilled", "warrior such as yourself should be able to manage.", "You will get <col=" + ChatColors.BLUE + ">Akrisae Points</col> if you somehow die.", "during the battle. Those are exchangeable for items.");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(npcId, ChatAnimation.NORMAL, "<col=" +ChatColors.BLUE + ">Akrisae Points</col> will get you random pieces of barrows armour.", "The more points you have, the higher the chance to get them.", "Open the chest <col=" + ChatColors.RED + ">south</col> of me for rewards.");
			stage = -1;
			break;
		}
	}

	@Override
	public void finish() {

	}

}
