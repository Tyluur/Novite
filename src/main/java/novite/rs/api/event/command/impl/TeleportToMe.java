package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 4, 2014
 */
public class TeleportToMe extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.MODERATOR;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "teletome" };
	}

	@Override
	public void execute(Player player) {
		String name = getCompleted(cmd, 1).replaceAll("_", " ");
		Player target = World.getPlayerByDisplayName(name);
		if (target != null) {
			target.setNextWorldTile(player);
			target.sendMessage("You have been teleported to " + player.getDisplayName());
		} else {
			player.sendMessage("No such player.");
		}
	}

}
