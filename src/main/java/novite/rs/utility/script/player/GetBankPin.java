package novite.rs.utility.script.player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import novite.rs.cache.Cache;
import novite.rs.game.player.Player;
import novite.rs.game.player.quests.QuestManager;
import novite.rs.utility.Config;
import novite.rs.utility.Saving;
import novite.rs.utility.TextIO;
import novite.rs.utility.script.GameScript;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Aug 9, 2014
 */
public class GetBankPin extends GameScript {

	public static void main(String... args) {
		try {
			Cache.init();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Config.get().load();
		QuestManager.load();
		System.out.println("Enter account name:");
		String name = TextIO.getlnString() + ".p";
		for (File acc : getAccounts()) {
			try {
				Player player = (Player) Saving.loadSerializedFile(acc);
				if (acc.getName().equalsIgnoreCase(name)) {
					if (player != null) {
						if (player.getBank().getPin() != null && player.getBank().getPin().hasPin()) {
							System.out.println(Arrays.toString(player.getBank().getPin().getCurrentPin()));
						}
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
