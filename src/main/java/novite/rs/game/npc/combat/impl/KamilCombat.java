package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.ForceTalk;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;

/**
 * 
 * @author Tyluur <itstyluur@gmail.com>
 * @since 2012-12-12
 */
public class KamilCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Kamil" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target);
		if (Utils.random(10) > 5) {
			npc.setNextForceTalk(new ForceTalk("Sallamakar Ro!"));
			npc.setNextAnimation(new Animation(1979));
			World.sendProjectile(npc, target, 368, 60, 32, 50, 50, 0, 0);
			delayHit(npc, 2, target, getMagicHit(npc, damage));
			if (damage > 0 && target.getFrozenBlockedDelay() < Utils.currentTimeMillis()) {
				if (target instanceof Player) {
					((Player) target).sendMessage("You have been frozen!");
				}
				target.addFreezeDelay(5000, true);
				target.setNextGraphics(new Graphics(369));
			}
		} else {
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, damage));
		}
		return defs.getAttackDelay();
	}

}
