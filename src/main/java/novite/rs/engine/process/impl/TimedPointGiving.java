package novite.rs.engine.process.impl;

import java.util.concurrent.TimeUnit;

import novite.rs.engine.process.TimedProcess;
import novite.rs.game.World;
import novite.rs.game.player.Player;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 31, 2014
 */
public class TimedPointGiving implements TimedProcess {

	@Override
	public Timer getTimer() {
		return new Timer(1, TimeUnit.SECONDS);
	}

	@Override
	public void execute() {
		try {
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted() || player.isDead() || player.hasFinished() || !player.getInterfaceManager().isClientActive()) {
					continue;
				}
				player.setSecondsPlayed(player.getSecondsPlayed() + 1);
				player.getLoyaltyManager().addPoints(player.isDonator() ? 2 : 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
