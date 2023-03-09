package novite.rs.api.event.command.impl;

import java.util.concurrent.TimeUnit;

import novite.rs.api.database.mysql.impl.DatabaseQueries;
import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 7, 2014
 */
public class CheckPayment extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.PLAYER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "claim" };
	}

	@Override
	public void execute(Player player) {
		Long lastTime = (Long) player.getTemporaryAttributtes().get("last_payment_verif");
		if (lastTime == null || (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastTime) > 10)) {
			DatabaseQueries.claimGoldPoints(player);
			player.getTemporaryAttributtes().put("last_payment_verif", System.currentTimeMillis());
		} else {
			player.sendMessage("You can only use this command once every 10 seconds...");
		}
	}

}
