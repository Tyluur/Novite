package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.npc.familiar.Familiar;
import novite.rs.game.player.controlers.impl.Wilderness;
import novite.rs.utility.Utils;

public class MossTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7330, 7329 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// priority over regular attack
			npc.setNextAnimation(new Animation(8223));
			npc.setNextGraphics(new Graphics(1460));
			for (Entity targets : npc.getPossibleTargets()) {
				if (targets.equals(target) && !targets.isAtMultiArea()) {
					continue;
				}
				sendSpecialAttack(targets, npc);
			}
			sendSpecialAttack(target, npc);
		} else {
			damage = getRandomMaxHit(npc, 160, NPCCombatDefinitions.MELEE, target);
			npc.setNextAnimation(new Animation(8222));
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		}
		return defs.getAttackDelay();
	}

	public void sendSpecialAttack(Entity target, NPC npc) {
		if (target.isAtMultiArea() && Wilderness.isAtWild(target)) {
			delayHit(npc, 1, target, getMagicHit(npc, getRandomMaxHit(npc, 160, NPCCombatDefinitions.MAGE, target)));
			World.sendProjectile(npc, target, 1462, 34, 16, 30, 35, 16, 0);
			if (Utils.getRandom(3) == 0) {
				target.getPoison().makePoisoned(58);
			}
		}
	}
}
