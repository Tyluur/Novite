package novite.rs.game.player.dialogues.impl;

import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 29, 2014
 */
public class Nurse_Sarah extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Would you like to be completely healed?", "It only costs you 100K!");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
			case -1:
				sendOptionsDialogue("Select an Option", "Yes, I would!", "No.");
				stage = 0;
				break;
			case 0:
				switch (option) {
					case FIRST:
						if (player.takeMoney(100000)) {
							player.restoreAll();
							sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Thanks for your business! Enjoy your restored character");
						} else {
							sendPlayerDialogue(ChatAnimation.SAD, "I don't have 100K cash on me, sorry!");
						}
						stage = -2;
						break;
					case SECOND:
						end();
						break;
				}
				break;

			default:
				break;
		}
	}

	@Override
	public void finish() {

	}

	int npcId;
}
