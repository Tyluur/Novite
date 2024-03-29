package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class KetZekCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Ket-Zek", 15207 };
	}// anims: DeathEmote: 9257 DefEmote: 9253 AttackAnim: 9252 gfxs: healing:
		// 444 - healer

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		int hit = 0;
		if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1) {
			commenceMagicAttack(npc, target, hit);
			return defs.getAttackDelay();
		}
		int attackStyle = Utils.getRandom(1);
		switch (attackStyle) {
		case 0:
			hit = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, hit));
			break;
		case 1:
			commenceMagicAttack(npc, target, hit);
			break;
		}
		return defs.getAttackDelay();
	}

	private void commenceMagicAttack(final NPC npc, final Entity target, int hit) {
		hit = getRandomMaxHit(npc, npc.getCombatDefinitions().getMaxHit() - 50, NPCCombatDefinitions.MAGE, target);
		npc.setNextAnimation(new Animation(9266));
		World.sendProjectile(npc, target, 2984, 34, 16, 30, 35, 16, 0);
		delayHit(npc, 2, target, getMagicHit(npc, hit));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				target.setNextGraphics(new Graphics(2983, 0, 96 << 16));
			}
		}, 2);
	}
}
