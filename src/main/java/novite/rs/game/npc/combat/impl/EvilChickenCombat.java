package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.ForceTalk;
import novite.rs.game.Graphics;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;

public class EvilChickenCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Evil Chicken" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		switch (Utils.getRandom(5)) {
			case 0:
				npc.setNextForceTalk(new ForceTalk("Bwuk"));
				break;
			case 1:
				npc.setNextForceTalk(new ForceTalk("Bwuk bwuk bwuk"));
				break;
			case 2:
				String name = "";
				if (target instanceof Player) {
					name = ((Player) target).getDisplayName();
				}
				npc.setNextForceTalk(new ForceTalk("Flee from me, " + name));
				break;
			case 3:
				name = "";
				if (target instanceof Player) {
					name = ((Player) target).getDisplayName();
				}
				npc.setNextForceTalk(new ForceTalk("Begone, " + name));
				break;
			case 4:
				npc.setNextForceTalk(new ForceTalk("Bwaaaauuuuk bwuk bwuk"));
				break;
			case 5:
				npc.setNextForceTalk(new ForceTalk("MUAHAHAHAHAAA!"));
				break;
		}
		target.setNextGraphics(new Graphics(337));
		delayHit(npc, 0, target, getMagicHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target)));
		return defs.getAttackDelay();
	}
}
