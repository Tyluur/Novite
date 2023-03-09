package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;

public class MagicalMeleeCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Jelly", "Bloodveld" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, target)));
		npc.setNextAnimation(new Animation(def.getAttackEmote()));
		return def.getAttackDelay();
	}
}
