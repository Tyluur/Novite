package novite.rs.utility.game;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 15, 2014
 */
public enum ReturnCode {
	
	INVALID_USERNAME_OR_PASSWORD(3),
	YOUR_ACCOUNT_HAS_BEEN_DISABLED(4),
	YOUR_ACCOUNT_IS_STILL_ONLINE(5),
	NOVITE_HAS_BEEN_UPDATED(6),
	WORLD_IS_FULL(7),
	UNABLE_TO_CONNECT_LOGINSERVER(8),
	LOGIN_LIMIT_EXCEEDED(9),
	INVALID_SESSION_ID(10),
	SERVER_IS_UPDATING(14),
	
	/** Custom ones */
	NULLED_ACCOUNT(20),
	UNREGISTERED_FORUM_ACCOUNT(26),
	INVALID_USERNAME(27),
	DATABASE_CONNECTION_ERROR(35);
	
	ReturnCode(int opcode) {
		this.opcode = opcode;
	}
	
	/**
	 * @return the opcode
	 */
	public int getOpcode() {
		return opcode;
	}

	private final int opcode;

}
