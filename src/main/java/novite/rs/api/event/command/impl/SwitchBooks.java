package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 28, 2014
 */
public class SwitchBooks extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "switchbook" };
	}

	@Override
	public void execute(Player player) {
		String completed = getCompleted(cmd, 1);
		try {
			boolean prayer = completed.contains("-pray");
			if (prayer) {
				player.getPrayer().setPrayerBook(!player.getPrayer().isAncientCurses());
			} else {
				int book = Integer.parseInt(completed.split(" ")[1]);
				player.getCombatDefinitions().setSpellBook(book);
			}
		} catch (Exception e) {
			player.sendMessage("Use as: switchbook [-pray/-book] [0-2]");
		}
	}
}
