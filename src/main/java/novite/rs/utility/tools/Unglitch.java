package novite.rs.utility.tools;

import java.io.File;

import novite.rs.game.player.Player;
import novite.rs.utility.Saving;

public class Unglitch {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		File acc = new File("cjay0091.p");
		Player player = (Player) Saving.loadSerializedFile(acc);
		player.setDisableEquip(false);
		Saving.storeSerializableClass(player, acc);
	}

}
