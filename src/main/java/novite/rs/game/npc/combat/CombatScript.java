package novite.rs.game.npc.combat;

import java.util.ArrayList;
import java.util.List;

import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.Hit;
import novite.rs.game.Region;
import novite.rs.game.World;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.familiar.Steeltitan;
import novite.rs.game.npc.godwars.zaros.Nex;
import novite.rs.game.player.CombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.actions.PlayerCombat;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public abstract class CombatScript {

	/*
	 * Returns ids and names
	 */
	public abstract Object[] getKeys();

	/*
	 * Returns Move Delay
	 */
	public abstract int attack(NPC npc, Entity target);

	public Entity[] getMultiAttackTargets(Entity entity, Entity target) {
		return getMultiAttackTargets(entity, target, 1, 9);
	}

	public Entity[] getMultiAttackTargets(Entity entity, Entity target, int maxDistance, int maxAmtTargets) {
		List<Entity> possibleTargets = new ArrayList<Entity>();
		possibleTargets.add(target);
		if (target.isAtMultiArea()) {
			y:
				for (int regionId : target.getMapRegionsIds()) {
					Region region = World.getRegion(regionId);
					if (target instanceof Player) {
						List<Integer> playerIndexes = region.getPlayerIndexes();
						if (playerIndexes == null) {
							continue;
						}
						for (int playerIndex : playerIndexes) {
							Player p2 = World.getPlayers().get(playerIndex);
							if (p2 == null || p2 == entity || p2 == target || p2.isDead() || !p2.hasStarted() || p2.hasFinished() || !p2.isCanPvp() || !p2.isAtMultiArea() || !p2.withinDistance(target, maxDistance) || (entity instanceof Player && !((Player) entity).getControllerManager().canHit(p2))) {
								continue;
							}
							possibleTargets.add(p2);
							if (possibleTargets.size() == maxAmtTargets) {
								break y;
							}
						}
					} else {
						List<Integer> npcIndexes = region.getNPCsIndexes();
						if (npcIndexes == null) {
							continue;
						}
						for (int npcIndex : npcIndexes) {
							NPC n = World.getNPCs().get(npcIndex);
							if (n == null || n == target || (entity instanceof Player && n == ((Player) entity).getFamiliar()) || n.isDead() || n.hasFinished() || !n.isAtMultiArea() || !n.withinDistance(target, maxDistance) || !n.getDefinitions().hasAttackOption() || (entity instanceof Player && !((Player) entity).getControllerManager().canHit(n))) {
								continue;
							}
							possibleTargets.add(n);
							if (possibleTargets.size() == maxAmtTargets) {
								break y;
							}
						}
					}
				}
		}
		return possibleTargets.toArray(new Entity[possibleTargets.size()]);
	}

	public static void delayHit(NPC npc, int delay, final Entity target, final Hit... hits) {

		npc.getCombat().addAttackedByDelay(target);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				for (Hit hit : hits) {
					NPC npc = (NPC) hit.getSource();
					if (npc.isDead() || npc.hasFinished() || target.isDead() || target.hasFinished()) {
						return;
					}
					target.applyHit(hit);
					npc.getCombat().doDefenceEmote(target);
					if (!(npc instanceof Nex) && hit.getLook() == HitLook.MAGIC_DAMAGE && hit.getDamage() == 0) {
						target.setNextGraphics(new Graphics(85, 0, 100));
					}
					if (target instanceof Player) {
						Player p2 = (Player) target;
						if (p2.getCombatDefinitions().isAutoRelatie() && !p2.getActionManager().hasSkillWorking() && !p2.hasWalkSteps()) {
							p2.closeInterfaces();
							p2.getActionManager().setAction(new PlayerCombat(npc));
						}
					} else {
						NPC n = (NPC) target;
						if (!n.isUnderCombat() || n.canBeAttackedByAutoRelatie()) {
							n.setTarget(npc);
						}
					}

				}
			}

		}, delay);
	}

	public static Hit getRangeHit(NPC npc, int damage) {
		return new Hit(npc, damage, HitLook.RANGE_DAMAGE);
	}

	public static Hit getMagicHit(NPC npc, int damage) {
		return new Hit(npc, damage, HitLook.MAGIC_DAMAGE);
	}

	public static Hit getRegularHit(NPC npc, int damage) {
		return new Hit(npc, damage, HitLook.REGULAR_DAMAGE);
	}

	public static Hit getMeleeHit(NPC npc, int damage) {
		return new Hit(npc, damage, HitLook.MELEE_DAMAGE);
	}

	public static int getRandomMaxHit(NPC npc, int maxHit, int attackStyle, Entity target) {
		int[] bonuses = npc.getBonuses();
		double att = bonuses == null ? 0 : attackStyle == NPCCombatDefinitions.RANGE ? bonuses[CombatDefinitions.RANGE_ATTACK] : attackStyle == NPCCombatDefinitions.MAGE ? bonuses[CombatDefinitions.MAGIC_ATTACK] : bonuses[CombatDefinitions.STAB_ATTACK];
		double def;
		if (target instanceof Player) {
			Player p2 = (Player) target;
			def = p2.getSkills().getLevel(Skills.DEFENCE) + (attackStyle == NPCCombatDefinitions.RANGE ? 1.25 : attackStyle == NPCCombatDefinitions.MAGE ? 2.0 : 1.0) * p2.getCombatDefinitions().getBonuses()[attackStyle == NPCCombatDefinitions.RANGE ? CombatDefinitions.RANGE_DEF : attackStyle == NPCCombatDefinitions.MAGE ? CombatDefinitions.MAGIC_DEF : CombatDefinitions.STAB_DEF];
			def *= p2.getPrayer().getDefenceMultiplier();
			if (attackStyle == NPCCombatDefinitions.MELEE) {
				if (p2.getFamiliar() instanceof Steeltitan) {
					def *= 1.15;
				}
			}
		} else {
			NPC n = (NPC) target;
			def = n.getBonuses() == null ? 0 : n.getBonuses()[attackStyle == NPCCombatDefinitions.RANGE ? CombatDefinitions.RANGE_DEF : attackStyle == NPCCombatDefinitions.MAGE ? CombatDefinitions.MAGIC_DEF : CombatDefinitions.STAB_DEF];
		}
		double prob = att / def;
		if (prob > 0.90) {
			prob = 0.90;
		} else if (prob < 0.05) {
			prob = 0.05;
		}
		if (prob < Math.random()) {
			return 0;
		}
		return Utils.getRandom(maxHit);
	}

}
