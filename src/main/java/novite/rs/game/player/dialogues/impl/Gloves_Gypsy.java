package novite.rs.game.player.dialogues.impl;

import novite.rs.Constants;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.game.player.quests.impl.Recipe_For_Disaster;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 29, 2014
 */
public class Gloves_Gypsy extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Hello, I am the Gloves Gypsy; I can grant you access", "to the most powerful gloves on " + Constants.SERVER_NAME + ", but you must", "prove yourself worthy. Do you wish to do so?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Start Recipe for Disaster?", "Yes", "No");
			stage = 0;
			break;
		case 0:
			switch (option) {
			case FIRST:
				end();
				player.getQuestManager().startQuest(Recipe_For_Disaster.class);
				break;
			case SECOND:
				sendPlayerDialogue(ChatAnimation.SAD, "No");
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
