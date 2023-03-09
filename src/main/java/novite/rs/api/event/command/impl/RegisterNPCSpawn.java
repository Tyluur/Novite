package novite.rs.api.event.command.impl;

import java.util.List;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.World;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.NPCAutoSpawn;
import novite.rs.utility.game.json.impl.NPCAutoSpawn.Direction;
import novite.rs.utility.game.npc.NPCSpawning;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 21, 2014
 */
public class RegisterNPCSpawn extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "n" };
	}

	@Override
	public void execute(Player player) {
		NPCAutoSpawn autospawn = JsonHandler.getJsonLoader(NPCAutoSpawn.class);
		List<NPCSpawning> spawns = autospawn.load();
		int id = Integer.parseInt(cmd[1]);
		Direction direction = cmd.length > 2 ? Direction.getDirection(cmd[2]) : Direction.NORTH;
		spawns.add(new NPCSpawning(player.getX(), player.getY(), player.getPlane(), id, direction));
		autospawn.save(spawns);
		World.spawnNPC(id, player, -1, true);
	}

}
