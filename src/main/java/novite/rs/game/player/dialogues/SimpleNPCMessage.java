package novite.rs.game.player.dialogues;

import java.util.ArrayList;
import java.util.List;

public class SimpleNPCMessage extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		List<String> messages = new ArrayList<String>();
		for (int i = 0; i < parameters.length; i++) {
			if (i == 0) {
				continue;
			}
			messages.add((String) parameters[i]);
		}
		sendNPCDialogue(npcId, ChatAnimation.NORMAL, messages.toArray(new String[messages.size()]));
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}

}
