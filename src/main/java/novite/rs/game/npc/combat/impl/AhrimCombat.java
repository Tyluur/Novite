package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.utility.Utils;

public class AhrimCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2025 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target);
		if (damage != 0 && target instanceof Player && Utils.random(6) == 0) {
			target.setNextGraphics(new Graphics(400, 0, 100));
			Player targetPlayer = (Player) target;
			int currentLevel = targetPlayer.getSkills().getLevel(Skills.STRENGTH);
			targetPlayer.getSkills().set(Skills.STRENGTH, currentLevel < 5 ? 0 : currentLevel - 5);
		}
		World.sendProjectile(npc, target, defs.getAttackProjectile(), 41, 16, 41, 35, 16, 0);
		npc.setNextGraphics(new Graphics(defs.getAttackGfx()));
		delayHit(npc, 2, target, getMagicHit(npc, damage));
		return defs.getAttackDelay();
	}
}
