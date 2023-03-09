package novite.rs.engine.process;

import java.util.concurrent.TimeUnit;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 20, 2014
 */
public interface TimedProcess {

	/**
	 * Gets the timer of the task
	 *
	 * @return
	 */
	public Timer getTimer();

	/**
	 * Handles what to do every time the timer is processed
	 */
	public void execute();

	/**
	 * The timer class
	 *
	 * @author Tyluur
	 *
	 */
	public static class Timer {

		/**
		 * Constructs a new timer
		 *
		 * @param delay
		 *            The delay of the timer
		 * @param timeUnit
		 *            The unit of the timer
		 */
		public Timer(int delay, TimeUnit timeUnit) {
			this.delay = delay;
			this.timeUnit = timeUnit;
		}

		/**
		 * The delay of the timer in milliseconds
		 */
		public long getDelayInMs() {
			switch (timeUnit) {
				case DAYS:
					return TimeUnit.DAYS.toMillis(delay);
				case HOURS:
					return TimeUnit.MILLISECONDS.toMillis(delay);
				case MINUTES:
					return TimeUnit.MINUTES.toMillis(delay);
				case SECONDS:
					return TimeUnit.SECONDS.toMillis(delay);
				case MILLISECONDS:
					return delay;
				default:
					return delay;
			}
		}

		/**
		 * Gets the time unit
		 */
		public TimeUnit getTimeUnit() {
			return timeUnit;
		}

		/**
		 * Gets the delay
		 */
		public int getDelay() {
			return delay;
		}

		/**
		 * The delay
		 */
		private final int delay;

		/**
		 * The time unit
		 */
		private final TimeUnit timeUnit;
	}
}
