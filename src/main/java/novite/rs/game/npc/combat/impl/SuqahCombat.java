package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class SuqahCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Suqah" };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.getRandom(3) == 0) {// barrage
			boolean hit = Utils.getRandom(1) == 0;
			delayHit(npc, 2, target, getMagicHit(npc, hit ? 100 : 0));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					target.setNextGraphics(new Graphics(369));
					target.addFreezeDelay(5000);
				}
			});
		} else {
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), defs.getAttackStyle(), target)));
		}
		return defs.getAttackDelay();
	}
}
