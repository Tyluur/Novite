package novite.rs.api.event.command.impl;

import novite.rs.Constants;
import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Jul 12, 2013
 */
public class SendHome extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.MODERATOR;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "sendhome", "telehome" };
	}

	@Override
	public void execute(Player player) {
		String[] cmd = command.split(" ");
		String name = getCompleted(cmd, 1).replaceAll("_", " ");
		Player target = World.getPlayerByDisplayName(name);
		if (target != null) {
			target.getControllerManager().forceStop();
			target.setNextWorldTile(Constants.HOME_TILE);
			target.sendMessage(player.getDisplayName() + " has teleported you home.");
		} else {
			player.sendMessage(name + " is an invalid name for a player to teleport to.");
		}
	}

}
