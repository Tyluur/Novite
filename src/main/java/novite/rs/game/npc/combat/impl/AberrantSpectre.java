package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.slayer.Slayer;
import novite.rs.utility.Utils;

public class AberrantSpectre extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Aberrant spectre" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		if (!Slayer.hasNosepeg(target)) {
			Player targetPlayer = (Player) target;
			if (!targetPlayer.getPrayer().isMageProtecting()) {
				int randomSkill = Utils.random(0, 6);
				int currentLevel = targetPlayer.getSkills().getLevel(randomSkill);
				targetPlayer.getSkills().set(randomSkill, currentLevel < 5 ? 0 : currentLevel - 5);
				targetPlayer.getPackets().sendGameMessage("The smell of the abberrant spectre make you feel slightly weaker.");
			}
			delayHit(npc, 1, target, getMagicHit(npc, targetPlayer.getMaxHitpoints() / 10));
			// TODO player emote hands on ears
		} else {
			delayHit(npc, 1, target, getMagicHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), def.getAttackStyle(), target)));
		}
		World.sendProjectile(npc, target, def.getAttackProjectile(), 18, 18, 50, 25, 0, 0);
		npc.setNextAnimation(new Animation(def.getAttackEmote()));
		return def.getAttackDelay();
	}
}
