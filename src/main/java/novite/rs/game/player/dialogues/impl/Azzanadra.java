package novite.rs.game.player.dialogues.impl;

import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.game.player.quests.impl.Desert_Treasure;
import novite.rs.utility.Utils;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 29, 2014
 */
public class Azzanadra extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Hello adventurer. I am the allmight Azzanandra.", "Do you think you are worthy enough to use the Ancient", "Spellbook?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Start Desert Treasure?", "Yes, I am!", "No, not yet.");
			stage = 0;
			break;
		case 0:
			switch (option) {
			case FIRST:
				end();
				player.getQuestManager().startQuest(Desert_Treasure.class);
				player.getQuestManager().handleNPC(player, Utils.findLocalNPC(player, 1971));
				break;
			case SECOND:
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {
	}

}
