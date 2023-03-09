package novite.rs.utility.game.npc;

import java.util.List;

import novite.rs.game.World;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.NPCAutoSpawn;

public final class NPCSpawns {

	public static final void loadNPCSpawns(int regionId) {
		NPCAutoSpawn autoSpawn = JsonHandler.getJsonLoader(NPCAutoSpawn.class);
		List<NPCSpawning> spawns = autoSpawn.getSpawns(regionId);
		if (spawns == null) {
			return;
		}
		for (NPCSpawning spawn : spawns) {
			World.spawnNPC(spawn.getId(), spawn.getTile(), -1, true);
		}
	}
}
