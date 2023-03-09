package novite.rs.game.npc.others;

import java.util.concurrent.TimeUnit;

import novite.rs.engine.CoresManager;
import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

public class Bork extends NPC {

	/**
	 *
	 */
	private static final long serialVersionUID = 347462259943586343L;
	public static long deadTime;

	public Bork(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(0);
		setForceAgressive(true);
	}

	@Override
	public void sendDeath(Entity source) {
		deadTime = Utils.currentTimeMillis() + (1000 * 60 * 60);
		resetWalkSteps();
		for (Entity e : getPossibleTargets()) {
			if (e instanceof Player) {
				final Player player = (Player) e;
				player.getInterfaceManager().sendInterface(693);
				player.getDialogueManager().startDialogue("DagonHai", 7137, player, 1);
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						player.stopAll();
					}
				}, 8);
			}
		}
		getCombat().removeTarget();
		setNextAnimation(new Animation(getCombatDefinitions().getDeathEmote()));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				drop();
				reset();
				setLocation(getRespawnTile());
				finish();
				if (!isSpawned()) {
					setRespawnTask();
				}
				stop();
			}

		}, 4);
	}

	@Override
	public void setRespawnTask() {
		if (!hasFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawn();
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Error e) {
					e.printStackTrace();
				}
			}
		}, 1, TimeUnit.HOURS);
	}

	public static String convertToTime() {
		String time = "You have to wait " + (getTime() == 0 ? "few more seconds" : getTime() + " mins") + " to kill bork again.";
		return time;
	}

	public static int getTime() {
		return (int) (deadTime - Utils.currentTimeMillis() / 60000);
	}

	public static boolean atBork(WorldTile tile) {
		if ((tile.getX() >= 3083 && tile.getX() <= 3120) && (tile.getY() >= 5522 && tile.getY() <= 5550)) {
			return true;
		}
		return false;
	}
}
