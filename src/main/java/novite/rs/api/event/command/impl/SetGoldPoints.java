package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur<itstyluur@gmail.com>
 * @since Aug 20, 2014
 */
public class SetGoldPoints extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "setgp" };
	}

	@Override
	public void execute(Player player) {
		player.getFacade().setGoldPoints(Integer.parseInt(cmd[1]));
	}

}
