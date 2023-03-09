package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.npc.familiar.Familiar;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;

public class LavaTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7342, 7341 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// priority over regular attack
			npc.setNextAnimation(new Animation(7883));
			npc.setNextGraphics(new Graphics(1491));
			delayHit(npc, 1, target, getMeleeHit(npc, getRandomMaxHit(npc, 140, NPCCombatDefinitions.MELEE, target)));
			if (damage <= 4 && target instanceof Player) {
				Player player = (Player) target;
				player.getCombatDefinitions().desecreaseSpecialAttack((player.getCombatDefinitions().getSpecialAttackPercentage() / 10));
			}
		} else {
			damage = getRandomMaxHit(npc, 140, NPCCombatDefinitions.MELEE, target);
			npc.setNextAnimation(new Animation(7980));
			npc.setNextGraphics(new Graphics(1490));
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		}
		if (Utils.getRandom(10) == 0) {
			delayHit(npc, 1, target, getMeleeHit(npc, Utils.getRandom(50)));
		}
		return defs.getAttackDelay();
	}
}
