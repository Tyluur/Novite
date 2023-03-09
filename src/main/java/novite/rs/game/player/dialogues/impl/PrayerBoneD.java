package novite.rs.game.player.dialogues.impl;

import novite.rs.game.player.actions.custom.BoneAltarAction;
import novite.rs.game.player.content.SkillsDialogue;
import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 5, 2014
 */
public class PrayerBoneD extends Dialogue {

	@Override
	public void start() {
		itemId = (Integer) parameters[0];
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.SELECT, "How many bones would you like to use?", player.getInventory().getItems().getNumberOf(itemId), new int[] { itemId }, null);
	}

	@Override
	public void run(int interfaceId, int option) {
		end();
		player.getActionManager().setAction(new BoneAltarAction(itemId, SkillsDialogue.getQuantity(player)));
	}

	@Override
	public void finish() {

	}

	int itemId;

}
