package novite.rs.game.player.dialogues.impl;

import novite.rs.game.player.content.scrolls.ClueScroll;
import novite.rs.game.player.content.scrolls.ScrollType;
import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 22, 2014
 */
public class ScrollRemoval extends Dialogue {

	ClueScroll scroll;
	ScrollType type;

	@Override
	public void start() {
		scroll = (ClueScroll) parameters[0];
		type = (ScrollType) parameters[1];
		sendDialogue("You can delete this scroll permanently if you wish.", "Do you want to remove this scroll from your account?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Delete this " + type.name().toLowerCase() + " scroll?", "Yes", "No");
			stage = 0;
			break;
		case 0:
			switch (option) {
			case FIRST:
				if (player.getClueScrollManager().deleteScroll(type)) {
					player.closeInterfaces();
				} else {
					sendDialogue("Error deleting scroll, make sure you have one!");
					stage = -2;
				}
				break;
			case SECOND:
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {
	}

}
