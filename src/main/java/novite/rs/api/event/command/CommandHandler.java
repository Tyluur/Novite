package novite.rs.api.event.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import novite.rs.Constants;
import novite.rs.game.player.Player;
import novite.rs.utility.logging.types.FileLogger;
import novite.rs.utility.tools.FileClassLoader;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Feb 28, 2014
 */
public class CommandHandler {

	/**
	 * Handles the command that has been typed
	 *
	 * @param player
	 *            The player typing the command
	 * @param cmd
	 *            The command typed
	 */
	public void handleCommand(Player player, String cmd) {
		try {
			CommandSkeleton command = commands.get(cmd.split(" ")[0].toLowerCase());
			if (command != null) {
				if (player.getRights() >= command.getRightsRequired().ordinal()) {
					command.setCommand(cmd).execute(player);
					FileLogger.getFileLogger().writeLog("cmd/", player.getDisplayName() + " used command:\t[" + cmd + "]", true);
				}
			}
		} catch (Exception e) {
			if (Constants.DEBUG)
				e.printStackTrace();
			player.sendMessage("Error parsing command! Retry with correct parameters if required.");
		}
	}

	/**
	 * Loads up all of the game commands to the map and prints out data about
	 * loading speed and size of map
	 */
	public void initialize() {
		commands.clear();
		for (Object packet : FileClassLoader.getClassesInDirectory(CommandSkeleton.class.getPackage().getName() + ".impl")) {
			CommandSkeleton skeleton = (CommandSkeleton) packet;
			if (skeleton.getCommandApplicable() != null) {
				for (String parameter : skeleton.getCommandApplicable()) {
					commands.put(parameter.toLowerCase(), skeleton);
				}
			} else {
				throw new IllegalStateException("Could not register " + skeleton.getClass().getCanonicalName() + "; no parameters");
			}
		}
	}

	public List<CommandSkeleton> getAvailableCommands(Player player) {
		List<CommandSkeleton> list = new ArrayList<CommandSkeleton>();
		Iterator<Entry<String, CommandSkeleton>> it = commands.entrySet().iterator();
		int rights = player.getRights();
		while (it.hasNext()) {
			Entry<String, CommandSkeleton> entry = it.next();
			int rightReq = entry.getValue().getRightsRequired().ordinal();
			if (rights >= rightReq) {
				list.add(entry.getValue());
			}
		}
		return list;
	}

	public static CommandHandler get() {
		return INSTANCE;
	}

	private final Map<String, CommandSkeleton> commands = new HashMap<String, CommandSkeleton>();
	private static final CommandHandler INSTANCE = new CommandHandler();

}
