package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;

public class ClayFamiliarCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 8241, 8243, 8245, 8247, 8249 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		if (target instanceof Player) {
			Player player = (Player) target;
			if (player.getAppearence().isNPC()) {
				npc.getCombat().removeTarget();
				return def.getAttackDelay();
			}
		}
		delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), def.getAttackStyle(), target)));
		npc.setNextAnimation(new Animation(def.getAttackEmote()));
		return def.getAttackDelay();
	}
}
