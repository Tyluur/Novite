package novite.rs.game.player.dialogues;

import java.util.ArrayList;
import java.util.List;

public class ItemMessage extends Dialogue {

	@Override
	public void start() {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < parameters.length; i++) {
			if (i == 0) {
				continue;
			}
			list.add((String) parameters[i]);
		}
		String[] messages = list.toArray(new String[list.size()]);
		sendItemDialogue((Integer) parameters[0], messages);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}

}
