package novite.rs.engine.game;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import novite.rs.engine.CoresManager;
import novite.rs.game.player.Player;
import novite.rs.networking.Session;
import novite.rs.utility.IsaacKeyPair;
import novite.rs.utility.game.GlobalPlayerInfo;

/**
 * @author Tyluur<itstyluur@gmail.com>
 * @since Aug 21, 2014
 */
public class LoginWorker implements Runnable {

	@Override
	public void run() {
		Thread.currentThread().setName("Login Worker");
		Object[] info = null;
		while (!CoresManager.shutdown) {
			try {
				while (!AWAITING_ADDING.isEmpty()) {
					info = AWAITING_ADDING.poll();
					Player player = (Player) info[0];
					if (player != null) {
						Session session = (Session) info[1];

						player.init(session, (String) info[2], (int) info[3], (int) info[4], (int) info[5], (IsaacKeyPair) info[6]);
						session.getLoginPackets().sendLoginDetails(player);
						session.setDecoder(3, player);
						session.setEncoder(2, player);
						player.start();
						if ((boolean) info[7]) {
							GlobalPlayerInfo.get().updateNewPlayers();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds this node to the awaiting adding queue.
	 * 
	 * @param player
	 *            The player to add.
	 * @return {@code True} if the queue didn't contain the player,
	 *         {@code False} if the queue already contained the player.
	 */
	public synchronized boolean offer(Player player, Object[] data) {
		Object[] info = new Object[data.length + 1];
		info[0] = player;
		for (int i = 0; i < info.length; i++) {
			if (i == 0)
				continue;
			info[i] = data[i - 1];
		}
		if (AWAITING_ADDING.contains(info)) {
			return false;
		}
		return AWAITING_ADDING.add(info);
	}

	/**
	 * A queue holding all the nodes that are awaiting to be added.
	 */
	private static final Queue<Object[]> AWAITING_ADDING = new LinkedBlockingQueue<Object[]>();

}
