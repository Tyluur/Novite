package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.game.player.dialogues.DialogueHandler;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 22, 2014
 */
public class ReloadDialogues extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "rld" };
	}

	@Override
	public void execute(Player player) {
		DialogueHandler.reload();
	}

}
