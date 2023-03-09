package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.PunishmentLoader;
import novite.rs.utility.game.punishments.Punishment.PunishmentType;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 24, 2014
 */
public class UnbanCommand extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.ADMINISTRATOR;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "unban" };
	}

	@Override
	public void execute(Player player) {
		final PunishmentLoader loader = JsonHandler.getJsonLoader(PunishmentLoader.class);
		final String name = getCompleted(cmd, 1).replaceAll(" ", "_");
		if (!loader.forceRemovePunishment(name, PunishmentType.BAN)) {
			player.sendMessage("No such ban was found!");
		} else {
			player.sendMessage("You have unpunished: " + name);
		}
	}

}
