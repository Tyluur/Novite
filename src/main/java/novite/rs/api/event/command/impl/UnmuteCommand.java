package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
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
public class UnmuteCommand extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.MODERATOR;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "unmute" };
	}

	@Override
	public void execute(final Player player) {
		final PunishmentLoader loader = JsonHandler.getJsonLoader(PunishmentLoader.class);
		String name = getCompleted(cmd, 1).replaceAll("_", " ");
		Player target = World.getPlayerByDisplayName(name);
		if (target != null) {
			loader.forceRemovePunishment(target.getUsername(), PunishmentType.MUTE);
			player.sendMessage("You have unpunished: " + target.getUsername());
		} else {
			player.sendMessage("No such player.");
		}
	}

}
