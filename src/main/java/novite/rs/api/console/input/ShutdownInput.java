package novite.rs.api.console.input;

import novite.rs.api.console.ConsoleInput;
import novite.rs.game.World;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Aug 7, 2014
 */
public class ShutdownInput implements ConsoleInput {

	@Override
	public String[] getPropableInputs() {
		return new String[] { "shutdown" };
	}

	@Override
	public void onInput() {
		World.safeShutdown(false, 1);
	}

}
