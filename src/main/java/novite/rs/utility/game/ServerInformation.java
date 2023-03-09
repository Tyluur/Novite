package novite.rs.utility.game;

import java.text.DecimalFormat;

import novite.Main;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Oct 12, 2013
 */
public class ServerInformation {

	public ServerInformation() {
		this.availableProcessors = Runtime.getRuntime().availableProcessors();
	}

	/**
	 * The amount of memory in use.
	 * 
	 * @param bytes
	 * @param si
	 * @return
	 */
	public String readable(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) {
			return bytes + " B";
		}
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public int getAvailableProcessors() {
		return availableProcessors;
	}

	/**
	 * Gets the uptime of the server in a formatted string in the following
	 * format: days, hours, minutes, seconds. This will also ensure the numbers
	 * are all two-digital for prettier formatting
	 * 
	 * @return
	 */
	public String getGameUptime() {
		long milliseconds = System.currentTimeMillis() - Main.STARTUP_TIME;
		int seconds = (int) (milliseconds / 1000) % 60;
		int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
		int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
		long days = milliseconds / (24 * 60 * 60 * 1000);
		DecimalFormat nft = new DecimalFormat("#00.###");
		nft.setDecimalSeparatorAlwaysShown(false);
		return nft.format(days) + ":" + nft.format(hours) + ":" + nft.format(minutes) + ":" + nft.format(seconds);
	}

	/**
	 * The amount of processors that are available on the computer
	 */
	private final int availableProcessors;

	/**
	 * The getter
	 * 
	 * @return
	 */
	public static ServerInformation get() {
		return INSTANCE;
	}

	/** The instance of this class. */
	private static final ServerInformation INSTANCE = new ServerInformation();

}
