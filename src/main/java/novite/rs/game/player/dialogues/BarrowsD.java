package novite.rs.game.player.dialogues;

import novite.rs.game.WorldTile;

public class BarrowsD extends Dialogue {

	@Override
	public void start() {
		sendDialogue("You've found a hidden tunnel, do you want to enter?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(DEFAULT_OPTIONS_TI, "Yes, I'm fearless.", "No way, that looks scary!");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				player.setNextWorldTile(new WorldTile(3534, 9677, 0));
				player.getVarsManager().sendVar(1270, 0);
			}
			end();
		}
	}

	@Override
	public void finish() {

	}

}
