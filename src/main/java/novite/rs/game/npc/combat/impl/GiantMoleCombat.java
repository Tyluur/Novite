package novite.rs.game.npc.combat.impl;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.Graphics;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.CombatScript;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class GiantMoleCombat extends CombatScript {

	private static final WorldTile[] COORDS = { new WorldTile(1737, 5228, 0), new WorldTile(1751, 5233, 0), new WorldTile(1778, 5237, 0), new WorldTile(1736, 5227, 0), new WorldTile(1780, 5152, 0), new WorldTile(1758, 5162, 0), new WorldTile(1745, 5169, 0), new WorldTile(1760, 5183, 0) };

	@Override
	public Object[] getKeys() {
		return new Object[] { 3340 };
	}

	@Override
	public int attack(final NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.random(5) == 0) { // bury
			npc.setNextAnimation(new Animation(3314));
			npc.setCantInteract(true);
			npc.getCombat().removeTarget();
			final Player player = (Player) (target instanceof Player ? target : null);
			if (player != null) {
				player.getInterfaceManager().sendTab(player.getInterfaceManager().hasResizableScreen() ? 1 : 11, 226);
			}
			final WorldTile middle = npc.getMiddleWorldTile();
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (player != null) {
						player.getInterfaceManager().removeTab(player.getInterfaceManager().hasResizableScreen() ? 1 : 11);
					}
					npc.setCantInteract(false);
					if (npc.isDead()) {
						return;
					}
					World.sendGraphics(npc, new Graphics(572), middle);
					World.sendGraphics(npc, new Graphics(571), new WorldTile(middle.getX(), middle.getY() - 1, middle.getPlane()));
					World.sendGraphics(npc, new Graphics(571), new WorldTile(middle.getX(), middle.getY() + 1, middle.getPlane()));
					World.sendGraphics(npc, new Graphics(571), new WorldTile(middle.getX() - 1, middle.getY() - 1, middle.getPlane()));
					World.sendGraphics(npc, new Graphics(571), new WorldTile(middle.getX() - 1, middle.getY() + 1, middle.getPlane()));
					World.sendGraphics(npc, new Graphics(571), new WorldTile(middle.getX() + 1, middle.getY() - 1, middle.getPlane()));
					World.sendGraphics(npc, new Graphics(571), new WorldTile(middle.getX() + 1, middle.getY() + 1, middle.getPlane()));
					World.sendGraphics(npc, new Graphics(571), new WorldTile(middle.getX() - 1, middle.getY(), middle.getPlane()));
					World.sendGraphics(npc, new Graphics(571), new WorldTile(middle.getX() + 1, middle.getY(), middle.getPlane()));
					npc.setNextWorldTile(new WorldTile(COORDS[Utils.random(COORDS.length)]));
					npc.setNextAnimation(new Animation(3315));

				}

			}, 2);

		} else {
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
		}
		return defs.getAttackDelay();
	}

}
