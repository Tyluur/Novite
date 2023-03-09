package novite.rs.api.event.command.impl;

import novite.rs.api.event.EventManager;
import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 22, 2014
 */
public class ReloadEvents extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "rle" };
	}

	@Override
	public void execute(Player player) {
		EventManager.get().load();
	}

}
