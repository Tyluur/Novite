package novite.rs.game.minigames.akrisae;

import novite.rs.game.item.Item;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 25, 2014
 */
public class AkrisaeFormulae {

	/**
	 * Gets the amount of items to loot
	 * 
	 * @param points
	 *            The points the player has
	 * @return
	 */
	public static int getLootAmount(int points) {
		if (points == 5)
			return 2;
		else if (points == 15)
			return 3;
		else if (points == 30)
			return 4;
		else if (points == 50)
			return 5;
		return 1;
	}

	/**
	 * Gets the amount of points to give the player
	 * 
	 * @param waves
	 *            The waves completed
	 * @param killed
	 *            The brothers killed
	 * @return
	 */
	public static int getPointsToAdd(int waves, int killed) {
		int newPoints = 0;

		newPoints += (waves * killed);
		newPoints /= 3;

		return newPoints;
	}

	/**
	 * The chance to get a barrow item
	 * 
	 * @return
	 */
	public static int getBarrowChance(int points) {
		int chance = 0;

		if (points == 5)
			chance = 5;
		else if (points == 15)
			chance = 10;
		else if (points == 30)
			chance = 18;
		else if (points == 50)
			chance = 30;

		return chance;
	}
}
