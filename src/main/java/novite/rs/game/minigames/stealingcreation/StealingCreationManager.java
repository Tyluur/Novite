package novite.rs.game.minigames.stealingcreation;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import novite.rs.engine.CoresManager;
import novite.rs.game.player.Player;

public class StealingCreationManager {

	private static final List<StealingCreationGame> running = new CopyOnWriteArrayList<StealingCreationGame>();
	private static TimerTask watcher;

	public synchronized static void createGame(int size, List<Player> blueTeam, List<Player> redTeam) {
		running.add(new StealingCreationGame(size, blueTeam, redTeam));
		if (watcher == null) {
			CoresManager.fastExecutor.schedule(watcher = new TimerTask() {
				@Override
				public void run() {
					try {
						processWatcher();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}, 0L, 1000);
		}
	}

	/**
	 * Processes watcher thread.
	 */
	private static void processWatcher() {
		for (StealingCreationGame game : running) {
			game.run();
		}
	}

	public static void removeGame(StealingCreationGame game) {
		running.remove(game);
		if (running.size() == 0) {
			watcher.cancel();
			watcher = null;
		}
	}
}
