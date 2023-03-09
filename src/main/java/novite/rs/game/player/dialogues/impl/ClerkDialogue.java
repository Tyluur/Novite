package novite.rs.game.player.dialogues.impl;

import novite.rs.Constants;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 20, 2014
 */
public class ClerkDialogue extends Dialogue {
	
	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Hello, welcome to the " + Constants.SERVER_NAME, "grand exchange owned by me.");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch(stage) {
		case -1:
			sendNPCDialogue(npcId, ChatAnimation.NORMAL, "All you have to do is register a buy/sell offer", "into the exchange and it will be processed. If the system", "is selling this item, you will see the quantity and pricing", "of the item.");
			stage = -2;
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
