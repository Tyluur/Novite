package novite.rs.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.HashMap;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.Hit;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class LucienCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 14256 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utils.getRandom(5);

		if (Utils.getRandom(10) == 0) {
			ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
			final HashMap<String, int[]> tiles = new HashMap<String, int[]>();
			for (Entity t : possibleTargets) {
				String key = t.getX() + "_" + t.getY();
				if (!tiles.containsKey(t.getX() + "_" + t.getY())) {
					tiles.put(key, new int[] { t.getX(), t.getY() });
					World.sendProjectile(npc, new WorldTile(t.getX(), t.getY(), npc.getPlane()), 1900, 34, 0, 30, 35, 16, 0);
				}
			}
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
					for (int[] tile : tiles.values()) {

						World.sendGraphics(null, new Graphics(1896), new WorldTile(tile[0], tile[1], 0));
						for (Entity t : possibleTargets) {
							if (t.getX() == tile[0] && t.getY() == tile[1]) {
								t.applyHit(new Hit(npc, Utils.random(100, 200), HitLook.REGULAR_DAMAGE));
							}
						}
					}
					stop();
				}

			}, 5);
		}
		if (attackStyle == 0) { // normal mage move
			npc.setNextAnimation(new Animation(11338));
			delayHit(npc, 2, target, getMagicHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target)));
			World.sendProjectile(npc, target, 2963, 34, 16, 40, 35, 16, 0);
		} else if (attackStyle == 1) { // normal mage move
			npc.setNextAnimation(new Animation(11338));
			delayHit(npc, 2, target, getRangeHit(npc, getRandomMaxHit(npc, Utils.random(100, 200), NPCCombatDefinitions.RANGE, target)));
			World.sendProjectile(npc, target, 1904, 34, 16, 30, 35, 16, 0);

			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					target.setNextGraphics(new Graphics(1910));
				}

			}, 2);

		} else if (attackStyle == 2) {
			npc.setNextAnimation(new Animation(11318));
			npc.setNextGraphics(new Graphics(1901));
			World.sendProjectile(npc, target, 1899, 34, 16, 30, 95, 16, 0);
			delayHit(npc, 4, target, getMagicHit(npc, getRandomMaxHit(npc, Utils.random(100, 200), NPCCombatDefinitions.MAGE, target)));
		} else if (attackStyle == 3) {
			npc.setNextAnimation(new Animation(11373));
			npc.setNextGraphics(new Graphics(1898));
			target.setNextGraphics(new Graphics(2954));
			delayHit(npc, 2, target, getRegularHit(npc, Utils.random(100, 200)));
		} else if (attackStyle == 4) {
			npc.setNextAnimation(new Animation(11364));
			npc.setNextGraphics(new Graphics(2600));
			npc.setCantInteract(true);
			npc.getCombat().removeTarget();
			final int damage = Utils.random(100, 300);
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					for (Entity t : npc.getPossibleTargets()) {
						t.applyHit(new Hit(npc, damage, HitLook.REGULAR_DAMAGE, 0));
					}
					npc.getCombat().addCombatDelay(3);
					npc.setCantInteract(false);
					npc.setTarget(target);
				}

			}, 4);
			return 0;
		}
		return defs.getAttackDelay();
	}
}
