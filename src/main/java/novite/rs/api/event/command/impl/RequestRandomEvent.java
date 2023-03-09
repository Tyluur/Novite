package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.randoms.RandomEventManager;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 26, 2014
 */
public class RequestRandomEvent extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "sre" };
	}

	@Override
	public void execute(Player player) {
		String params = getCompleted(cmd, 1);
		if (params.contains("-name")) {
			String name = params.split("-name")[1].trim();
			RandomEventManager.get().start(player, name);
		} else {
			player.getTemporaryAttributtes().put("random_event_requested", true);
		}
	}
}
