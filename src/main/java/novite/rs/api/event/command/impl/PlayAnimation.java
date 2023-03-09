package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.Animation;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 24, 2014
 */
public class PlayAnimation extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.ADMINISTRATOR;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "anim", "emote" };
	}

	@Override
	public void execute(Player player) {
		player.setNextAnimation(new Animation(Integer.parseInt(cmd[1])));
	}

}
