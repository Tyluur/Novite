package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 6, 2014
 */
public class Unjail extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.PLAYER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "unjail" };
	}

	@Override
	public void execute(Player player) {
		if (player.isSupporter() || player.getRights() > 0) {
			String name = getCompleted(cmd, 1).replaceAll("_", " ");
			Player target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage("No such player by the name: " + name);
				return;
			}
			target.setJailed(System.currentTimeMillis());
		}
	}

}
