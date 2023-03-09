package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 25, 2014
 */
public class SetAkrisaePoints extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "setap" };
	}

	@Override
	public void execute(Player player) {
		player.getFacade().setAkrisaePoints(Integer.parseInt(cmd[1]));
	}

}
