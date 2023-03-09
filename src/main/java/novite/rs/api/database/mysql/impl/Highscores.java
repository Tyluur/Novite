package novite.rs.api.database.mysql.impl;

import static novite.rs.game.player.Skills.CONSTRUCTION;
import static novite.rs.game.player.Skills.DUNGEONEERING;
import static novite.rs.game.player.Skills.HUNTER;
import static novite.rs.game.player.Skills.SUMMONING;

import java.sql.SQLException;
import java.sql.Statement;

import novite.rs.Constants;
import novite.rs.api.database.DatabaseConnection;
import novite.rs.game.World;
import novite.rs.game.player.Player;

/**
 * Handles the updating of the player's highscores information
 * @author Tyluur<itstyluur@gmail.com>
 * @since December 13th 2013
 */
public class Highscores {

	/**
	 * Updates the mysql table with the players skilling information
	 *
	 * @param player
	 *            The player to update it for
	 */
	public static void saveNewHighscores(Player player) {
		if (player.getRights() > 2 || !Constants.isVPS) {
			return;
		}
		DatabaseConnection connection = World.getConnectionPool().nextFree();
		String name = player.getDisplayName();
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("DELETE FROM `hs_users` WHERE username = '" + name + "';");
			stmt.executeUpdate("INSERT INTO `hs_users` (`username`,`rights`,`overall_xp`,`attack_xp`,`defence_xp`,`strength_xp`,`constitution_xp`,`ranged_xp`,`prayer_xp`,`magic_xp`,`cooking_xp`,`woodcutting_xp`,`fletching_xp`,`fishing_xp`,`firemaking_xp`,`crafting_xp`,`smithing_xp`,`mining_xp`,`herblore_xp`,`agility_xp`,`thieving_xp`,`slayer_xp`,`farming_xp`,`runecrafting_xp`, `hunter_xp`, `construction_xp`, `summoning_xp`, `dungeoneering_xp`) VALUES ('" + name + "','" + player.getFacade().getRates().ordinal() + "','" + (player.getSkills().getTotalExp()) + "'," + player.getSkills().getXp(0) + "," + player.getSkills().getXp(1) + "," + player.getSkills().getXp(2) + "," + player.getSkills().getXp(3) + "," + player.getSkills().getXp(4) + "," + player.getSkills().getXp(5) + "," + player.getSkills().getXp(6) + "," + player.getSkills().getXp(7) + "," + player.getSkills().getXp(8) + "," + player.getSkills().getXp(9) + "," + player.getSkills().getXp(10) + "," + player.getSkills().getXp(11) + "," + player.getSkills().getXp(12) + "," + player.getSkills().getXp(13) + "," + player.getSkills().getXp(14) + "," + player.getSkills().getXp(15) + "," + player.getSkills().getXp(16) + "," + player.getSkills().getXp(17) + "," + player.getSkills().getXp(18) + "," + player.getSkills().getXp(19) + "," + player.getSkills().getXp(20) + "," + player.getSkills().getXp(HUNTER) + "," + player.getSkills().getXp(CONSTRUCTION) + "," + player.getSkills().getXp(SUMMONING) + "," + player.getSkills().getXp(DUNGEONEERING) + ");");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connection.returnConnection();
		}
	}

}
