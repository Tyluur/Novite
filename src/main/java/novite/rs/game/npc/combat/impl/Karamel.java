package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;

public class Karamel extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 3495 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(-1));
		npc.setNextAnimation(new Animation(1979));
		int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target);
		if (damage != 0) {
			target.setNextGraphics(new Graphics(369, 0, 100));
		}
		World.sendProjectile(npc, target, 368, 60, 32, 50, 50, 0, 0);
		delayHit(npc, 2, target, getMagicHit(npc, damage));
		return defs.getAttackDelay();
	}

}
