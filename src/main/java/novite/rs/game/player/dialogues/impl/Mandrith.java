package novite.rs.game.player.dialogues.impl;

import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ShopsLoader;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 6, 2014
 */
public class Mandrith extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select an Option", "Pk-Point Exchange", "Receive a Skull");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (option) {
		case FIRST:
			JsonHandler.<ShopsLoader>getJsonLoader(ShopsLoader.class).openShop(player, "Pk Point Exchange");
			break;
		case SECOND:
			player.setWildernessSkull();
			break;
		}
		end();
	}

	@Override
	public void finish() {
	}

}
