package novite.rs.game.player.dialogues.impl;

import novite.rs.game.minigames.CastleWars;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;

public class Lanthus extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Good day! How may I help you?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendOptionsDialogue(DEFAULT_OPTIONS_TI, "What is this place?", "What do you have for trade?", "How many Castle Wars games have I finished?");
			stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(ChatAnimation.NORMAL, "What is this place?");
				stage = 1;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(ChatAnimation.NORMAL, "What do you have for trade?");
				stage = 2;
			} else if (componentId == OPTION_3) {
				sendPlayerDialogue(ChatAnimation.NORMAL, "How many castle wars game have I won?");
				stage = 3;
			} else {
				end();
			}
		} else if (stage == 1) {
			sendNPCDialogue(npcId, ChatAnimation.NORMAL, "This is the great Castle Wars arena! Here you can fight for the glory of Saradomin or Zamorak.");
			stage = -2;
		} else if (stage == 2) {
			CastleWars.openCastleWarsTicketExchange(player);
		} else if (stage == 3) {
			sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You have finished " + player.getFinishedCastleWars() + " Castle Wars games so far, my son.");
			stage = -2;
		} else {
			end();
		}
	}

	@Override
	public void finish() {

	}
}
