package novite.rs.game.player.dialogues;

import java.util.ArrayList;
import java.util.List;

public class SimpleMessage extends Dialogue {

	@Override
	public void start() {
		List<String> messages = new ArrayList<String>();
		for (Object param : parameters) {
			messages.add((String) param);
		}
		sendDialogue(messages.toArray(new String[messages.size()]));
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}

}
