package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 20, 2014
 */
public class SpawnNpc extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "npc" };
	}

	@Override
	public void execute(Player player) {
		try {
			World.spawnNPC(Integer.parseInt(cmd[1]), player, -1, true, true);
		} catch (NumberFormatException e) {
			player.getPackets().sendPanelBoxMessage("Use: ::npc id(Integer)");
		}
	}

}
