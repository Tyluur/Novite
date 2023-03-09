package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

public class DrainSkill extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "ds" };
	}

	@Override
	public void execute(Player player) {
		int skill = Integer.parseInt(cmd[1]);
		player.getSkills().drainLevel(skill, player.getSkills().getLevel(skill));
	}

}
