/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package novite.rs.game.npc.jad;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import novite.rs.engine.CoresManager;
import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.Magic;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;

/**
 *
 * @author Owner
 */
public class TzTokJad extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = 5908420182962388837L;

	public TzTokJad(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playerIndexes != null) {
				for (int npcIndex : playerIndexes) {
					Player player = World.getPlayers().get(npcIndex);
					if (player == null || player.isDead() || player.hasFinished() || !player.isRunning() || !player.withinDistance(this, 64) || ((!isAtMultiArea() || !player.isAtMultiArea()) && player.getAttackedBy() != this && player.getAttackedByDelay() > System.currentTimeMillis()) || !clipedProjectile(player, false)) {
						continue;
					}
					possibleTarget.add(player);
				}
			}
		}
		return possibleTarget;
	}

	/*
	 * gotta override else setRespawnTask override doesnt work
	 */
	@Override
	public void sendDeath(Entity source) {
		Player killer = (Player) source;
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		killer.getInventory().addItem(6570, 1);
		Magic.sendNormalTeleportSpell(killer, 0, 0, new WorldTile(2438, 5173, 0));
		WorldTasksManager.schedule(new WorldTask() {

			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					drop();
					reset();
					setLocation(getRespawnTile());
					finish();
					setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
	public void setRespawnTask() {
		if (!hasFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
		final NPC npc = this;
		CoresManager.slowExecutor.schedule(new Runnable() {

			@Override
			public void run() {
				setFinished(false);
				World.addNPC(npc);
				npc.setLastRegionId(0);
				World.updateEntityRegion(npc);
				loadMapRegions();
				checkMultiArea();
			}
		}, getCombatDefinitions().getRespawnDelay() * 1200, TimeUnit.MILLISECONDS);
	}
}
