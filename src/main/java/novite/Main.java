package novite;

import novite.rs.Constants;
import novite.rs.api.console.ConsoleListener;
import novite.rs.engine.CoresManager;
import novite.rs.engine.game.GameLoader;
import novite.rs.networking.ServerChannelHandler;
import novite.rs.utility.Config;
import novite.rs.utility.Stopwatch;
import novite.rs.utility.logging.LoggerSetup;

public class Main {

	public static void main(String[] args) {
		try {
			/** Loading up the configuration */
			Config.get().load();
			/** Registers the custom System.println loggers */
			LoggerSetup.registerServerLoggers();
			/** Waits for all tasks to load before the next phase */
			GameLoader.get().getBackgroundLoader().waitForPendingTasks();
			/**
			 * Once everything is loaded the background loader service is shut
			 * down
			 */
			GameLoader.get().getBackgroundLoader().shutdown();
			/** Preparing the console listener */
			ConsoleListener.get().prepare();
			/** Showing the successful launch information in the console */
			System.out.println("Server loaded in " + STOPWATCH.elapsed() + " ms with SQL " + (Constants.SQL_ENABLED ? "en" : "dis") + "abled.");
			/** Setting the server startup time since everything has loaded */
			STARTUP_TIME = System.currentTimeMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void shutdown() {
		closeServices();
		System.out.println("Shutting down...");
	}

	public static void closeServices() {
		ServerChannelHandler.shutdown();
		CoresManager.shutdown();
	}

	public static void restart() {
		closeServices();
		System.gc();
		try {
			System.exit(2);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * The time when the server is fully prepared
	 */
	public static long STARTUP_TIME = -1;

	/**
	 * The stopwatch that is clocked when the server starts
	 */
	public static final Stopwatch STOPWATCH = new Stopwatch();
}
