package novite.rs.game.player.cutscenes.actions;

import novite.rs.game.Graphics;
import novite.rs.game.player.Player;

public class PlayerGraphicAction extends CutsceneAction {

	private Graphics gfx;

	public PlayerGraphicAction(Graphics gfx, int actionDelay) {
		super(-1, actionDelay);
		this.gfx = gfx;
	}

	@Override
	public void process(Player player, Object[] cache) {
		player.setNextGraphics(gfx);
	}

}
