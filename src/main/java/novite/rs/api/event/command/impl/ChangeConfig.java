package novite.rs.api.event.command.impl;

import novite.rs.Constants;
import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.utility.Config;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Aug 2, 2014
 */
public class ChangeConfig extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "changeconfig" };
	}

	@Override
	public void execute(Player player) {
		Config.get().changeConfig(cmd[1], cmd[2]);
		switch (cmd[1].toLowerCase()) {
		case "double_exp":
			Constants.isDoubleExp = Config.get().getBoolean("double_exp");
			World.sendWorldMessage("Double experience is now " + (Constants.isDoubleExp ? "en" : "dis") + "abled!", false, false);
			break;
		case "double_votes":
			Constants.isDoubleVotes = Config.get().getBoolean("double_votes");
			World.sendWorldMessage("Double vote tokens is now " + (Constants.isDoubleVotes ? "en" : "dis") + "abled!", false, false);
			break;
		}
	}

}
