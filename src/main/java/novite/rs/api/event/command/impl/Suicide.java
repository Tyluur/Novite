package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.Hit;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

public class Suicide extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "die", "suicide" };
	}

	@Override
	public void execute(Player player) {
		player.removeHitpoints(new Hit(player, Short.MAX_VALUE, HitLook.MELEE_DAMAGE));
	}

}
