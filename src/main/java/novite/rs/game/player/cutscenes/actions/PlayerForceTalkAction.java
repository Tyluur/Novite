package novite.rs.game.player.cutscenes.actions;

import novite.rs.game.ForceTalk;
import novite.rs.game.player.Player;

public class PlayerForceTalkAction extends CutsceneAction {

	private String text;

	public PlayerForceTalkAction(String text, int actionDelay) {
		super(-1, actionDelay);
		this.text = text;
	}

	@Override
	public void process(Player player, Object[] cache) {
		player.setNextForceTalk(new ForceTalk(text));
	}

}
