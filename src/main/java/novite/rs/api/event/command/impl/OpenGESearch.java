package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 22, 2014
 */
public class OpenGESearch extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "gesearch" };
	}

	@Override
	public void execute(Player player) {
		player.getPackets().sendConfig1(744, 0);
		player.getPackets().sendConfig(1113, 0);
		player.getPackets().sendInterface(true, 752, 7, 389);
		player.getPackets().sendRunScript(570, new Object[] { "Grand Exchange Item Search" });
	}

}
