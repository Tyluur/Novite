package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandHandler;
import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 19, 2014
 */
public class ReloadCommands extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "rlc" };
	}

	@Override
	public void execute(Player player) {
		CommandHandler.get().initialize();
	}

}
