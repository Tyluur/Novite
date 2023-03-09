package novite.rs.utility.game.item;


/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 31, 2014
 */
public enum CustomItemPrices {

	BRONZE_LG(11814, 352),
	BRONZE_SK(11816, 352),
	IRON_LG(11818, 1232),
	IRON_SK(11820, 1232),
	STEEL_LG(11822, 4400),
	STEEL_SK(11824, 4400),
	BLACK_LG(11826, 9253),
	BLACK_SK(11828, 9253),
	MITHRIL_LG(11830, 11440),
	MITHRIL_SK(11832, 11440),
	ADAMANT_LG(11834, 32000),
	ADAMANT_SK(11836, 32000),
	PROSELYTE_LG(9666, 30000),
	PROSELYTE_SK(9670, 30000),
	RUNE_LG(11838, 218600),
	RUNE_SK(11840, 218600),
	GREEN_DHIDE(11864, 14200),
	BLUE_DHIDE_SET(11866, 16680),
	RED_DHIDE_SET(11868, 20010),
	BLACK_DHIDE_SET(11870, 24020),
	SPLITBARK(11876, 105000),
	BLUE_MYSTIC_SET(11872, 235000),
	LIGHT_MYSTIC_SET(11960, 235000),
	DARK_MYSTIC_SET(11962, 235000),
	PHOENIXNECK(11090, 35000);

	CustomItemPrices(int id, int price) {
		this.ids = new int[] { id };
		this.price = price;
	}

	CustomItemPrices(int[] ids, int price) {
		this.ids = ids;
		this.price = price;
	}

	private final int[] ids;
	private final int price;

	/**
	 * Gets the item prices instance by the item id
	 * 
	 * @param id
	 *            The item id
	 * @return
	 */
	public static CustomItemPrices getItemPrice(int id) {
		for (CustomItemPrices prices : CustomItemPrices.values()) {
			for (int id2 : prices.ids) {
				if (id2 == id) {
					return prices;
				}
			}
		}
		return null;
	}

	/**
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}

}
