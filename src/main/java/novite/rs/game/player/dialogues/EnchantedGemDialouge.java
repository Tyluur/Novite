package novite.rs.game.player.dialogues;

import novite.rs.cache.loaders.NPCDefinitions;

public class EnchantedGemDialouge extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select an Option", "How many monsters do I have left?", "What combat level is my task?");
	}

	@Override
	public void run(int interfaceId, int option) {
		if (player.getSlayerTask() == null) {
			sendDialogue("You do not have a task!", "Talk to Lapalok for one.");
			return;
		}
		switch (getStage()) {
		case -1:
			switch (option) {
			case FIRST:
				sendDialogue("You have to kill " + player.getSlayerTask().getAmount() + " more " + player.getSlayerTask().getName() + ".");
				setStage(-2);
				break;
			case SECOND:
				sendDialogue("Your task's combat level is " + NPCDefinitions.getNPCDefinitions(player.getSlayerTask().getName()).combatLevel + ".");
				setStage(-2);
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {

	}

}