package novite.rs.game.player.dialogues.impl;

import novite.rs.api.input.StringInputAction;
import novite.rs.game.player.clans.ClansManager;
import novite.rs.game.player.dialogues.Dialogue;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Dec 12, 2013
 */
public class ClanCreateDialogue extends Dialogue {

	@Override
	public void start() {
		sendDialogue("You must be a member of a clan in order to join their channel.", "Would you like to create a clan?");
	}

	@Override
	public void run(int interfaceId, int option) {
		if (stage == -1) {
			player.getPackets().sendInputNameScript("Enter the clan name you'd like to have.", new StringInputAction() {

				@Override
				public void handle(String input) {
					ClansManager.createClan(player, input);
				}
			});
			end();
		}
	}

	@Override
	public void finish() {

	}

}
