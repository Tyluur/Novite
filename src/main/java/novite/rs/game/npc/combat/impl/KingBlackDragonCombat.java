package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Hit;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.Combat;
import novite.rs.utility.Utils;

public class KingBlackDragonCombat extends CombatScript {

	public KingBlackDragonCombat() {
	}

	public Object[] getKeys() {
		return (new Object[] { Integer.valueOf(50) });
	}

	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utils.getRandom(5);
		int size = npc.getSize();
		if (attackStyle == 0) {
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1) {
				attackStyle = Utils.getRandom(4) + 1;
			} else {
				delayHit(npc, 0, target, new Hit[] { getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), 0, target)) });
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				return defs.getAttackDelay();
			}
		} else if (attackStyle == 1 || attackStyle == 2) {
			int damage = Utils.getRandom(650);
			Player player = (target instanceof Player) ? (Player) target : null;
			if (Combat.hasAntiDragProtection(target) || player != null && (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7)))
				damage = 0;
			if (player != null && player.getFireImmune() > System.currentTimeMillis()) {
				if (damage != 0)
					damage = Utils.getRandom(164);
			} else if (damage == 0)
				damage = Utils.getRandom(164);
			else if (player != null)
				player.getPackets().sendGameMessage("You are hit by the dragon's fiery breath!", true);
			delayHit(npc, 2, target, new Hit[] { getRegularHit(npc, damage) });
			World.sendProjectile(npc, target, 393, 34, 16, 30, 35, 16, 0);
			npc.setNextAnimation(new Animation(81));
		} else if (attackStyle == 3) {
			Player player = (target instanceof Player) ? (Player) target : null;
			int damage;
			if (Combat.hasAntiDragProtection(target)) {
				damage = getRandomMaxHit(npc, 164, 2, target);
				if (player != null)
					player.getPackets().sendGameMessage("Your shield absorbs most of the dragon's poisonous breath!", true);
			} else if (player != null && (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7))) {
				damage = getRandomMaxHit(npc, 164, 2, target);
				if (player != null)
					player.getPackets().sendGameMessage("Your prayer absorbs most of the dragon's poisonous breath!", true);
			} else {
				damage = Utils.getRandom(650);
				if (player != null)
					player.getPackets().sendGameMessage("You are hit by the dragon's poisonous breath!", true);
			}
			if (Utils.getRandom(2) == 0)
				target.getPoison().makePoisoned(80);
			delayHit(npc, 2, target, new Hit[] { getRegularHit(npc, damage) });
			World.sendProjectile(npc, target, 394, 34, 16, 30, 35, 16, 0);
			npc.setNextAnimation(new Animation(81));
		} else if (attackStyle == 4) {
			Player player = (target instanceof Player) ? (Player) target : null;
			int damage;
			if (Combat.hasAntiDragProtection(target)) {
				damage = getRandomMaxHit(npc, 164, 2, target);
				if (player != null)
					player.getPackets().sendGameMessage("Your shield absorbs most of the dragon's freezing breath!", true);
			} else if (player != null && (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7))) {
				damage = getRandomMaxHit(npc, 164, 2, target);
				if (player != null)
					player.getPackets().sendGameMessage("Your prayer absorbs most of the dragon's freezing breath!", true);
			} else {
				damage = Utils.getRandom(650);
				if (player != null)
					player.getPackets().sendGameMessage("You are hit by the dragon's freezing breath!", true);
			}
			if (Utils.getRandom(2) == 0)
				target.addFreezeDelay(15000L);
			delayHit(npc, 2, target, new Hit[] { getRegularHit(npc, damage) });
			World.sendProjectile(npc, target, 395, 34, 16, 30, 35, 16, 0);
			npc.setNextAnimation(new Animation(81));
		} else {
			Player player = (target instanceof Player) ? (Player) target : null;
			int damage;
			if (Combat.hasAntiDragProtection(target)) {
				damage = getRandomMaxHit(npc, 164, 2, target);
				if (player != null)
					player.getPackets().sendGameMessage("Your shield absorbs most of the dragon's shocking breath!", true);
			} else if (player != null && (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7))) {
				damage = getRandomMaxHit(npc, 164, 2, target);
				if (player != null)
					player.getPackets().sendGameMessage("Your prayer absorbs most of the dragon's shocking breath!", true);
			} else {
				damage = Utils.getRandom(650);
				if (player != null)
					player.getPackets().sendGameMessage("You are hit by the dragon's shocking breath!", true);
			}
			delayHit(npc, 2, target, new Hit[] { getRegularHit(npc, damage) });
			World.sendProjectile(npc, target, 396, 34, 16, 30, 35, 16, 0);
			npc.setNextAnimation(new Animation(81));
		}
		return defs.getAttackDelay();
	}
}
