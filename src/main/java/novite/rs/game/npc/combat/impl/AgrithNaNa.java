package novite.rs.game.npc.combat.impl;

import novite.rs.game.Entity;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;

public class AgrithNaNa extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 3493 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
		return defs.getAttackDelay();
	}

}
