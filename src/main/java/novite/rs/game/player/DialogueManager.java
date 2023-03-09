package novite.rs.game.player;

import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.game.player.dialogues.DialogueHandler;

public class DialogueManager {

	private Player player;
	private Dialogue lastDialogue;

	public DialogueManager(Player player) {
		this.player = player;
	}

	public void startDialogue(Class<?> clazz, Object... parameters) {
		startDialogue(clazz.getSimpleName(), parameters);
	}

	public void startDialogue(Object key, Object... parameters) {
		if (!player.getControllerManager().useDialogueScript(key)) {
			return;
		}
		if (lastDialogue != null) {
			lastDialogue.finish();
		}
		lastDialogue = DialogueHandler.getDialogue(key);
		if (lastDialogue == null) {
			return;
		}
		lastDialogue.parameters = parameters;
		lastDialogue.setPlayer(player);
		lastDialogue.start();
	}

	public void continueDialogue(int interfaceId, int componentId) {
		if (lastDialogue == null) {
			return;
		}
		if (lastDialogue != null && lastDialogue.getStage() == -2) {
			finishDialogue();
			return;
		}
		lastDialogue.run(interfaceId, componentId);
	}

	public void finishDialogue() {
		if (lastDialogue == null) {
			return;
		}
		lastDialogue.finish();
		lastDialogue = null;
		if (player.getInterfaceManager().containsChatBoxInter()) {
			player.getInterfaceManager().closeChatBoxInterface();
		}
	}

}
