package novite.rs.game.player.dialogues.impl;

import novite.rs.game.player.actions.crafting.CannonBallCreation;
import novite.rs.game.player.content.SkillsDialogue;
import novite.rs.game.player.dialogues.Dialogue;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Jan 15, 2014
 */
public class CannonBallD extends Dialogue {

	@Override
	public void start() {
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.SELECT, "How many bars would you like to use?", player.getInventory().getItems().getNumberOf(2353), new int[] { 2 }, null);
	}

	@Override
	public void run(int interfaceId, int option) {
		end();
		player.getActionManager().setAction(new CannonBallCreation(SkillsDialogue.getQuantity(player)));

	}

	@Override
	public void finish() {

	}

}
