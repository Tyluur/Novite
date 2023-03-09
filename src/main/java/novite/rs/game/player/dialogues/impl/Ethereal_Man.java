package novite.rs.game.player.dialogues.impl;

import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.game.player.quests.impl.Lunar_Diplomacy;
import novite.rs.utility.Utils;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 29, 2014
 */
public class Ethereal_Man extends Dialogue {
	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Hello spiritual energy. I can grant you access to the", "lunar spellbook once you prove yourself worthy.", "Do you wish to try?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Start Lunar Diplomacy?", "Yes.", "No.");
			stage = 0;
			break;
		case 0:
			end();
			switch (option) {
			case FIRST:
				player.getQuestManager().startQuest(Lunar_Diplomacy.class);
				player.getQuestManager().handleNPC(player, Utils.findLocalNPC(player, 4501));
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {
	}

}
