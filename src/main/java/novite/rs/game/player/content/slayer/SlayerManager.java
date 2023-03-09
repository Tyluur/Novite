package novite.rs.game.player.content.slayer;

import java.io.Serializable;

import novite.rs.game.player.Player;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 21, 2014
 */
public class SlayerManager implements Serializable {

	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * Displays the rewards interface
	 * 
	 * @param interfaceId
	 *            The rewards interface id
	 */
	public void displayRewards(int interfaceId) {
		player.closeInterfaces();
		sendPoints(interfaceId);
		switch (interfaceId) {
		case LEARN_INTERFACE:
			int startLine = 25;
			for (Abilities ability : Abilities.values()) {
				player.getPackets().sendIComponentText(interfaceId, startLine, unlockedAbilities[ability.ordinal()] ? "<col=" + ChatColors.GREEN + ">UNLOCKED</col>" : "Locked");
				startLine++;
			}
			break;
		case ASSIGN_INTERFACE:
			int length = Utils.getInterfaceDefinitionsComponentsSize(interfaceId);
			for (int line = 0; line < length; line++) {
				if (line == 25 || line == 19 || (line >= 20 && line <= 22))
					continue;
				player.getPackets().sendIComponentText(interfaceId, line, "");
			}
			for (int i = 37; i <= 41; i++) {
				player.getPackets().sendHideIComponent(interfaceId, i, true);
			}
			player.getPackets().sendIComponentText(interfaceId, 23, "Cancel Task");
			player.getPackets().sendIComponentText(interfaceId, 26, "30 Points / 500K");
			break;
		}
		player.getInterfaceManager().sendInterface(interfaceId);
	}

	/**
	 * Sends the amount of points we have over the interface
	 * 
	 * @param interfaceId
	 *            The interface to send over
	 */
	public void sendPoints(int interfaceId) {
		player.getPackets().sendIComponentText(interfaceId, interfaceId == BUY_INTERFACE ? 20 : interfaceId == LEARN_INTERFACE ? 18 : 19, "" + points);
	}

	/**
	 * Remove the points if possible, otherwise returns false
	 * 
	 * @param amount
	 *            The amount of points to remove
	 */
	public boolean removePoints(int amount) {
		int newPoints = points - amount;
		if (newPoints < 0) {
			player.sendMessage("You don't have enough points to complete this transaction.");
			return false;
		}
		points -= amount;
		return true;
	}

	/**
	 * Finding out if the abilty has been unlocked by the player
	 * 
	 * @param ability
	 *            The ability to check for
	 * @return
	 */
	public boolean hasUnlockedAbility(Abilities ability) {
		return unlockedAbilities[ability.ordinal()];
	}

	/**
	 * Unlocks the ability if it isn't already unlocked
	 * 
	 * @param ability
	 *            The ability to unlock
	 */
	public boolean unlockAbility(Abilities ability) {
		if (unlockedAbilities[ability.ordinal()])
			return false;
		unlockedAbilities[ability.ordinal()] = true;
		return true;
	}

	/**
	 * @return the points
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * @param points
	 *            the points to set
	 */
	public void setPoints(int points) {
		this.points = points;
	}

	/**
	 * The array of unlocked abilities
	 */
	private final boolean[] unlockedAbilities = new boolean[Abilities.values().length];

	/**
	 * The amount of slayer points we have
	 */
	private int points;

	/** The important interface ids */
	public static final int BUY_INTERFACE = 164; // 20
	public static final int LEARN_INTERFACE = 163; // 18
	public static final int ASSIGN_INTERFACE = 161; // 19

	private transient Player player;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3841832249429152521L;

}
