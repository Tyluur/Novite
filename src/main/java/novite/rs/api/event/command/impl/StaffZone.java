package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.Magic;
import novite.rs.utility.game.Rights;
import novite.rs.utility.game.TeleportLocations;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 6, 2014
 */
public class StaffZone extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.PLAYER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "staffzone", "sz" };
	}

	@Override
	public void execute(Player player) {
		if (player.isSupporter() || player.getRights() > 0) {
			Magic.sendNormalTeleportSpell(player, 1, 0, TeleportLocations.STAFF_ZONE);
		}
	}

}
