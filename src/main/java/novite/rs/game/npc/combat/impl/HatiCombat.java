package novite.rs.game.npc.combat.impl;

import novite.rs.game.Entity;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;

public class HatiCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Hati" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();

		return defs.getAttackDelay();
	}
}
