package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;

public class FakeNomadCombat extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 8529 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(12697));
		boolean hit = getRandomMaxHit(npc, 50, NPCCombatDefinitions.MAGE, target) != 0;
		delayHit(npc, 2, target, getRegularHit(npc, hit ? 50 : 0));
		World.sendProjectile(npc, target, 1657, 30, 30, 75, 25, 0, 0);
		if (hit) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					target.setNextGraphics(new Graphics(2278, 0, 100));
				}
			}, 1);
		}
		return defs.getAttackDelay();
	}

}
