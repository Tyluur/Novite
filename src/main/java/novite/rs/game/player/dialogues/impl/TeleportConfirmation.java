package novite.rs.game.player.dialogues.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.content.Magic;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.utility.game.ChatColors;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 20, 2014
 */
public class TeleportConfirmation extends Dialogue {
	
	int npcId = 2244;

	@Override
	public void start() {
		sendNPCDialogue(npcId, ChatAnimation.FURIOUS, "HEY! Before you teleport, you must know <col=" + ChatColors.RED + ">this", "<col=" + ChatColors.RED + ">location is dangerous</col>. Are you sure you wish to travel?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch(stage) {
		case -1:
			sendOptionsDialogue("Are you sure?", "Yes", "No");
			stage = 0;
			break;
		case 0:
			switch(option) {
			case FIRST:
				Magic.sendPurpleTeleportSpell(player, (WorldTile) parameters[0]);
				break;
			case SECOND:
				break;
			}
			end();
			break;
		}
	}

	@Override
	public void finish() {
		
	}

}
