package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.utility.Utils;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 29, 2014
 */
public class MeLDScript extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 4510 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		int random = Utils.random(4);
		int damage = Utils.random(150, 275);
		switch (random) {
		case 1:
		case 2:
			npc.setNextAnimation(MAGIC_ANIMATION);
			target.setNextGraphics(MAGIC_GFX);
			delayHit(npc, 1, target, getMagicHit(npc, damage));
			break;
		default:
			npc.setNextAnimation(MELEE_ANIMATION);
			delayHit(npc, 0, target, getMeleeHit(npc, damage));
			break;
		}
		return 3;
	}
	
	private static final Animation MELEE_ANIMATION = new Animation(1884);
	private static final Animation MAGIC_ANIMATION = new Animation(811);
	private static final Graphics MAGIC_GFX = new Graphics(76);

}
