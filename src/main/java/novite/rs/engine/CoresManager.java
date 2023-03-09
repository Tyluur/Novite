package novite.rs.engine;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import novite.rs.game.minigames.games.MainGameHandler;

public final class CoresManager {

	/**
	 * Initializes all important cores management systems.
	 */
	public static void init() {
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		serverWorkersCount = availableProcessors >= 6 ? availableProcessors - (availableProcessors >= 12 ? 6 : 4) : 2;
		serverWorkerChannelExecutor = serverWorkersCount > 1 ? Executors.newFixedThreadPool(serverWorkersCount, new DecoderThreadFactory()) : Executors.newSingleThreadExecutor(new DecoderThreadFactory());
		serverBossChannelExecutor = Executors.newSingleThreadExecutor(new DecoderThreadFactory());
		fastExecutor = new Timer("Fast Executor");
		slowExecutor = Executors.newScheduledThreadPool(2, new SlowThreadFactory());

		MainGameHandler.get().startUp();
	}

	/**
	 * Shuts down all core management systems
	 */
	public static void shutdown() {
		serverWorkerChannelExecutor.shutdown();
		serverBossChannelExecutor.shutdown();
		fastExecutor.cancel();
		slowExecutor.shutdown();
		shutdown = true;
	}

	private CoresManager() {

	}

	public static volatile boolean shutdown;
	public static ExecutorService serverWorkerChannelExecutor;
	public static ExecutorService serverBossChannelExecutor;
	public static Timer fastExecutor;
	public static ScheduledExecutorService slowExecutor;
	public static int serverWorkersCount;

	/*
	 * public static volatile boolean shutdown;
	 * 
	 * private static final int availableProcessors =
	 * Runtime.getRuntime().availableProcessors();
	 * 
	 * public static ExecutorService serverWorkerChannelExecutor =
	 * availableProcessors >= 6 ?
	 * Executors.newFixedThreadPool(availableProcessors - (availableProcessors
	 * >= 12 ? 7 : 5), new DecoderThreadFactory()) :
	 * Executors.newSingleThreadExecutor(new DecoderThreadFactory()); public
	 * static ExecutorService serverBossChannelExecutor =
	 * Executors.newSingleThreadExecutor(new DecoderThreadFactory()); public
	 * static Timer fastExecutor = new Timer("Fast Executor"); public static
	 * ScheduledExecutorService slowExecutor = availableProcessors >= 6 ?
	 * Executors.newScheduledThreadPool(availableProcessors >= 12 ? 4 : 2, new
	 * SlowThreadFactory()) : Executors.newSingleThreadScheduledExecutor(new
	 * SlowThreadFactory()); public static int serverWorkersCount =
	 * availableProcessors >= 6 ? availableProcessors - (availableProcessors >=
	 * 12 ? 7 : 5) : 1;
	 */
}