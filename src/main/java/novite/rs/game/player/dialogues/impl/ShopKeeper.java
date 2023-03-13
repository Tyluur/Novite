package novite.rs.game.player.dialogues.impl;

import novite.rs.api.event.listeners.interfaces.SkillSelectionInterface;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.game.player.dialogues.SimpleMessage;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ShopsLoader;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 21, 2014
 */
public class ShopKeeper extends Dialogue {

	int npcId;
	
	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.LISTENING, "Hello, how can I help you?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch(stage) {
		case -1:
			sendOptionsDialogue("Select a Option", "View General Store", "Purchase Skillcapes");
			stage = 0;
			break;
		case 0:
			switch(option) {
			case FIRST:
				((ShopsLoader) JsonHandler.getJsonLoader(ShopsLoader.class)).openShop(player, "General Store");
				break;
			case SECOND:
				SkillSelectionInterface.display(player);
				player.getDialogueManager().startDialogue(SimpleMessage.class, "Select the skill in which you wish to buy a cape!");
				player.getTemporaryAttributtes().put("skill_selection_type", "CAPES");
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
