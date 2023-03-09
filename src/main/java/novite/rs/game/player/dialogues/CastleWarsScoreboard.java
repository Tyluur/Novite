package novite.rs.game.player.dialogues;

import novite.rs.game.minigames.CastleWars;

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
