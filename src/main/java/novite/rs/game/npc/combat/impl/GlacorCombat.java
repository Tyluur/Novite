package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.Hit;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.bosses.glacor.Glacor;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class GlacorCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 14301};
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		Glacor glacor = (Glacor) npc;
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (target instanceof Player) {
			switch (Utils.getRandom(5)) {
			case 0:
			case 1:
			case 2:
				sendDistancedAttack(glacor, target);
				break;
			case 3:
				if (Utils.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
					npc.setNextAnimation(new Animation(9955));
					delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, 350, NPCCombatDefinitions.MELEE, target)));
				} else {
					sendDistancedAttack(glacor, target);
				}
				break;
			case 4:
				final WorldTile tile = new WorldTile(target);
				npc.setNextAnimation(new Animation(9955));
				World.sendProjectile(npc, tile, 2314, 50, 0, 46, 20, 0, 10);
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						for (Entity e : npc.getPossibleTargets(false, true)) {
							if (e instanceof Player) {
								Player player = (Player) e;
								if (player.withinDistance(tile, 0)) {
									player.applyHit(new Hit(npc, player.getHitpoints() / 2, HitLook.RANGE_DAMAGE));
								}
								player.getPackets().sendGraphics(new Graphics(2315), tile);
							}
						}
					}
				}, 3);
				break;
			}
		}
		return defs.getAttackDelay();
	}

	private void sendDistancedAttack(Glacor npc, final Entity target) {
		boolean isRangedAttack = Utils.random(2) == 1;
		if (isRangedAttack) {
			delayHit(npc, 2, target, getRangeHit(npc, getRandomMaxHit(npc, 294, NPCCombatDefinitions.RANGE, target)));
			World.sendProjectile(npc, target, 962, 50, 30, 46, 30, 0, 10);
		} else {
			delayHit(npc, 2, target, getMagicHit(npc, getRandomMaxHit(npc, 264, NPCCombatDefinitions.MAGE, target)));
			World.sendProjectile(npc, target, 634, 50, 30, 46, 30, 5, 10);
			if (Utils.random(5) == 0) {
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						target.setNextGraphics(new Graphics(369));
						target.addFreezeDelay(10000); // ten seconds
					}
				});
			}
		}
		npc.setNextAnimation(new Animation(isRangedAttack ? 9968 : 9967));
	}

}
