package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.Saving;
import novite.rs.utility.game.Rights;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.PunishmentLoader;
import novite.rs.utility.game.punishments.Punishment.PunishmentType;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 24, 2014
 */
public class UnipbanCommand extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.ADMINISTRATOR;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "unipban" };
	}

	@Override
	public void execute(Player player) {
		final PunishmentLoader loader = JsonHandler.getJsonLoader(PunishmentLoader.class);
		final String name = getCompleted(cmd, 1).replaceAll(" ", "_");
		Player target = Saving.loadPlayer(name);
		if (target == null) {
			player.sendMessage("No such player: " + name);
			return;
		}
		if (!loader.forceRemovePunishment(target.getLastIP(), PunishmentType.IPBAN)) {
			player.sendMessage("No such ipban was found!");
		} else {
			player.sendMessage("You have unpunished: " + target.getUsername());
		}
	}

}
