package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.npc.familiar.Familiar;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;

public class MinotaurCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6854, 6853, 6856, 6855, 6858, 6857, 6860, 6859, 6862, 6861, 6864, 6863 };
	}

	@Override
	public int attack(final NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		if (usingSpecial) {// priority over regular attack
			familiar.submitSpecial(familiar.getOwner());
			npc.setNextAnimation(new Animation(8026));
			npc.setNextGraphics(new Graphics(1496));
			World.sendProjectile(npc, target, 1497, 34, 16, 30, 35, 16, 0);
			delayHit(npc, 1, target, getMagicHit(npc, getRandomMaxHit(npc, 40, NPCCombatDefinitions.MAGE, target)));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					npc.setNextGraphics(new Graphics(1408));
				}
			});
		} else {
			npc.setNextAnimation(new Animation(8024));
			delayHit(npc, 1, target, getMagicHit(npc, getRandomMaxHit(npc, 40, NPCCombatDefinitions.MAGE, target)));
		}
		return defs.getAttackDelay();
	}
}
