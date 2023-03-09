package novite.rs.game.player.dialogues;

import java.util.ArrayList;
import java.util.List;

import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.player.content.Magic;

public class Transportation extends Dialogue {

	// Ring of duelling
	// Combat bracelet

	public static int EMOTE = 9603, GFX = 1684;

	@Override
	public void start() {
		List<String> titles = new ArrayList<String>();
		titles.add("Select an Option");
		for (int i = 0; i < parameters.length; i++) {
			if (i == 0 || (i % 2 == 0 && i != 0) && i != (parameters.length - 1)) {
				titles.add((String) parameters[i]);
			}
		}
		String[] options = titles.toArray(new String[titles.size()]);
		sendOptionsDialogue(options);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		boolean teleported = false;
		if (componentId == 1) {
			teleported = Magic.sendItemTeleportSpell(player, true, EMOTE, GFX, 4, (WorldTile) parameters[1]);
		} else if (componentId == 2) {
			teleported = Magic.sendItemTeleportSpell(player, true, EMOTE, GFX, 4, (WorldTile) parameters[3]);
		} else if (componentId == 3) {
			teleported = Magic.sendItemTeleportSpell(player, true, EMOTE, GFX, 4, (WorldTile) parameters[5]);
		} else if (componentId == 4) {
			teleported = Magic.sendItemTeleportSpell(player, true, EMOTE, GFX, 4, (WorldTile) parameters[7]);
		}
		if (!teleported) {
			end();
			return;
		}
		Item item = player.getInventory().getItems().lookup((Integer) parameters[8]);
		if (item.getId() >= 3853 && item.getId() <= 3865 || item.getId() >= 10354 && item.getId() <= 10361) {
			item.setId(item.getId() + 2);
		} else if (item.getId() == 3867 || item.getId() == 10362 || item.getId() == 2566) {
			item.setId(-1);
		} else if (item.getId() >= 2552 && item.getId() <= 2567) {
			item.setId(item.getId() + 2);
		} else {
			item.setId(item.getId() - 2);
		}
		if (item.getId() != -1)
			player.getInventory().refresh(player.getInventory().getItems().getThisItemSlot(item));
		else
			player.getInventory().deleteItem(item);
		end();
	}

	@Override
	public void finish() {
	}

}
