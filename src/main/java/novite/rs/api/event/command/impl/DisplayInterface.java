package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 20, 2014
 */
public class DisplayInterface extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "inter", "interface" };
	}

	@Override
	public void execute(Player player) {
		int interfaceId = Integer.parseInt(cmd[1]);
		player.getInterfaceManager().sendInterface(interfaceId);
	}

}
