package novite.rs.engine.process;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import novite.rs.utility.tools.FileClassLoader;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 20, 2014
 */
public class ProcessManagement {

	public ProcessManagement() {
		loadedClasses = FileClassLoader.getClassesInDirectory(ProcessManagement.class.getPackage().getName() + ".impl");
		service = Executors.newScheduledThreadPool(1);
	}

	private final List<Object> loadedClasses;

	/**
	 * Registers all events to the {@link #service} thread pool
	 */
	public void registerEvents() {
		for (Object clazz : loadedClasses) {
			TimedProcess process = (TimedProcess) clazz;
			startTimedProcess(process);
		}
	}

	/**
	 * Starts a new game event on the executor with the correct delay
	 *
	 * @param event
	 */
	private void startTimedProcess(final TimedProcess process) {
		service.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					process.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0, process.getTimer().getDelay(), process.getTimer().getTimeUnit());
	}

	/**
	 * @return the instance
	 */
	public static ProcessManagement get() {
		return INSTANCE;
	}

	/**
	 * The executor service
	 */
	private final ScheduledExecutorService service;

	/**
	 * The instance of this class
	 */
	private static final ProcessManagement INSTANCE = new ProcessManagement();
}
