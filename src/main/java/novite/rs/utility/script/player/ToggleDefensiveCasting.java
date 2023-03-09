package novite.rs.utility.script.player;

import java.io.File;
import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.game.player.Player;
import novite.rs.utility.Config;
import novite.rs.utility.Saving;
import novite.rs.utility.script.GameScript;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Aug 7, 2014
 */
public class ToggleDefensiveCasting extends GameScript {

	public static void main(String... args) {
		try {
			Cache.init();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String name = "hometown_p_k.p";
		Config.get().load();
		for (File acc : getAccounts()) {
			try {
				if (acc.getName().equalsIgnoreCase(name)) {
					Player player = (Player) Saving.loadSerializedFile(acc);
					System.out.println("Set to 0 from " + player.getFacade().getNoviteGamePoints());
					player.getFacade().setNoviteGamePoints(0);
					savePlayer(player, acc);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}