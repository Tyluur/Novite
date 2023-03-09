package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 19, 2014
 */
public class RemoveObjectSpawns extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "rmospns" };
	}

	@Override
	public void execute(Player player) {
		player.getTemporaryAttributtes().put("removing_object_spawns", true);
	}

}
