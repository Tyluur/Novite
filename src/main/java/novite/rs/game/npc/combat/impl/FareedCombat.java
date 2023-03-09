package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;

/**
 * 
 * @author Tyluur <itstyluur@gmail.com>
 * @since 2012-12-15
 */
public class FareedCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 1977 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if ((Math.random() * 100) > 50) {
			int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target);
			npc.setNextAnimation(new Animation(14223));
			target.setNextGraphics(new Graphics(400, 0, 100));
			World.sendProjectile(npc, target, defs.getAttackProjectile(), 41, 16, 41, 35, 16, 0);
			npc.setNextGraphics(new Graphics(defs.getAttackGfx()));
			delayHit(npc, 2, target, getMagicHit(npc, damage));
		} else {
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, 410, NPCCombatDefinitions.MELEE, target)));
		}
		return defs.getAttackDelay() + 2;
	}

}
