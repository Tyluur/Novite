package novite.rs.utility.game.npc;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 30, 2014
 */
public enum NPCSpeaking {

	ZOMBIE_PROTESTER(
	2833,
	"Help! Zombies are taking over our land!"),
	ITEM_REFUNDER(
	5026,
	"Talk to me to buy back untradeables!"),
	POS_MANAGER(
	2593,
	"Check out Player Owned Shops by talking to me!");

	NPCSpeaking(int id, String message) {
		this.ids = new int[] { id };
		this.message = message;
	}

	/**
	 * @return the ids
	 */
	public int[] getIds() {
		return ids;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	private final int[] ids;
	private final String message;

	/**
	 * Gets the message of the npc
	 *
	 * @param id
	 *            The id of the npc
	 * @return
	 */
	public static String getMessage(int id) {
		for (NPCSpeaking npcs : NPCSpeaking.values()) {
			for (int i = 0; i < npcs.ids.length; i++) {
				if (npcs.getIds()[i] == id) {
					return npcs.getMessage();
				}
			}
		}
		return null;
	}
}
