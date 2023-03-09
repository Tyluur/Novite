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
 * @since Jul 31, 2014
 */
public class MassDeleteSpawns extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.OWNER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "mdn" };
	}

	@Override
	public void execute(Player player) {
		List<Integer> npcs = player.getRegion().getNPCsIndexes();
		if (npcs == null) {
			return;
		}
		List<NPCSpawning> spawns = ((NPCAutoSpawn) JsonHandler.getJsonLoader(NPCAutoSpawn.class)).load();
		Iterator<NPCSpawning> it = spawns.iterator();
		for (Integer index : npcs) {
			NPC npc = World.getNPCs().get(index);
			if (npc == null || !npc.withinDistance(player, 10))
				continue;
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
			System.out.println("Removed a monster spawn! [" + npc + "]");
		}
	}

}
