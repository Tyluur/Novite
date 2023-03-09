package novite.rs.utility.script.player;

import java.io.File;
import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.game.player.Player;
import novite.rs.utility.Config;
import novite.rs.utility.Saving;
import novite.rs.utility.Utils.CombatRates;
import novite.rs.utility.script.GameScript;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 30, 2014
 */
public class PlayerRatesChanger extends GameScript {

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
					player.getFacade().setModifiers(CombatRates.ELITE);
					savePlayer(player, acc);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static final String name = "feztastic.p";
}
