package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ShopsLoader;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 21, 2014
 */
public class ReloadShops extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "rlsh" };
	}

	@Override
	public void execute(Player player) {
		((ShopsLoader) JsonHandler.getJsonLoader(ShopsLoader.class)).initialize();
	}

}
