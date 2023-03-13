package novite.rs.utility.script.player;

import java.io.File;
import java.io.IOException;

import novite.rs.Constants;
import novite.rs.cache.Cache;
import novite.rs.game.player.Player;
import novite.rs.utility.Config;
import novite.rs.utility.Saving;
import novite.rs.utility.script.GameScript;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Aug 7, 2014
 */
public class UnNull extends GameScript {

	public static void main(String... args) {
		try {
			Cache.init();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Config.get().load();
		for (File acc : getAccounts()) {
			try {
				Player player = (Player) Saving.loadSerializedFile(acc);
				if (acc.getName().equalsIgnoreCase(name)) {
					player.getControllerManager().removeControlerWithoutCheck();
					player.setLocation(Constants.HOME_TILE);
					savePlayer(player, acc);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static final String name = "sage.p";
}
