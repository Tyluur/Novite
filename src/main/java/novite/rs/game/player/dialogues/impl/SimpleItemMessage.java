package novite.rs.game.player.dialogues.impl;

import java.util.ArrayList;
import java.util.List;

import novite.rs.game.player.dialogues.Dialogue;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 21, 2014
 */
public class SimpleItemMessage extends Dialogue {

	@Override
	public void start() {
		int itemId = (int) parameters[0];
		List<String> messages = new ArrayList<>();
		for (int i = 0; i < parameters.length; i++) {
			if (i == 0)
				continue;
			if (parameters[i] instanceof String) {
				messages.add((String) parameters[i]);
			}
		}
		sendItemDialogue(itemId, messages.toArray(new String[messages.size()]));
	}

	@Override
	public void run(int interfaceId, int option) {
		end();
	}

	@Override
	public void finish() {
		
	}

}
