package novite.rs.utility.logging.types;

import java.io.OutputStream;
import java.io.PrintStream;

import novite.rs.Constants;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Dec 14, 2013
 */
public class ErrorLogger extends PrintStream {

	public ErrorLogger(OutputStream out) {
		super(out);
	}

	@Override
	public void println(Object x) {
		logError(x);
	}

	@Override
	public void println(String x) {
		if (Constants.isVPS) {
			FileLogger.getFileLogger().writeDropboxLog("errors/", x, true);
		}
		super.println(x);
	}

	private void logError(Object object) {
		String text = object.toString();
		if (Constants.isVPS) {
			FileLogger.getFileLogger().writeDropboxLog("errors/", text, true);
		}
		super.println(object);
	}

}
