package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Aug 6, 2013
 */
public class Test extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "test" };
	}

	@Override
	public void execute(final Player player) {
		System.out.println(player.getMostDamageReceivedSourcePlayer());
	}

}
