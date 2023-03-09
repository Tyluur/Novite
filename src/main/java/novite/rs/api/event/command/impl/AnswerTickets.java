package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.TicketSystem;
import novite.rs.utility.game.Rights;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 20, 2014
 */
public class AnswerTickets extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.PLAYER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "ticket" };
	}

	@Override
	public void execute(Player player) {
		if (player.isSupporter() || player.getRights() > 0) {
			TicketSystem.answerTicket(player);
		}
	}

}
