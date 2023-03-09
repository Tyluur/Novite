package novite.rs.game.minigames.runeslayer;

import java.util.ArrayList;
import java.util.List;

import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.game.player.clans.Clan;
import novite.rs.game.player.clans.ClanMember;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Dec 24, 2013
 */
public class RuneSlayerLobby {

	/**
	 * Makes sure the player can enter the game, and calls
	 * {@link #passToGame(Clan, Player)} once requirements have been met.
	 *
	 * @param player
	 *            The player entering the game.
	 */
	public static void confirmClanRequirements(Player player) {
		try {
			if (player.getClanManager() == null) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You must be in a clan to start RuneSlayer.", "Talk to the RuneSlayer master if you need help.");
				return;
			}
			if (!player.getClanManager().getChannelPlayers().contains(player)) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You must be in a clan to start RuneSlayer.", "Talk to the RuneSlayer master if you need help.");
				return;
			}
			if (!player.getClanManager().getClan().getClanLeaderUsername().equals(player.getUsername())) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You must be the owner of the clan you're in to start RuneSlayer", "Tell your clan leader that you want to start RuneSlayer with them.", "", "Talk to the RuneSlayer master if you need help.");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			player.getDialogueManager().startDialogue("SimpleMessage", "You must be in a clan to enter RuneSlayer", "Talk to the RuneSlayer master if you need help.");
			return;
		}
		List<Player> players = new ArrayList<Player>();
		for (ClanMember m : player.getClanManager().getClan().getMembers()) {
			Player p = World.getPlayer(m.getUsername());
			if (p != null && p.withinDistance(player)) {
				players.add(p);
			}
		}
		passToGame(players, player.getClanManager().getClan());
	}

	/**
	 * Bringing everybody who is in the clan to the game.
	 * 
	 * @param clan
	 *            The clan that is being moved to the game.
	 * @param p
	 *            The
	 */
	private static void passToGame(List<Player> players, Clan clan) {
		RuneSlayerFloor floor = new RuneSlayerFloor(players);
		for (Player p : players) {
			p.getControllerManager().startController("RuneSlayer", floor, clan);
		}
		floor.passToFloor(true);
	}

}