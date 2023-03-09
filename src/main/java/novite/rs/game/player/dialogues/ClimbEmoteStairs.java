package novite.rs.game.player.dialogues;

import novite.rs.game.WorldTile;

public class ClimbEmoteStairs extends Dialogue {

	private WorldTile upTile;
	private WorldTile downTile;
	private int emoteId;

	// uptile, downtile, climbup message, climbdown message, emoteid
	@Override
	public void start() {
		upTile = (WorldTile) parameters[0];
		downTile = (WorldTile) parameters[1];
		emoteId = (Integer) parameters[4];
		sendDialogue(SEND_3_LARGE_OPTIONS, "What would you like to do?", (String) parameters[2], (String) parameters[3], "Never mind.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (interfaceId == SEND_3_LARGE_OPTIONS && componentId == 2) {
			player.useStairs(emoteId, upTile, 2, 3);
		} else if (interfaceId == SEND_3_LARGE_OPTIONS && componentId == 3) {
			player.useStairs(emoteId, downTile, 2, 2);
		}
		end();
	}

	@Override
	public void finish() {

	}

}
