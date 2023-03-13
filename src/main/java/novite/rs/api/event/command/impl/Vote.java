package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 1, 2014
 */
public class Vote extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.PLAYER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "vote" };
	}

	@Override
	public void execute(Player player) {
		player.getPackets().sendOpenURL("http://lazarus.runetoplist.com/vote?theme=default");
	}

}
