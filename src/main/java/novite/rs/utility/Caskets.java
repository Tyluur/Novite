package novite.rs.utility;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Aug 11, 2013
 */
public enum Caskets {

	/** The casket given when a player completes a scroll */
	PUZZLE_CASKET(19040, 0, 0),
	
	/** The casket dropped when a monster dies */
	REGULAR(7237, 25000, 75000);

	Caskets(int itemId, int baseCoinAmount, int extraCoinAmount) {
		this.itemId = itemId;
		this.baseCoinAmount = baseCoinAmount;
		this.extraCoinAmount = extraCoinAmount;
	}

	private final int itemId;
	private final int baseCoinAmount;
	private final int extraCoinAmount;

	/**
	 * @return the itemId
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * @return the baseCoinAmount
	 */
	public int getBaseCoinAmount() {
		return baseCoinAmount;
	}

	/**
	 * @return the extraCoinAmount
	 */
	public int getExtraCoinAmount() {
		return extraCoinAmount;
	}

	/**
	 * Gets a casket by the item id
	 * 
	 * @param itemId
	 *            The id of the item
	 * @return
	 */
	public static Caskets getCasket(int itemId) {
		for (Caskets caskets : Caskets.values()) {
			if (caskets.getItemId() == itemId) {
				return caskets;
			}
		}
		return null;
	}
}