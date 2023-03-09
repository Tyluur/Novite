package novite.rs.api.event.command.impl;

import java.util.concurrent.TimeUnit;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.api.input.IntegerInputAction;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 6, 2014
 */
public class JailCommand extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.PLAYER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "jail" };
	}

	@Override
	public void execute(final Player player) {
		if (player.isSupporter() || player.getRights() > 0) {
			String name = getCompleted(cmd, 1).replaceAll("_", " ");
			final Player target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage("No such player by the name: " + name);
				return;
			}
			player.getPackets().sendInputIntegerScript("How many hours should they be jailed for?", new IntegerInputAction() {

				@Override
				public void handle(int input) {
					long hours = TimeUnit.HOURS.toMillis(input);
					target.setJailed(System.currentTimeMillis() + hours);
					target.getControllerManager().startController("JailControler");

					hours = input;
					target.sendMessage("You have been jailed for " + hours + " hours!");
					player.sendMessage("You have jailed " + target.getDisplayName() + " for " + hours + " hours.");
				}
			});
		}
	}

}
