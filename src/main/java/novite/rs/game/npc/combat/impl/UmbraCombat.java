package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.minigames.ZarosGodwars;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class UmbraCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 13452 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		//npc.setNextGraphics(new Graphics(3357));
		for (final Entity t : ZarosGodwars.nex.getPossibleTargets()) {
			World.sendProjectile(npc, t, 380, 20, 20, 20, 1, 0, 0);
			int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, t);
			delayHit(npc, 1, t, getMagicHit(npc, damage));
			if (damage > 0 && Utils.getRandom(5) == 0) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						if (t instanceof Player) {
							((Player) t).getSkills().drainLevel(Skills.ATTACK, (int) (((Player) t).getSkills().getLevel(Skills.ATTACK) * 0.1));
						}
						t.setNextGraphics(new Graphics(381));
					}

				}, 2);
			}
		}
		return defs.getAttackDelay();
	}
}