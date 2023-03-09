package novite.rs.engine.game;

import novite.rs.Constants;
import novite.rs.engine.CoresManager;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;

/**
 * @author Tyluur<itstyluur@gmail.com>
 */
public class GameWorker implements Runnable {

	public GameWorker() {
		Thread.currentThread().setName("Game Worker");
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	}

	@Override
	public final void run() {
		while (!CoresManager.shutdown) {
			long currentTime = Utils.currentTimeMillis();
			long start = System.currentTimeMillis();
			StringBuilder bldr = new StringBuilder();
			try {
				WorldTasksManager.processTasks();
				bldr.append("\nTook " + (System.currentTimeMillis() - start) + " ms to process tasks\n");
				start = System.currentTimeMillis();
				for (Player player : World.getPlayers()) {
					if (player == null || !player.hasStarted() || player.hasFinished()) {
						continue;
					}
					if (currentTime - player.getPacketsDecoderPing() > Constants.AFK_LOGOUT_DELAY && player.getSession().getChannel().isOpen()) {
						player.getSession().getChannel().close();
					}
					/*if (Constants.isVPS && currentTime - player.getPacketsDecoderPing() > (afking ? Constants.AFK_LOGOUT_DELAY * 3 : Constants.AFK_LOGOUT_DELAY) && player.getSession().getChannel().isOpen()) {
						System.out.println(player.getUsername() + " was inactive for " + Constants.AFK_LOGOUT_DELAY + " ms [" + TimeUnit.MILLISECONDS.toSeconds(Constants.AFK_LOGOUT_DELAY) + " sec], so they were booted!\n");
						player.getSession().getChannel().close();
					}*/
					player.processEntity();
				}
				bldr.append("Took " + (System.currentTimeMillis() - start) + " ms to process players\n");
				start = System.currentTimeMillis();
				for (NPC npc : World.getNPCs()) {
					long t = System.currentTimeMillis();
					if (npc == null || npc.hasFinished()) {
						continue;
					}
					npc.processEntity();
					long t1 = System.currentTimeMillis() - t;
					if (t1 > 100)
						System.out.println("Took " + (t1) + " ms to process " + npc);
				}
				bldr.append("Took " + (System.currentTimeMillis() - start) + " ms to process npcs\n");
				start = System.currentTimeMillis();
				for (Player player : World.getPlayers()) {
					if (player == null || !player.hasStarted() || player.hasFinished()) {
						continue;
					}
					player.getPackets().sendLocalPlayersUpdate();
					player.getPackets().sendLocalNPCsUpdate();
				}
				bldr.append("Took " + (System.currentTimeMillis() - start) + " ms to send local updating\n");
				start = System.currentTimeMillis();
				for (Player player : World.getPlayers()) {
					if (player == null || !player.hasStarted() || player.hasFinished()) {
						continue;
					}
					player.resetMasks();
				}
				for (NPC npc : World.getNPCs()) {
					if (npc == null || npc.hasFinished()) {
						continue;
					}
					npc.resetMasks();
				}
				setDelay(bldr, (int) (Utils.currentTimeMillis() - currentTime));
				long sleepTime = Constants.WORLD_CYCLE_TIME + currentTime - Utils.currentTimeMillis();
				if (sleepTime > 0) {
					Thread.sleep(sleepTime);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(StringBuilder bldr, int delay) {
		this.delay = delay;
		if (delay > 200) {
			System.err.println(bldr.toString());
			System.err.println("Extreme game cycle: " + delay);
		}
	}

	private int delay = 0;

}