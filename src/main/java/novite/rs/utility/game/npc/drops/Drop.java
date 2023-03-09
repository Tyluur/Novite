package novite.rs.utility.game.npc.drops;

import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.utility.Utils;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 21, 2014
 */
public class Drop {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int timesReached = 0;
		int count = 0;
		while (true) {
			count++;
			double random = Utils.getRandomDouble(100);
			if (random <= Chance.CUSTOM_RARE.getChance()) {
				System.err.println("Got here on try # " + count + ": " + random);
				timesReached++;
				if (timesReached == 5) {
					break;
				}
			}
		}
	}
	
	public enum Chance {

		ALWAYS(100), COMMON(90), UNCOMMON(50), VARIES(30), UNKNOWN(25), RARE(10), VERY_RARE(5), CUSTOM_RARE(0.75);

		private double chance;

		Chance(double chance) {
			this.chance = chance;
		}

		public double getChance() {
			return chance;
		}

		public static Chance getChanceByName(String name) {
			for (Chance chance : Chance.values()) {
				if (chance.name().replaceAll("_", " ").equalsIgnoreCase(name)) {
					return chance;
				}
			}
			return null;
		}
	}


	private final int minAmount, maxAmount;
	private int itemId;
	private Chance rate;

	public Drop(int itemId, Chance rate, int amount) {
		this.setItemId(itemId);
		this.setRate(rate);
		this.minAmount = amount;
		this.maxAmount = amount;
	}

	public Drop(int itemId, Chance rate, int minAmount, int maxAmount) {
		this.setItemId(itemId);
		this.setRate(rate);
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
	}

	public int getMinAmount() {
		return minAmount;
	}

	public int getExtraAmount() {
		return maxAmount - minAmount;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public int getItemId() {
		return itemId;
	}

	public Chance getRate() {
		return rate;
	}

	@Override
	public String toString() {
		return "[DROP]" + ItemDefinitions.getItemDefinitions(getItemId()).getName() + " - MIN { " + getMinAmount() + " } MAX { " + getMaxAmount() + " } CHANCE { " + getRate() + " }\n";
	}

	/**
	 * @param rate
	 *            the rate to set
	 */
	public void setRate(Chance rate) {
		this.rate = rate;
	}

	/**
	 * @param itemId
	 *            the itemId to set
	 */
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
}