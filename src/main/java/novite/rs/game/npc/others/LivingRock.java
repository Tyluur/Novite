package novite.rs.game.npc.others;

import java.util.concurrent.TimeUnit;

import novite.rs.engine.CoresManager;
import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.npc.combat.NPCCombatDefinitions;
import novite.rs.game.player.Player;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class LivingRock extends NPC {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7911686054785344067L;
	private Entity source;
	private long deathTime;

	public LivingRock(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setForceTargetDistance(4);
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
					drop();
					reset();
					transformIntoRemains(source);
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public void transformIntoRemains(Entity source) {
		this.source = source;
		deathTime = Utils.currentTimeMillis();
		final int remainsId = getId() + 5;
		transformInto(remainsId);
		setWalkType(NO_WALK);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (remainsId == getId()) {
						takeRemains();
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, 3, TimeUnit.MINUTES);

	}

	public boolean canMine(Player player) {
		return Utils.currentTimeMillis() - deathTime > 60000 || player == source;
	}

	public void takeRemains() {
		setNPC(getId() - 5);
		setLocation(getRespawnTile());
		setWalkType(NORMAL_WALK);
		finish();
		if (!isSpawned()) {
			setRespawnTask();
		}
	}

}
