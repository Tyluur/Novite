package novite.rs.api.database.mysql.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import novite.rs.Constants;
import novite.rs.api.database.DatabaseConnection;
import novite.rs.game.World;
import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.impl.VoteEasyAchievement;
import novite.rs.game.player.dialogues.SimpleMessage;
import novite.rs.game.player.dialogues.impl.SimpleItemMessage;
import novite.rs.utility.Utils;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 30, 2014
 */
public class VoteVerification {

    /**
     * Checks for the vote by the player's name in the database and gives them
     * their reward
     *
     * @param player The player checking their vote
     */
    public static void checkVote(Player player) {
        DatabaseConnection connection = World.getConnectionPool().nextFree();
        String username = player.getUsername();
        try {
            Statement stmt = connection.createStatement();
            username = username.replaceAll(" ", "_");
            ResultSet rs = stmt.executeQuery("SELECT rewardid FROM `has_voted` WHERE username = '" + username + "' and given = '0'");
            if (rs.next()) {
                switch (rs.getInt("rewardid")) {
                    case 0:
                        player.getInventory().addItem(new Item(VOTE_TOKEN, 1));
                        break;
                }
                /** Notifying the achievement to update */
                player.getAchievementManager().notifyUpdate(VoteEasyAchievement.class);

                /** Gives the player their vote bonuses for 12 hours */
                player.getFacade().setVoteBonus(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(12));
                player.getDialogueManager().startDialogue(SimpleMessage.class, "You now have vote bonuses for " + (TimeUnit.MILLISECONDS.toHours(player.getFacade().getVoteBonus() - System.currentTimeMillis())) + " hours!");

                /** Increments the times voted */
                player.getFacade().setTimesVoted(player.getFacade().getTimesVoted() + 1);

                /** Telling everyone online the player has voted */
                World.sendWorldMessage(Utils.formatPlayerNameForDisplay(player.getUsername()) + " has voted and received their reward from voting!", false, false);

                /** Thanking the player for voting */
                player.sendMessage("Thank you for voting for " + Constants.SERVER_NAME + ", your vote truly counts!");

                /** Updating the MySQL table */
                stmt.execute("DELETE FROM `has_voted` WHERE username = '" + username + "'");
            } else {
                player.sendMessage("You have not voted yet.");
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.returnConnection();
        }
    }

    /**
     * Checks the auth code in the database and gives a reward
     *
     * @param player The player
     * @param auth   The auth code
     */
    public static void checkAuth(Player player, String auth) {
        DatabaseConnection connection = World.getConnectionPool().nextFree();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT `authcode` FROM `authcodes` WHERE `authcode`= '" + auth + "' AND `recieved` = 0");
            if (rs.next()) {
                stmt.execute("UPDATE `authcodes` SET `recieved` = 1 WHERE `authcode` = '" + auth + "'");

                /** Giving rewards for successful auth codes */
                player.getInventory().addItem(10944, 5);
                player.getAchievementManager().notifyUpdate(VoteEasyAchievement.class);

                /** Notifying everyone ingame */
                World.sendWorldMessage(player.getDisplayName() + " has successfully voted and received their reward!", false, false);

                /** Gives the player their vote bonuses for 12 hours */
                player.getFacade().setVoteBonus(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(12));
                player.getDialogueManager().startDialogue(SimpleMessage.class, "You now have vote bonuses for " + (TimeUnit.MILLISECONDS.toHours(player.getFacade().getVoteBonus() - System.currentTimeMillis())) + " hours!");
            } else {
                player.sendMessage("There is no such auth code by the key: " + auth);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.returnConnection();
        }
    }

    /**
     * The id of the vote token
     */
    private static final int VOTE_TOKEN = 7775;

}
