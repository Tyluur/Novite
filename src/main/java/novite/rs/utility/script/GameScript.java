package novite.rs.utility.script;

import java.io.File;
import java.io.IOException;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.utility.Saving;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since 2012-12-30
 */
public abstract class GameScript {

	public static File[] getAccounts() {
		File dir = new File(Saving.PATH);
		return dir.listFiles();
	}

	public static Player getPlayer(File file) throws ClassNotFoundException, IOException {
		return (Player) Saving.loadSerializedFile(file);
	}

	public static void savePlayer(Player player, File account) {
		try {
			System.out.println("Completed " + account.getAbsolutePath());
			Saving.storeSerializableClass(player, account);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Item[] getBankItems(Player player) {
		return player.getBank().generateContainer();
	}

}
