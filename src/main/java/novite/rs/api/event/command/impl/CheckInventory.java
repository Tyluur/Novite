package novite.rs.api.event.command.impl;

import java.util.ArrayList;
import java.util.List;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.api.event.listeners.interfaces.Scrollable;
import novite.rs.game.World;
import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;
import novite.rs.utility.game.Rights;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 10, 2014
 */
public class CheckInventory extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.MODERATOR;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "checkinv" };
	}

	@Override
	public void execute(Player player) {

		String name = getCompleted(cmd, 1);
		if (World.containsPlayer(name)) {
			Player target = World.getPlayer(name);
			List<String> messages = new ArrayList<String>();
			messages.add(target.getUsername() + "'s inventory has: ");
			for (Item item : target.getInventory().getItems().toArray()) {
				if (item == null)
					continue;
				messages.add(Utils.format(item.getAmount()) + "x " + item.getName() + ". [" + item.getId() + "]");
			}
			Scrollable.sendQuestScroll(player, name + "'s inventory", messages.toArray(new String[messages.size()]));
			
		} else {
			player.sendMessage(name + " is an invalid name for a player.");
		}
	}

}
