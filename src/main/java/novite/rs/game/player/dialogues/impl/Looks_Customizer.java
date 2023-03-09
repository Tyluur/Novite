package novite.rs.game.player.dialogues.impl;

import novite.rs.game.player.content.PlayerLook;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 22, 2014
 */
public class Looks_Customizer extends Dialogue {

	@Override
	public void start() {
		npcId = (int) parameters[0];
		if (player.getEquipment().wearingArmour()) {
			sendDialogue("You have to take off your armour first.");
			stage = -2;
			return;
		}
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "This will cost you 200k coins, are you sure?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Select an Option", "Yes", "No");
			stage = 0;
			break;
		case 0:
			if (option == FIRST) {
				if (player.takeMoney(200000)) {
					PlayerLook.openCharacterCustomizing(player);
				} else {
					sendNPCDialogue(npcId, ChatAnimation.LAUGHING, "LOL! You don't have 200k coins to pay for this.");
					stage = -2;
					return;
				}
			}
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}

	int npcId;

}
