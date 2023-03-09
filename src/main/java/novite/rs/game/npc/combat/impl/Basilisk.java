package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.slayer.Slayer;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class Basilisk extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Basilisk" };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		if (!Slayer.hasReflectiveEquipment(target)) {
			Player targetPlayer = (Player) target;
			int randomSkill = Utils.random(0, 6);
			int currentLevel = targetPlayer.getSkills().getLevel(randomSkill);
			targetPlayer.getSkills().set(randomSkill, currentLevel < 5 ? 0 : currentLevel - 5);
			delayHit(npc, 0, target, getMeleeHit(npc, targetPlayer.getMaxHitpoints() / 10));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					target.setNextGraphics(new Graphics(747));
				}
			});
			// TODO player emote hands on ears
		} else {
			delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), def.getAttackStyle(), target)));
		}
		npc.setNextAnimation(new Animation(def.getAttackEmote()));
		return def.getAttackDelay();
	}
}
