package novite.rs.utility.script.player;

import java.io.File;
import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.game.player.Player;
import novite.rs.utility.Saving;
import novite.rs.utility.script.GameScript;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 10, 2014
 */
public class ExperienceRateSelection extends GameScript {

	public static void main(String[] args) throws IOException {
		Cache.init();
		for (File acc : getAccounts()) {
			try {
				Player player = (Player) Saving.loadSerializedFile(acc);
				if (player != null) {
					player.setUsername(acc.getName());
					player.getFacade().setModifier(new double[3]);
				}
				savePlayer(player, acc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
