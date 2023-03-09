package novite.rs.game.npc.combat.impl;

import novite.rs.game.Entity;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since May 4, 2014
 */
public class SappingGlacyte extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 14303 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		if (target instanceof Player) {
			target.player().getPrayer().drainPrayer((int) (target.player().getPrayer().getPrayerpoints() * .1));
		}
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		return defs.getAttackDelay();
	}

}
