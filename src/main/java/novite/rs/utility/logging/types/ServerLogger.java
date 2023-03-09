package novite.rs.utility.logging.types;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.TimeZone;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Dec 14, 2013
 */
public class ServerLogger extends PrintStream {

	public ServerLogger(OutputStream out) {
		super(out);
	}

	@Override
	public void print(boolean message) {
		Throwable throwable = new Throwable();
		String name = throwable.getStackTrace()[2].getFileName().replaceAll(".java", "");
		String line = String.valueOf(throwable.getStackTrace()[2].getLineNumber());
		log(name + ":" + line, "" + message);
	}

	@Override
	public void print(int message) {
		Throwable throwable = new Throwable();
		String name = throwable.getStackTrace()[2].getFileName().replaceAll(".java", "");
		String line = String.valueOf(throwable.getStackTrace()[2].getLineNumber());
		log(name + ":" + line, "" + message);
	}

	@Override
	public void print(String message) {
		Throwable throwable = new Throwable();
		String name = throwable.getStackTrace()[2].getFileName().replaceAll(".java", "");
		String line = String.valueOf(throwable.getStackTrace()[2].getLineNumber());
		log(name + ":" + line, "" + message);
	}

	private void log(String className, String text) {
		super.print("[" + className + "][" + getDate() + "]" + text);
	}

	@SuppressWarnings("deprecation")
	private String getDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("America/Toronto"));
		return cal.getTime().toLocaleString();
	}

}
