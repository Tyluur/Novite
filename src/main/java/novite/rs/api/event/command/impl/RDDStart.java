package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 29, 2014
 */
public class RDDStart extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "rfdstart" };
	}

	@Override
	public void execute(Player player) {
		player.getControllerManager().startController("RFDQuest");
	}

}
