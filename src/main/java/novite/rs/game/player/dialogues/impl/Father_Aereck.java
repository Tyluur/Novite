package novite.rs.game.player.dialogues.impl;

import novite.rs.api.event.listeners.interfaces.GravestoneListener;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 24, 2014
 */
public class Father_Aereck extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Hello there brother " + player.getDisplayName() + ". How may I help you today?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Select an Option", "Can I have a different gravestone?", "Can you restore my prayer?");
			stage = 0;
			break;
		case 0:
			switch (option) {
			case FIRST:
				end();
				GravestoneListener.openSelectionInterface(player);
				break;
			case SECOND:
				sendNPCDialogue(npcId, ChatAnimation.NORMAL, "I think the gods prefer if you pray", "to them at an altar dedicated to their name.");
				stage = -2;
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {
	}

}
