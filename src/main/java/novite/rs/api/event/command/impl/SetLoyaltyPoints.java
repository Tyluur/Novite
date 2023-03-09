package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 26, 2014
 */
public class SetLoyaltyPoints extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "setlp" };
	}

	@Override
	public void execute(Player player) {
		player.getLoyaltyManager().setPoints(Integer.parseInt(cmd[1]));
	}

}
