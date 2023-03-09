package novite.rs.api.event.command.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import novite.rs.Constants;
import novite.rs.api.event.command.CommandHandler;
import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.api.event.listeners.interfaces.Scrollable;
import novite.rs.game.player.Player;
import novite.rs.utility.game.ChatColors;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class CommandList extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.MODERATOR;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "commands" };
	}

	@Override
	public void execute(Player player) {
		List<CommandSkeleton> list = CommandHandler.get().getAvailableCommands(player);
		ListIterator<CommandSkeleton> it = list.listIterator();
		Collections.sort(list, new Comparator<CommandSkeleton>() {

			@Override
			public int compare(CommandSkeleton x, CommandSkeleton y) {
				int startComparison = CommandList.compare(x.getRightsRequired().ordinal(), y.getRightsRequired().ordinal());
				return startComparison != 0 ? startComparison : CommandList.compare(x.getRightsRequired().ordinal(), y.getRightsRequired().ordinal());
			}
		});

		List<String> messageList = new ArrayList<String>();
		List<String> completed = new ArrayList<String>();
		while (it.hasNext()) {
			CommandSkeleton entry = it.next();
			if (!completed.contains(entry.toString())) {
				StringBuilder params = new StringBuilder();
				int rightReq = entry.getRightsRequired().ordinal();
				String group = rightReq == 3 ? "OWNER" : rightReq == 2 ? "ADMIN" : rightReq == 1 ? "MOD" : "PLAYER";
				String color = rightReq == 3 ? ChatColors.RED : rightReq == 2 ? ChatColors.BLUE : rightReq == 1 ? ChatColors.WHITE : ChatColors.MAROON;

				for (int i = 0; i < entry.getCommandApplicable().length; i++) {
					params.append(entry.getCommandApplicable()[i] + "" + (i == entry.getCommandApplicable().length - 1 ? "" : ", "));
				}
				messageList.add((Constants.DEBUG ? entry.getClass().getSimpleName() : "") + "[<col=" + color + ">" + group + "</col>] ::" + params);
				completed.add(entry.toString());
			}
		}
		Scrollable.sendScroll(player, "Commands List", messageList.toArray(new String[messageList.size()]));
	}

	private static int compare(long a, long b) {
		return a < b ? -1 : a > b ? 1 : 0;
	}

}
