package novite.rs.utility.game.item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 22, 2014
 */
public class ExchangeItem {
	
	public ExchangeItem(int itemId) {
		this.itemId = itemId;
	}
	
	/**
	 * @return the itemId
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * The id of the exchange item
	 */
	private final int itemId;
	
	/** 
	 * The list of prices it has gone for
	 */
	private final List<Integer> prices = new ArrayList<Integer>();
	
	/**
	 * Gets the average price of the item
	 * @return
	 */
	public int getAveragePrice() {
		int size = getPrices().size();
		if (size == 0)
			return -1;
		else {
			int total = 0;
			for (Integer price : getPrices()) {
				total += price;
			}
			return (total / size);
		}
	}

	/**
	 * @return the prices
	 */
	public List<Integer> getPrices() {
		return prices;
	}

}
