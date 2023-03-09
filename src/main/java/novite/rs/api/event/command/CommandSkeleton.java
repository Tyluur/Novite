package novite.rs.api.event.command;

import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Feb 28, 2014
 */
public abstract class CommandSkeleton {

	/**
	 * Gets the {@link Rights} level rights required
	 *
	 * @return
	 */
	public abstract Rights getRightsRequired();

	/**
	 * Gets the array of commands that can be used to identify the command
	 *
	 * @return
	 */
	public abstract String[] getCommandApplicable();

	/**
	 * Executes the command
	 *
	 * @param player
	 *            The player typing the command
	 */
	public abstract void execute(Player player);

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public CommandSkeleton setCommand(String command) {
		this.command = command;
		cmd = command.split(" ");
		return this;
	}

	/**
	 * The command typed by the user
	 */
	protected String command = "";

	/**
	 * The split command string
	 */
	protected String[] cmd = new String[] {};

	/**
	 * Gets the completed string from the given index
	 *
	 * @param cmd
	 *            The command array
	 * @param index
	 *            The index to begin
	 * @return
	 */
	public static String getCompleted(String[] cmd, int index) {
		StringBuilder sb = new StringBuilder();
		for (int i = index; i < cmd.length; i++) {
			if (i == cmd.length - 1 || cmd[i + 1].startsWith("+")) {
				return sb.append(cmd[i]).toString();
			}
			sb.append(cmd[i]).append(" ");
		}
		return "null";
	}

}
