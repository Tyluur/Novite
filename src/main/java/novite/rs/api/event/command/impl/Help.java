package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.game.player.dialogues.impl.HelpDialogue;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 15, 2014
 */
public class Help extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.PLAYER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "help", "info", "information" };
	}

	@Override
	public void execute(Player player) {
		player.getDialogueManager().startDialogue(HelpDialogue.class);
	}

}
