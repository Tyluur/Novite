package novite.rs.game.player.dialogues.impl;

import java.util.concurrent.TimeUnit;

import novite.rs.api.input.StringInputAction;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 26, 2014
 */
public class Security_Guard extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Hello, how may I be of service to you?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Select an Option", (player.getFacade().isExpLocked() ? "Unlock" : "Lock") + " experience.", "Cancel");
			stage = 0;
			break;
		case 0:
			switch (option) {
			case FIRST:
				player.getFacade().setExpLocked(!player.getFacade().isExpLocked());
				sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Your experience is now " + (player.getFacade().isExpLocked() ? "" : "un") + "locked.");
				stage = -2;
				break;
			case SECOND:
				end();
				break;
			}
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("unused")
	private void changeName() {
		if (player.getFacade().getLastDisplayNameChange() == -1 || TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - player.getFacade().getLastDisplayNameChange()) >= 7) {
			player.getPackets().sendInputNameScript("Enter Name:", new StringInputAction() {

				@Override
				public void handle(String input) {
					player.setDisplayName(input);
					player.getFacade().setLastDisplayNameChange(System.currentTimeMillis());
					player.getAppearence().generateAppearenceData();
					end();
				}
			});
		} else {
			long days = player.getFacade().getLastDisplayNameChange() + TimeUnit.DAYS.toMillis(7);
			long hours = TimeUnit.MILLISECONDS.toHours(days - System.currentTimeMillis());
			String timeInfo = "";
			if (hours > 1)
				timeInfo = "Hours Left: " + hours;
			else {
				long minutes = TimeUnit.MILLISECONDS.toMinutes(days - System.currentTimeMillis());
				timeInfo = "Minutes Left: " + minutes;
			}
			sendDialogue("You cannot change your display name yet!", timeInfo);
			stage = -2;
		}
	}

	@Override
	public void finish() {
	}

}
