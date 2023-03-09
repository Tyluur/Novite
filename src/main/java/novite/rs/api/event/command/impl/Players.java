package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.game.player.dialogues.SimpleMessage;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 31, 2014
 */
public class Players extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.PLAYER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "players" };
	}

	@Override
	public void execute(Player player) {
		player.getDialogueManager().startDialogue(SimpleMessage.class, "Check your information tab to see", "how many players there are online.");
	}

}
