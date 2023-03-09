package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;
import novite.rs.utility.game.Rights;
import novite.rs.utility.game.json.impl.PunishmentLoader;
import novite.rs.utility.game.punishments.Punishment.PunishmentType;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 3, 2014
 */
public class Yell extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.PLAYER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "shout", "yell" };
	}

	@Override
	public void execute(Player player) {
		if (PunishmentLoader.isPunished(player.getUsername(), PunishmentType.MUTE) || PunishmentLoader.isPunished(player.getSession().getIP(), PunishmentType.IPMUTE)) {
			player.sendMessage("You are muted. Check back in 48 hours.");
			return;
		}
		if (!player.isDonator() && !player.isSupporter() && player.getRights() == 0)
			return;
		
		String message = getCompleted(cmd, 1);
		if (message == null || message.equalsIgnoreCase("null"))
			return;

		message = Utils.fixChatMessage(message.replaceAll("<", ""));
		
		World.sendWorldMessage("<img=" + player.getChatIcon() + ">" + player.getDisplayName() + ": <col=0D747F>" + message, false, true);
	}

}
