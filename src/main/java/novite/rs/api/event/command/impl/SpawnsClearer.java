package novite.rs.api.event.command.impl;

import java.util.Iterator;
import java.util.List;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.World;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.utility.game.Rights;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.NPCAutoSpawn;
import novite.rs.utility.game.npc.NPCSpawning;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 30, 2014
 */
public class SpawnsClearer extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "clearspawns" };
	}

	@Override
	public void execute(Player player) {
		List<Integer> npcIndexes = player.getRegion().getNPCsIndexes();
		if (npcIndexes == null)
			return;
		for (Integer index : npcIndexes) {
			NPC npc = World.getNPCs().get(index);
			if (npc == null || !npc.withinDistance(player))
				continue;
			List<NPCSpawning> spawns = ((NPCAutoSpawn) JsonHandler.getJsonLoader(NPCAutoSpawn.class)).load();
			Iterator<NPCSpawning> it = spawns.iterator();
			while (it.hasNext()) {
				NPCSpawning spawn = it.next();
				if (spawn == null) {
					continue;
				}
				if (spawn.getId() == npc.getId() && spawn.getX() == npc.getStartTile().getX() && spawn.getY() == npc.getStartTile().getY() && spawn.getZ() == npc.getStartTile().getPlane()) {
					it.remove();
				}
			}
			((NPCAutoSpawn) JsonHandler.getJsonLoader(NPCAutoSpawn.class)).save(spawns);
			npc.finish();
		}
	}

}
