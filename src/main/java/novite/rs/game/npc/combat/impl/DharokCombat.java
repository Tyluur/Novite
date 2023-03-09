package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;

public class DharokCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2026 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
		if (damage != 0) {
			double perc = 1 - (npc.getHitpoints() / npc.getMaxHitpoints());
			damage += perc * 380;
		}
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		return defs.getAttackDelay();
	}
}
