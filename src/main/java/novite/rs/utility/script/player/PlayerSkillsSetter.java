package novite.rs.utility.script.player;

import java.io.File;
import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.utility.Config;
import novite.rs.utility.Saving;
import novite.rs.utility.script.GameScript;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 30, 2014
 */
public class PlayerSkillsSetter extends GameScript {


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
					set(player, Skills.SUMMONING, 85);
					savePlayer(player, acc);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void set(Player player, int skill, int lvl) {
		player.getSkills().setStat(skill, lvl);
	}
	
	private static final String name = "endlesskillz.p";
}
