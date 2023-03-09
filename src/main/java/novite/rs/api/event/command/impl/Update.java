package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 24, 2014
 */
public class Update extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "update" };
	}

	@Override
	public void execute(Player player) {
		int delay = 60;
		if (cmd.length >= 2) {
			try {
				delay = Integer.valueOf(cmd[1]);
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::restart secondsDelay(IntegerValue)");
				return;
			}
		}
		World.safeShutdown(false, delay);
	}

}
