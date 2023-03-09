package novite.rs.game.npc.godwars;

import java.util.ArrayList;
import java.util.List;

import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class GodWarMinion extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = 8508624240307372651L;

	public GodWarMinion(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
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
					if (player == null || player.isDead() || player.hasFinished() || !player.isRunning() || !player.withinDistance(this, 64) || ((!isAtMultiArea() || !player.isAtMultiArea()) && player.getAttackedBy() != this && player.getAttackedByDelay() > Utils.currentTimeMillis()) || !clipedProjectile(player, false)) {
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
	}

	public void respawn() {
		setFinished(false);
		World.addNPC(this);
		setLastRegionId(0);
		World.updateEntityRegion(this);
		loadMapRegions();
		checkMultiArea();
	}

}
