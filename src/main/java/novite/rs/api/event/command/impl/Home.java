package novite.rs.api.event.command.impl;

import novite.rs.Constants;
import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.Magic;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 18, 2014
 */
public class Home extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.PLAYER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "home" };
	}

	@Override
	public void execute(Player player) {
		Magic.sendNormalTeleportSpell(player, 1, 0, Constants.HOME_TILE);
	}

}
