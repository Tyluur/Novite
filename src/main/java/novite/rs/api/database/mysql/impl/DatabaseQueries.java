package novite.rs.api.database.mysql.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import novite.rs.api.database.DatabaseConnection;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.game.player.dialogues.SimpleMessage;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 7, 2014
 */
public class DatabaseQueries {

	/**
	 * Claims the gold points the player has purchased from the sql database
	 * 
	 * @param player
	 *            The player
	 */
	@SuppressWarnings("deprecation")
	public static void claimGoldPoints(Player player) {
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		String name = player.getUsername();
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM purchases WHERE delivered='0' AND acc='" + name.replaceAll("_", " ") + "';");
			boolean found = false;
			while (rs.next()) {
				double price = Double.parseDouble(rs.getString(3));
				connection.createStatement().executeUpdate("UPDATE `purchases` SET delivered='1' WHERE delivered='0' AND `acc`='" + name + "';");

				int goldPointsPurchased = (int) Math.ceil(100 * price);
				player.getFacade().rewardCoins(goldPointsPurchased);
				player.getFacade().setTotalPointsPurchased((long) (player.getFacade().getTotalPointsPurchased() + price));
				
				player.getAttributes().remove("checked_groups");
				player.getForumGroups().clear();
				player.addForumGroups();
				
				player.getDialogueManager().startDialogue(SimpleMessage.class, "You have just received " + goldPointsPurchased + " gold points.", "Exchange them at Party Pete in the Edgeville bank.", "<col=" + ChatColors.MAROON + ">You have now purchased $" + player.getFacade().getTotalPointsPurchased() + " in gold points.", "Thank you for supporting the server!");

				println("[" + new Date().toLocaleString() + "] " + player.getDisplayName() + " has purchased Gold Points for $" + Utils.format((long) price) + ".");
				found = true;
			}
			if (!found) {
				player.sendMessage("You have no gold points to claim.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.returnConnection();
		}
	}

	private static void println(String data) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("info/script/donations.txt", true));
			writer.write(data + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
