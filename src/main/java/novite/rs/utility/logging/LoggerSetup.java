package novite.rs.utility.logging;

import novite.rs.utility.logging.types.ErrorLogger;
import novite.rs.utility.logging.types.ServerLogger;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Feb 22, 2014
 */
public class LoggerSetup {

	/**
	 * Registers the customized loggers for the server
	 */
	public static void registerServerLoggers() {
		System.setErr(new ErrorLogger(System.err));
		System.setOut(new ServerLogger(System.out));
	}

}
