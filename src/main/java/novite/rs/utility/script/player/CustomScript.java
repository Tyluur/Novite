package novite.rs.utility.script.player;

import java.io.File;
import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.game.player.Player;
import novite.rs.utility.Saving;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.script.GameScript;

/**
 * @author Tyluur<itstyluur@gmail.com>
 * @since Aug 22, 2014
 */
@SuppressWarnings("unused")
public class CustomScript extends GameScript {

	public static void main(String[] args) throws IOException {
		Cache.init();
		JsonHandler.initialize();
		for (File acc : getAccounts()) {
			try {
				if (acc.getName().equalsIgnoreCase(ACCOUNT_NAME)) {
					Player player = (Player) Saving.loadSerializedFile(acc);
					if (player != null) {
						player.getFacade().setTotalPointsPurchased(6000);
						player.getFacade().setGoldPoints(0);
						savePlayer(player, acc);
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void addNGPoints(Player player, int amount) {
		player.getFacade().setNoviteGamePoints(player.getFacade().getNoviteGamePoints() + amount);
	}
	
	private static final String ACCOUNT_NAME = "war.p";

}
