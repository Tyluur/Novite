package novite.rs.game.player.dialogues.impl;

import novite.rs.game.minigames.runeslayer.RuneSlayerShop;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 30, 2014
 */
public class RuneSlayer_Master extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Greetings adventurer. How may I be of help to you?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Select an Option", "View Rewards", "Explain RuneSlayer");
			stage = 0;
			break;
		case 0:
			switch (option) {
			case FIRST:
				RuneSlayerShop.display(player);
				end();
				break;
			case SECOND:
				sendNPCDialogue(npcId, ChatAnimation.NORMAL, "When you enter the portal behind me, you will have a few", "moments to get prepared to battle with your clan.", "You can also pick up some supplies.");
				stage = 1;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You will then be teleported into a room with monsters", "to defeat. When all the monsters have been killed", "a skilling task will appear. Once the task is", "finished, the room is cleared.");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You must stay alive as long as you can", "and you will receive more tokens. These tokens can be", "exchanged for rewards in my shop.");
			stage = -2;
			break;
		}
	}

	@Override
	public void finish() {

	}

}
