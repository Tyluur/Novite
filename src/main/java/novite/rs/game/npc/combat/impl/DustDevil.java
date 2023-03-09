package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Hit;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.slayer.Slayer;
import novite.rs.utility.Utils;

public class DustDevil extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 1624 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		if (!Slayer.hasMask(target)) {
			Player targetPlayer = (Player) target;
			targetPlayer.applyHit(new Hit(npc, Utils.random(20), HitLook.REGULAR_DAMAGE));
			targetPlayer.getPackets().sendGameMessage("The dust devil's smoke suffocates you.");
			delayHit(npc, 1, target, getMeleeHit(npc, targetPlayer.getMaxHitpoints() / 4));
		} else {
			delayHit(npc, 1, target, getMeleeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), def.getAttackStyle(), target)));
		}
		npc.setNextAnimation(new Animation(def.getAttackEmote()));
		return def.getAttackDelay();
	}

}
