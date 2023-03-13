package novite.rs.api.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import novite.rs.api.console.input.EntityInformationInput;
import novite.rs.api.console.input.ServerInformationInput;
import novite.rs.api.console.input.ShutdownInput;
import novite.rs.engine.CoresManager;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 20, 2014
 */
public class ConsoleListener {

	/**
	 * Preparing everything and populatng the {@link #inputs} map
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public void prepare() throws InstantiationException, IllegalAccessException {
		populateList();
		listen();
	}

	/**
	 * Populating the list with console input classes
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private void populateList() throws InstantiationException, IllegalAccessException {
		putInformation(EntityInformationInput.class.newInstance());
		putInformation(ServerInformationInput.class.newInstance());
		putInformation(ShutdownInput.class.newInstance());
	}

	/**
	 * Puts the information for the class into the map
	 * 
	 * @param clazz
	 */
	private void putInformation(ConsoleInput clazz) {
		for (String propable : clazz.getPropableInputs()) {
			inputs.put(propable, clazz);
		}
	}

	/**
	 * Listens for the console input on a new thread and gives the user the
	 * result of their requested input
	 */
	public void listen() {
		CoresManager.slowExecutor.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				try {
			        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					String result = br.readLine();
					if (result == null)
						return;
					if (result.startsWith("-")) {
						ConsoleInput input = inputs.get(result.replaceFirst("-", ""));
						if (input != null) {
							input.onInput();
						} else {
							System.err.println("Invalid console input: " + result);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
		/*new Thread(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("Console Listener");
				while (!CoresManager.shutdown) {
					try {
						String result = TextIO.getlnString();
						if (result.startsWith("-")) {
							ConsoleInput input = inputs.get(result.replaceFirst("-", ""));
							if (input != null) {
								input.onInput();
							} else {
								System.err.println("Invalid console input: " + result);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();*/
	}

	/**
	 * The map of the possible inputs
	 */
	private Map<String, ConsoleInput> inputs = new HashMap<>();

	/**
	 * @return the listener
	 */
	public static ConsoleListener get() {
		return LISTENER;
	}

	/**
	 * The instance of the listener
	 */
	private static final ConsoleListener LISTENER = new ConsoleListener();

}
