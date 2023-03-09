package novite.rs.game.npc.godwars.zaros;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.player.controlers.impl.GodWars;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;

public class ZarosMinion extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = 5974794206587714046L;

	public ZarosMinion(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					if (source instanceof Player) {
						Player player = (Player) source;
						Controller controler = player.getControllerManager().getController();
						if (controler != null && controler instanceof GodWars) {
							GodWars godControler = (GodWars) controler;
							godControler.incrementKillCount(4);
						}
					}
					drop();
					reset();
					setLocation(getRespawnTile());
					finish();
					if (!isSpawned()) {
						setRespawnTask();
					}
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
}