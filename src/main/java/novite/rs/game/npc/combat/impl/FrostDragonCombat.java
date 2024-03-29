package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.Combat;
import novite.rs.utility.Utils;

public class FrostDragonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Frost dragon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		final Player player = target instanceof Player ? (Player) target : null;
		int damage;
		switch (Utils.getRandom(3)) {
			case 0: // Melee
				if (npc.withinDistance(target, 3)) {
					damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
					npc.setNextAnimation(new Animation(defs.getAttackEmote()));
					delayHit(npc, 0, target, getMeleeHit(npc, damage));
				} else {
					damage = Utils.getRandom(650);
					if (Combat.hasAntiDragProtection(target) || (player != null && (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7)))) {
						damage = 0;
						player.getPackets().sendGameMessage("Your " + (Combat.hasAntiDragProtection(target) ? "shield" : "prayer") + " absorbs most of the dragon's breath!", true);
					} else if ((!Combat.hasAntiDragProtection(target) || !player.getPrayer().usingPrayer(0, 17) || !player.getPrayer().usingPrayer(1, 7)) && player.getFireImmune() > Utils.currentTimeMillis()) {
						damage = Utils.getRandom(164);
						player.getPackets().sendGameMessage("Your potion absorbs most of the dragon's breath!", true);
					}
					npc.setNextAnimation(new Animation(13155));
					World.sendProjectile(npc, target, 393, 28, 16, 35, 35, 16, 0);
					delayHit(npc, 1, target, getRegularHit(npc, damage));
				}
				break;
			case 1: // Dragon breath
				if (npc.withinDistance(target, 3)) {
					damage = Utils.getRandom(650);
					if (Combat.hasAntiDragProtection(target) || (player != null && (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7)))) {
						damage = 0;
						player.getPackets().sendGameMessage("Your " + (Combat.hasAntiDragProtection(target) ? "shield" : "prayer") + " absorbs most of the dragon's breath!", true);
					} else if ((!Combat.hasAntiDragProtection(target) || !player.getPrayer().usingPrayer(0, 17) || !player.getPrayer().usingPrayer(1, 7)) && player.getFireImmune() > Utils.currentTimeMillis()) {
						damage = Utils.getRandom(164);
						player.getPackets().sendGameMessage("Your potion fully protects you from the heat of the dragon's breath!", true);
					}
					npc.setNextAnimation(new Animation(13152));
					npc.setNextGraphics(new Graphics(2465));
					delayHit(npc, 1, target, getRegularHit(npc, damage));
				} else {
					damage = Utils.getRandom(650);
					if (Combat.hasAntiDragProtection(target) || (player != null && (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7)))) {
						damage = 0;
						player.getPackets().sendGameMessage("Your " + (Combat.hasAntiDragProtection(target) ? "shield" : "prayer") + " absorbs most of the dragon's breath!", true);
					} else if ((!Combat.hasAntiDragProtection(target) || !player.getPrayer().usingPrayer(0, 17) || !player.getPrayer().usingPrayer(1, 7)) && player.getFireImmune() > Utils.currentTimeMillis()) {
						damage = Utils.getRandom(164);
						player.getPackets().sendGameMessage("Your potion fully protects you from the heat of the dragon's breath!", true);
					}
					npc.setNextAnimation(new Animation(13155));
					World.sendProjectile(npc, target, 393, 28, 16, 35, 35, 16, 0);
					delayHit(npc, 1, target, getRegularHit(npc, damage));
				}
				break;
			case 2: // Range
				damage = Utils.getRandom(250);
				npc.setNextAnimation(new Animation(13155));
				World.sendProjectile(npc, target, 2707, 28, 16, 35, 35, 16, 0);
				delayHit(npc, 1, target, getRangeHit(npc, damage));
				break;
			case 3: // Ice arrows range
				damage = Utils.getRandom(250);
				npc.setNextAnimation(new Animation(13155));
				World.sendProjectile(npc, target, 369, 28, 16, 35, 35, 16, 0);
				delayHit(npc, 1, target, getRangeHit(npc, damage));
				break;
			case 4: // Orb crap
				break;
		}
		return defs.getAttackDelay();
	}

}
