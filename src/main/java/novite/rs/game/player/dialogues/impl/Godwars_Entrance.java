package novite.rs.game.player.dialogues.impl;

import novite.rs.game.WorldTile;
import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 31, 2014
 */
public class Godwars_Entrance extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select an Option", "Bandos", "Armadyl", "Saradomin", "Zamorak");
	}

	@Override
	public void run(int interfaceId, int option) {
		WorldTile teleTile = null;
		switch (option) {
		case FIRST:
			teleTile = BANDOS;
			break;
		case SECOND:
			teleTile = ARMADYL;
			break;
		case THIRD:
			teleTile = SARADOMIN;
			break;
		case FOURTH:
			teleTile = ZAMORAK;
			break;
		}
		end();
		player.useStairs(827, teleTile, 2, 1, "You climb down the rope...");
		player.getControllerManager().startController("GodWars");
	}

	@Override
	public void finish() {

	}

	private static final WorldTile BANDOS = new WorldTile(2845, 5339, 2);
	private static final WorldTile ARMADYL = new WorldTile(2872, 5266, 2);
	private static final WorldTile SARADOMIN = new WorldTile(2915, 5300, 1);
	private static final WorldTile ZAMORAK = new WorldTile(2886, 5352, 2);

}
