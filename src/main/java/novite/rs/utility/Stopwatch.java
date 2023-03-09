package novite.rs.utility;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 14, 2014
 */
public class Stopwatch {

	public Stopwatch() {
		startTime = System.currentTimeMillis();
	}

	/**
	 * The time that was elapsed since the stop watch was started
	 * 
	 * @return
	 */
	public long elapsed() {
		return System.currentTimeMillis() - startTime;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * The time that the stopwatch was ticked to start at
	 */
	private final long startTime;

}
