package novite.rs.api.event.command.impl;

import java.util.Iterator;
import java.util.List;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.NPCAutoSpawn;
import novite.rs.utility.game.json.impl.NPCAutoSpawn.Direction;
import novite.rs.utility.game.npc.NPCSpawning;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 14, 2014
 */
public class ChangeDirection extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "changedir" };
	}

	@Override
	public void execute(Player player) {
		Direction direction = Direction.valueOf(getCompleted(cmd, 1));
		List<NPCSpawning> spawns = ((NPCAutoSpawn) JsonHandler.getJsonLoader(NPCAutoSpawn.class)).load();
		Iterator<NPCSpawning> it = spawns.iterator();
		while (it.hasNext()) {
			NPCSpawning spawn = it.next();
			if (spawn == null) {
				continue;
			}
			if (spawn.getX() == player.getX() && spawn.getY() == player.getY() && spawn.getZ() == player.getPlane()) {
				System.out.println("Direction: " + direction + ", Found " + spawn.getId() + " at your coords[" + NPCDefinitions.getNPCDefinitions(spawn.getId()).getName() + "]");
				spawn.setDirection(direction);
			}
		}
		((NPCAutoSpawn) JsonHandler.getJsonLoader(NPCAutoSpawn.class)).save(spawns);
	}

}
