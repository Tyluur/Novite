package novite.rs.game.player.cutscenes.actions;

import novite.rs.game.Animation;
import novite.rs.game.player.Player;

public class PlayerAnimationAction extends CutsceneAction {

	private Animation anim;

	public PlayerAnimationAction(Animation anim, int actionDelay) {
		super(-1, actionDelay);
		this.anim = anim;
	}

	@Override
	public void process(Player player, Object[] cache) {
		player.setNextAnimation(anim);
	}

}
