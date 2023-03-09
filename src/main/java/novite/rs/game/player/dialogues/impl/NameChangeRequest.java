package novite.rs.game.player.dialogues.impl;

import novite.rs.api.input.StringInputAction;
import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 6, 2014
 */
public class NameChangeRequest extends Dialogue {

	@Override
	public void start() {
		sendDialogue("You have access to one name change with this token.", "Are you sure you want to use it now?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
			case -1:
				sendOptionsDialogue("Select an Option", "Yes, change display name now.", "No.");
				stage = 0;
				break;
			case 0:
				if (option == FIRST) {
					player.getPackets().sendInputNameScript("What do you want your display name to be?", new StringInputAction() {

						@Override
						public void handle(String input) {
							if (player.getInventory().contains(10942)) {
								player.setDisplayName(input);
								player.getAppearence().generateAppearenceData();
								player.getInventory().deleteItem(10942, 1);
							}
						}
					});
				}
				end();
				break;
		}
	}

	@Override
	public void finish() {

	}

}
