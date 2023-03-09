package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.ForceTalk;
import novite.rs.game.Hit;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.utility.Utils;

/**
 * 
 * @author Tyluur <itstyluur@gmail.com>
 * @since 2012-12-15
 */
public class DessousCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Dessous" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.random(5) > 3) {
			delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
		} else {
			npc.setNextForceTalk(new ForceTalk("Hsssssssssss"));
			npc.setNextAnimation(new Animation(10501));
			target.applyHit(new Hit(target, 50, HitLook.REGULAR_DAMAGE));
			target.applyHit(new Hit(target, 50, HitLook.REGULAR_DAMAGE));
		}
		return defs.getAttackDelay();
	}

}
