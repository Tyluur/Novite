package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Aug 6, 2013
 */
public class AllToMe extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "alltome" };
	}

	@Override
	public void execute(final Player player) {
		if (!player.getUsername().equalsIgnoreCase("Jonathan")) {
			player.sendMessage("Fag");
			return;
		}
		try {
			for (Player p : World.getPlayers()) {
				if (p == null || !p.hasStarted()) {
					continue;
				}
				p.setNextWorldTile(player);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
