package novite.rs.api.event.command.impl;

import java.util.List;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.World;
import novite.rs.game.WorldObject;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ObjectSpawnLoader;
import novite.rs.utility.game.json.impl.ObjectSpawnLoader.ObjectSpawn;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 21, 2014
 */
public class SpawnObject extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "object", "obj" };
	}

	@Override
	public void execute(Player player) {
		boolean save = false;
		try {
			int type = cmd.length > 2 ? Integer.parseInt(cmd[2]) : 10;
			int rotation = cmd.length == 4 ? Integer.parseInt(cmd[3]) : 0;
			if (type > 22 || type < 0) {
				type = 10;
			}
			WorldObject object = new WorldObject(Integer.valueOf(cmd[1]), type, rotation, player.getX(), player.getY(), player.getPlane());
			World.spawnObject(object);
			if (save) {
				ObjectSpawnLoader loader = JsonHandler.getJsonLoader(ObjectSpawnLoader.class);
				List<ObjectSpawn> spawns = loader.load();
				spawns.add(new ObjectSpawn(object.getId(), object.getType(), object.getRotation(), object.getX(), object.getY(), object.getPlane(), true));
				loader.save(spawns);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

}
