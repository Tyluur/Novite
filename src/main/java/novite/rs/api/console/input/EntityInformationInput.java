package novite.rs.api.console.input;

import novite.rs.api.console.ConsoleInput;
import novite.rs.game.World;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 20, 2014
 */
public class EntityInformationInput implements ConsoleInput {

	@Override
	public String[] getPropableInputs() {
		return new String[] { "entityinfo" };
	}

	@Override
	public void onInput() {
		System.err.println("Information for all entities in the world: [" + World.getPlayers().size() + " players, " + World.getNPCs().size() + " npcs]");
	}

}
