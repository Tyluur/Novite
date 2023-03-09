package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ShopsLoader;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 2, 2014
 */
public class OpenShop extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "openshop" };
	}

	@Override
	public void execute(Player player) {
		((ShopsLoader) JsonHandler.getJsonLoader(ShopsLoader.class)).openShop(player, getCompleted(cmd, 1));
	}

}
