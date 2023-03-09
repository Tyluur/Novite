package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;

public class SapGlacite extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 14303 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (target instanceof Player) {
			Player player = (Player) target;
			player.getPrayer().drainPrayer((int) (player.getPrayer().getPrayerpoints() * .05));
		}
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), defs.getAttackStyle(), target)));
		return defs.getAttackDelay();
	}
}
