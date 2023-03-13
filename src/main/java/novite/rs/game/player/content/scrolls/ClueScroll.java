package novite.rs.game.player.content.scrolls;

import java.io.Serializable;

import novite.rs.api.event.listeners.interfaces.Scrollable;
import novite.rs.game.WorldTile;
import novite.rs.game.player.Player;
import novite.rs.utility.game.ChatColors;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 21, 2014
 */
public abstract class ClueScroll implements Serializable {

	/**
	 * The ids of the animations
	 */
	public static final Integer RASPBERRY = 21, BOW = 4, CHEER = 9, JIG = 15, CLAP = 22, DANCE = 14, HEADBANG = 17, JUMP_FOR_JOY = 11, SALUTE = 23, SHRUG = 8, YAWN = 13;

	/**
	 * The name of the clue scroll
	 * 
	 * @return
	 */
	public abstract String getName();

	/**
	 * Gets the information interface id of the scroll. Use null if there is no
	 * interface
	 * 
	 * @return
	 */
	public abstract Integer getInformationInterface();

	/**
	 * Gets the animation required to complete this scroll. Use null if there is
	 * no animation.
	 * 
	 * @return
	 */
	public abstract Integer getAnimation();

	/**
	 * The array of hints that will be shown to the player. These are not valid
	 * for maps, so use null.
	 * 
	 * @return
	 */
	public abstract String[] getHints();

	/**
	 * Finding out if the player has the complete prerequisites for the scroll,
	 * such as correct armour equipped.
	 * 
	 * @param player
	 *            The player
	 * @return
	 */
	public abstract Boolean completePrequisites(Player player);

	/**
	 * Gets the tile the player must do an action on
	 * 
	 * @return
	 */
	public abstract WorldTile getActionTile();

	/**
	 * What to do when a scroll is opened
	 * 
	 * @param player
	 *            The player
	 */
	public void open(Player player) {
		boolean map = getAnimation() == null;
		if (map) {
			player.getInterfaceManager().sendInterface(getInformationInterface());
		} else {
			Scrollable.sendScroll(player, "<col=" + ChatColors.MAROON + ">Clue Scroll Hint", getHints());
		}
	}
	
	private static final long serialVersionUID = -8218739173530350402L;

}
