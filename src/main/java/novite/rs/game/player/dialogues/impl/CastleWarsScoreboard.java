package novite.rs.game.player.dialogues.impl;

import novite.rs.game.minigames.CastleWars;
import novite.rs.game.player.dialogues.Dialogue;

public class CastleWarsScoreboard extends Dialogue {

	@Override
	public void start() {
		CastleWars.viewScoreBoard(player);

	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();

	}

	@Override
	public void finish() {

	}

}
