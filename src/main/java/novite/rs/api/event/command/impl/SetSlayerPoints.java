package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 6, 2014
 */
public class SetSlayerPoints extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "setslayerp", "ssp" };
	}

	@Override
	public void execute(Player player) {
		player.getSlayerManager().setPoints(Integer.parseInt(cmd[1]));
	}

}
