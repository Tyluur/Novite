package novite.rs.api.event.command.impl;

import novite.rs.Constants;
import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 20, 2014
 */
public class SearchItemName extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "itemn" };
	}

	@Override
	public void execute(Player player) {
		StringBuilder bldr = new StringBuilder();
		for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
			Item item = new Item(i);
			if (item.getDefinitions().getName().toLowerCase().contains(getCompleted(cmd, 1).toLowerCase())) {
				bldr.append(i + ", ");
				player.getPackets().sendMessage(99, "[<col=FF0000>ITEM</col>] <col=" + ChatColors.LIGHT_BLUE + ">" + item.getDefinitions().getName() + "</col> - ID: " + item.getId() + "", player);
			}
		}
		if (Constants.DEBUG) {
			System.out.println(bldr.toString());
		}
	}

}
