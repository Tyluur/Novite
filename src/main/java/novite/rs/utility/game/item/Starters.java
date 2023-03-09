package novite.rs.utility.game.item;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Apr 1, 2014
 */
public class Starters {

	public Starters(String ip) {
		this.ip = ip;
		this.setAmount(0);
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * The ip of the starters
	 */
	private final String ip;

	/**
	 * The amount of starters received on this ip
	 */
	private int amount;

}
