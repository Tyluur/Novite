package novite.rs.utility.game;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import novite.rs.api.database.DatabaseConnection;
import novite.rs.game.World;
import novite.rs.utility.logging.types.FileLogger;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Aug 11, 2013
 */
public class GlobalPlayerInfo {

	/**
	 * Updates the player count on the website if an update is required
	 */
	public void updateIfNecessary() {
		int players = getPlayersOnline();
		int max = getMaxPlayers();
		if (players > max) {
			updateMaxPlayers(players);
		}
	}

	/**
	 * Updates the new players text file with the amount of players in the day.
	 */
	public void updateNewPlayers() {
		List<String> lines = FileLogger.getFileLogger().getFileText("newplayers/");
		if (lines.size() == 0) {
			FileLogger.getFileLogger().writeLog("newplayers/", "1", false);
		} else {
			String firstLine = lines.get(0);
			firstLine = firstLine.split("]")[1].trim();
			int count = Integer.parseInt(firstLine);
			FileLogger.getFileLogger().writeLog("newplayers/", (count + 1) + "", false);
		}
	}

	public void updatePlayersOnline() {
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		try {
			int count = getPlayersOnline();
			Statement stmt = connection.createStatement();
			stmt.execute("UPDATE `players` SET playersOnline='" + count + "'");
			System.out.println("Updated the player count to " + count + " successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.returnConnection();
			}
		}
	}

	/**
	 * Sends the update query to the database for the player count
	 *
	 * @param newCount
	 *            The player count
	 */
	private void updateMaxPlayers(int newCount) {
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("UPDATE `players` SET `maxPlayers` = '" + newCount + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.returnConnection();
			}
		}
	}

	/**
	 * Finds the max amount of players ever on the server from the database
	 *
	 * @return The number
	 */
	public int getMaxPlayers() {
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM `players` WHERE `maxPlayers` >= 0");
			if (rs.next()) {
				return rs.getInt("maxPlayers");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.returnConnection();
			}
		}
		return -1;
	}

	/**
	 * Gets the total amount of players online.
	 *
	 * @return
	 */
	public int getPlayersOnline() {
		return Math.round(World.getPlayers().size());
	}

	/**
	 * @return the instance
	 */
	public static GlobalPlayerInfo get() {
		return INSTANCE;
	}

	private static final GlobalPlayerInfo INSTANCE = new GlobalPlayerInfo();

}
