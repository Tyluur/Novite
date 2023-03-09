package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 29, 2014
 */
public class ResetQuests extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "resetq" };
	}

	@Override
	public void execute(Player player) {
		player.getQuestManager().getProgressed().clear();
		player.getQuestManager().getStages().clear();
		player.getFacade().setLastRFDWave(0);
	}

}
