package novite.rs.api.console.input;

import novite.rs.api.console.ConsoleInput;
import novite.rs.game.World;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ServerInformation;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 20, 2014
 */
public class ServerInformationInput implements ConsoleInput {

	@Override
	public String[] getPropableInputs() {
		return new String[] { "serverinfo" };
	}

	@Override
	public void onInput() {
		StringBuilder bldr = new StringBuilder();
	    Runtime runtime = Runtime.getRuntime();
		long inUse = Runtime.getRuntime().totalMemory();
		
		bldr.append("Uptime: " + ServerInformation.get().getGameUptime() + ". Ticks: " + World.getGameWorker().getDelay() + "\n");
		bldr.append("Cores: " + runtime.availableProcessors() + ". Memory Usage: " + ServerInformation.get().readable(inUse, true) + ". Free Memory: " + Utils.format(runtime.freeMemory()) + ". Maximum Memory: " + Utils.format(runtime.maxMemory()) + ". Total Available: " + Utils.format(runtime.totalMemory()));
		
		System.err.println(bldr.toString());
	/*	
	    Runtime runtime = Runtime.getRuntime();

	    NumberFormat format = NumberFormat.getInstance();

	    StringBuilder sb = new StringBuilder();
	    long maxMemory = runtime.maxMemory();
	    long allocatedMemory = runtime.totalMemory();
	    long freeMemory = runtime.freeMemory();

	    sb.append("free memory: " + format.format(freeMemory / 1024) + "\n");
	    sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
	    sb.append("max memory: " + format.format(maxMemory / 1024) + "\n");
	    sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");
	    
		System.err.println("Uptime: " + ServerInformation.get().getGameUptime() + ", Last Tick Speed: " + GameWorker.get().getDelay() + "\n" + sb.toString());
*/	}

}
