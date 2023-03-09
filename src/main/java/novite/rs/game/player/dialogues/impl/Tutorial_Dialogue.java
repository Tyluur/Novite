package novite.rs.game.player.dialogues.impl;

import novite.rs.Constants;
import novite.rs.game.WorldTile;
import novite.rs.game.player.controlers.impl.StartTutorial;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 22, 2014
 */
public class Tutorial_Dialogue extends Dialogue {

	@Override
	public void start() {
		stage = (Integer) parameters[0];
		send(stage++);
	}

	/**
	 * @param currentStage
	 */
	private void send(int currentStage) {
		if (player.getControllerManager().getController() instanceof StartTutorial) {
			StartTutorial tut = (StartTutorial) player.getControllerManager().getController();
			tut.setStage(currentStage);
		}
		final int viewInterface = 155;
		switch (currentStage) {
		case 0:
			sendNPCDialogueNoContinue(npcId, ChatAnimation.NORMAL, 5, "Hello adventurer " + player.getDisplayName() + ". Welcome to " + Constants.SERVER_NAME + ".", "It's my job to inform you about us in less than 30 seconds.", "So here we go...");
			player.setNextWorldTile(Constants.HOME_TILE);
			player.getInterfaceManager().sendInterface(viewInterface);
			break;
		case 1:
			sendNPCDialogueNoContinue(npcId, ChatAnimation.NORMAL, 5, "This is the Spirit Tree everyone uses to teleport", "around the game world. Interact with the tree and", "select a teleport location and you'll see yourself there.");
			player.setNextWorldTile(new WorldTile(2603, 3096, 0));
			break;
		case 2:
			sendNPCDialogueNoContinue(npcId, ChatAnimation.NORMAL, 5, "Here we have the \"Event Portal\", with which will teleport", "you into event locations, such as questing places", "and minigame locations.");
			player.setNextWorldTile(new WorldTile(2607, 3097, 0));
			break;
		case 3:
			sendNPCDialogueNoContinue(npcId, ChatAnimation.NORMAL, 5, "Infront of you is the Exchange Clerk. This", "is where you can buy and sell all items to " + Constants.SERVER_NAME + " players.", "Try buying supplies after the tutorial.");
			player.setNextWorldTile(new WorldTile(2611, 3095, 0));
			break;
		case 4:
			sendNPCDialogueNoContinue(npcId, ChatAnimation.NORMAL, 5, "We're at the book lectern. Here is where you can wish", "for a change in your prayer or magic book. Simply", "interact with the lectern to have it changed.");
			player.setNextWorldTile(new WorldTile(2606, 3088, 0));
			break;
		case 5:
			sendNPCDialogueNoContinue(npcId, ChatAnimation.NORMAL, 5, "Lastly is the Slayer Master, Lapalok.", "If you wish to start a slayer task", "it is best you talk to him first.");
			player.setNextWorldTile(new WorldTile(2606, 3103, 0));
			break;
		case 6:
			sendNPCDialogueNoContinue(npcId, ChatAnimation.NORMAL, -1, "Please personalize your character's looks!", "Modify your appearance with the \"?\" symbol at the top right");
			break;
		default:
			sendDialogue("stage:" + currentStage);
			break;
		}
	}

	@Override
	public void run(int interfaceId, int option) {
		send(stage++);
	}

	@Override
	public void finish() {

	}

	int stage;
	private final int npcId = 2244;

}
