package novite.rs.api.event.command.impl;

import java.util.List;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.Hit;
import novite.rs.game.World;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 30, 2014
 */
public class KillLocalNPCS extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "killnpcs" };
	}

	@Override
	public void execute(Player player) {
		List<Integer> indexes = player.getRegion().getNPCsIndexes();
		for (Integer index : indexes) {
			NPC npc = World.getNPCs().get(index);
			if (npc == null || !npc.getDefinitions().hasAttackOption())
				continue;
			npc.applyHit(new Hit(player, npc.getHitpoints(), HitLook.DESEASE_DAMAGE));
		}
	}

}
