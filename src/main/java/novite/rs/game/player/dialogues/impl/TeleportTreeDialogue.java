package novite.rs.game.player.dialogues.impl;

import novite.rs.Constants;
import novite.rs.api.event.listeners.interfaces.TeleportationSelectListener;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 15, 2014
 */
public class TeleportTreeDialogue extends Dialogue {
	
	int npcId = 3637;

	@Override
	public void start() {
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Greetings adventurer. I can teleport you all around the", "map of " + Constants.SERVER_NAME + ". Please choose your desired location!");
	}

	@Override
	public void run(int interfaceId, int option) {
		end();
		TeleportationSelectListener.display(player);
	}

	@Override
	public void finish() {
		
	}

}
