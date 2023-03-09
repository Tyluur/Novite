package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.ChatColors;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 5, 2014
 */
public class HidePlayer extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.PLAYER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "hide" };
	}

	@Override
	public void execute(Player player) {
		if (player.isSupporter() || player.getRights() > 0) {
			player.getAppearence().switchHidden();
			player.getDialogueManager().startDialogue("SimpleMessage", "<col=" + ChatColors.MAROON + ">You are now " + (player.getAppearence().isHidden() ? "hidden" : "visible") + ".");
		}
	}

}
