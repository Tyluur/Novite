package novite.rs.engine.process.impl;

import java.util.concurrent.TimeUnit;

import novite.rs.engine.process.TimedProcess;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.utility.Saving;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 3, 2014
 */
public class SavingFileProcess implements TimedProcess {

	@Override
	public Timer getTimer() {
		return new Timer(15, TimeUnit.MINUTES);
	}

	@Override
	public void execute() {
		try {
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted() || player.hasFinished() || !player.controlerAvailable()) {
					continue;
				}
				Saving.savePlayer(player);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
