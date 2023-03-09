package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;

public class SpiritualMage extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6257, 6221, 6278 };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target);
		npc.setNextGraphics(new Graphics(defs.getAttackGfx()));
		delayHit(npc, 2, target, getMagicHit(npc, damage));
		if (damage > 0) {
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					target.setNextGraphics(new Graphics(defs.getAttackProjectile()));
				}
			}, 2);
		}
		return defs.getAttackDelay() + 2;
	}

}
