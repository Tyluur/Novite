package novite.rs.game.player.dialogues;

import novite.rs.game.WorldTile;
import novite.rs.game.minigames.ZarosGodwars;

public final class NexEntrance extends Dialogue {

	@Override
	public void start() {
		sendDialogue(SEND_3_TEXT_INFO, "The room beyond this point is a prison!", "There is no way out other than death or teleport.", "Only those who endure dangerous encounters should proceed.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (getStage() == -1) {
			setStage(0);
			sendOptionsDialogue("Join " + ZarosGodwars.getPlayers().size() + " people fighting?", "Climb down.", "Stay here");
		} else if (getStage() == 0) {
			if (componentId == 1) {
				player.setNextWorldTile(new WorldTile(2911, 5204, 0));
				player.getControllerManager().startController("ZGDControler");
			}
			end();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
