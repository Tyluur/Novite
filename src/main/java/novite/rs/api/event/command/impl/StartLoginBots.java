package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;
import novite.rs.utility.tools.LoginBot;

/**
 * @author Tyluur<itstyluur@gmail.com>
 * @since Aug 21, 2014
 */
public class StartLoginBots extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "loginbots" };
	}

	@Override
	public void execute(Player player) {
		LoginBot.start(Integer.parseInt(cmd[1]));
	}

}
