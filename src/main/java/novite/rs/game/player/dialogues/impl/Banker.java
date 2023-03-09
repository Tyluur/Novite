package novite.rs.game.player.dialogues.impl;

import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 21, 2014
 */
public class Banker extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Good day, how may I help you?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Select an Option", "I'd like to view my bank account, please.", "I'd like to view my Bank Pin Settings.");
			stage = 0;
			break;
		case 0:
			switch (option) {
			case FIRST:
				end();
				player.getBank().openBank();
				break;
			case SECOND:
				end();
				player.getBank().getPin().openSettingsScreen();
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
