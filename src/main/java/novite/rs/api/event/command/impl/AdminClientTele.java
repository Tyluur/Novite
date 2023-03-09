package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 19, 2014
 */
public class AdminClientTele extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.ADMINISTRATOR;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "tele" };
	}

	@Override
	public void execute(Player player) {
		cmd = cmd[1].split(",");
		int plane = Integer.valueOf(cmd[0]);
		int x = Integer.valueOf(cmd[1]) << 6 | Integer.valueOf(cmd[3]);
		int y = Integer.valueOf(cmd[2]) << 6 | Integer.valueOf(cmd[4]);
		player.setNextWorldTile(new WorldTile(x, y, plane));
	}

}
