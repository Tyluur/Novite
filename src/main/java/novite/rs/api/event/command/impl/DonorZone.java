package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.DonatorZone;
import novite.rs.utility.game.Rights;

public class DonorZone extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.PLAYER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "dz" };
	}

	@Override
	public void execute(Player player) {
		if (player.isDonator() || player.getRights() == 3) {
			DonatorZone.enterDonatorzone(player);
		}
	}

}
