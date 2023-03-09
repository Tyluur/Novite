package novite.rs.game.player.content.scrolls;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 21, 2014
 */
public enum ScrollType {

	EASY(50), 
	MEDIUM(40),
	HARD(35), 
	ELITE(20);

	ScrollType(double percentChance) {
		this.percentChance = percentChance;
	}

	/**
	 * @return the percentChance
	 */
	public double getPercentChance() {
		return percentChance;
	}

	private final double percentChance;

}
