package novite.rs.utility.script.player;

import java.io.File;
import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.game.player.Player;
import novite.rs.game.player.quests.QuestManager;
import novite.rs.game.player.quests.impl.Desert_Treasure;
import novite.rs.utility.Config;
import novite.rs.utility.Saving;
import novite.rs.utility.script.GameScript;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 15, 2014
 */
public class PlayerQuestRestarter extends GameScript {

	public static void main(String... args) {
		try {
			Cache.init();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Config.get().load();
		QuestManager.load();
		for (File acc : getAccounts()) {
			try {
				Player player = (Player) Saving.loadSerializedFile(acc);
				if (acc.getName().equalsIgnoreCase(name)) {
					if (player != null) {
						Desert_Treasure dt = (Desert_Treasure) player.getQuestManager().getProgressedQuest(QuestManager.getQuest(Desert_Treasure.class).getName());
						if (dt != null) {
							player.getFacade().getDesertTreasureKills().clear();
						}
					}
					savePlayer(player, acc);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static final String name = "sorryboutcha.p";

}
