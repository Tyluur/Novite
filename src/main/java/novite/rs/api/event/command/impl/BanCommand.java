package novite.rs.api.event.command.impl;

import java.util.concurrent.TimeUnit;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.api.input.IntegerInputAction;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.PunishmentLoader;
import novite.rs.utility.game.punishments.Punishment.PunishmentType;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 24, 2014
 */
public class BanCommand extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.MODERATOR;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "ban" };
	}

	@Override
	public void execute(final Player player) {
		final PunishmentLoader loader = JsonHandler.getJsonLoader(PunishmentLoader.class);
		final String name = getCompleted(cmd, 1).replaceAll("_", " ");
		player.getPackets().sendInputIntegerScript("Enter Duration (hours)", new IntegerInputAction() {

			@Override
			public void handle(int input) {
				Player target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.forceLogout();
					loader.addPunishment(target.getUsername(), PunishmentType.BAN, TimeUnit.HOURS.toMillis(input));
					player.sendMessage("You have punished: " + target.getUsername());
				} else {
					loader.addPunishment(name, PunishmentType.BAN, TimeUnit.HOURS.toMillis(input));
				}
			}
		});
	}

}
