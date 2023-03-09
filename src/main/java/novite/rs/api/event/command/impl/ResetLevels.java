package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class ResetLevels extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "reset" };
	}

	@Override
	public void execute(Player player) {
		for (int i = 0; i < Skills.SKILL_NAME.length; i++) {
			int level = (i != 3 ? 1 : 10);
			player.getSkills().set(i, level);
			player.getSkills().setXp(i, Skills.getXPForLevel(level));
		}
	}

}
