package novite.rs.utility.game.item;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 9, 2014
 */
public class ItemBonus {
	
	public ItemBonus(int itemId, int[] bonuses) {
		this.itemId = itemId;
		this.bonuses = bonuses;
	}
	
	/**
	 * @return the itemId
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * @return the bonuses
	 */
	public int[] getBonuses() {
		return bonuses;
	}

	private final int itemId;
	private final int[] bonuses;

}
