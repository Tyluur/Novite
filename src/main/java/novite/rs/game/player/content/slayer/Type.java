package novite.rs.game.player.content.slayer;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 20, 2014
 */
public enum Type {

	EASY(70, 150),

	MEDIUM(50, 80),

	HARD(25, 50),

	ELITE(10, 20);

	Type(int lowestAmount, int highestAmount) {
		this.lowestAmount = lowestAmount;
		this.highestAmount = highestAmount;
	}

	/**
	 * @return the lowestAmount
	 */
	public int getLowestAmount() {
		return lowestAmount;
	}

	/**
	 * @return the highestAmount
	 */
	public int getHighestAmount() {
		return highestAmount;
	}

	private final int lowestAmount;
	private final int highestAmount;

}
