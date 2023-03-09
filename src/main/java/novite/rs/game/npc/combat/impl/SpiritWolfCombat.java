package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.npc.familiar.Familiar;
import novite.rs.game.player.Player;

public class SpiritWolfCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6829, 6828 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		if (usingSpecial) {// priority over regular attack
			familiar.submitSpecial(familiar.getOwner());
			npc.setNextAnimation(new Animation(8293));
			npc.setNextGraphics(new Graphics(1334));
			World.sendProjectile(npc, target, 1333, 34, 16, 30, 35, 16, 0);
			if (target instanceof NPC) {
				if (target.getSize() < 2) {
					target.setAttackedByDelay(3000);// three seconds
				} else {
					familiar.getOwner().getPackets().sendGameMessage("Your familiar cannot scare that monster.");
				}
			} else if (target instanceof Player) {
				familiar.getOwner().getPackets().sendGameMessage("Your familiar cannot scare a player.");
			} else if (target instanceof Familiar) {
				familiar.getOwner().getPackets().sendGameMessage("Your familiar cannot scare other familiars.");
			}
		} else {
			npc.setNextAnimation(new Animation(6829));
			delayHit(npc, 1, target, getMagicHit(npc, getRandomMaxHit(npc, 40, NPCCombatDefinitions.MAGE, target)));
		}
		return defs.getAttackDelay();
	}

}
