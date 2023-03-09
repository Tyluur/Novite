package novite.rs.networking;

import java.util.ArrayList;

import novite.rs.Constants;

/**
 * Filters the amount of connections that can be made to the network
 *
 * @author Tyluur<itstyluur@gmail.com>
 */
public class ConnectionFilteration {

	/**
	 * If the ip is connected
	 *
	 * @param ip
	 *            The ip
	 */
	public static boolean connected(String ip) {
		return connections.contains(ip);
	}

	/**
	 * Adds the ip to the list
	 *
	 * @param ip
	 *            The ip
	 */
	public static void add(String ip) {
		connections.add(ip);
	}

	/**
	 * Removes the ip from the list
	 *
	 * @param ip
	 *            The ip
	 */
	public static void remove(String ip) {
		connections.remove(ip);
	}

	/**
	 * Gets the amount of sessions connected from this ip
	 *
	 * @param ip
	 *            The ip to check for
	 */
	public static int getAmountConnected(String ip) {
		int amount = 1;
		for (int i = 0; i < connections.size(); i++) {
			if (connections.get(i).equalsIgnoreCase(ip)) {
				amount++;
			}
		}
		return amount;
	}

	/**
	 * Gets the list of connected ips
	 */
	public static ArrayList<String> getConnections() {
		return connections;
	}

	private static ArrayList<String> connections = new ArrayList<String>(Constants.PLAYERS_LIMIT * 3);
}