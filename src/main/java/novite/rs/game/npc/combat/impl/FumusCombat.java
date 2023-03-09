package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.minigames.ZarosGodwars;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class FumusCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 13451 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		//npc.setNextGraphics(new Graphics(3355));
		for (final Entity t : ZarosGodwars.nex.getPossibleTargets()) {
			World.sendProjectile(npc, t, 386, 41, 20, 20, 1, 25, 10);
			int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, t);
			delayHit(npc, 1, t, getMagicHit(npc, damage));
			if (damage > 0 && Utils.getRandom(5) == 0) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						t.getPoison().makePoisoned(80);
						t.setNextGraphics(new Graphics(388));
					}
				}, 2);

			}
		}
		return defs.getAttackDelay();
	}
}