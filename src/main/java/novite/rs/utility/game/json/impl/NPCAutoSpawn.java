package novite.rs.utility.game.json.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import novite.rs.cache.Cache;
import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.game.npc.NPC;
import novite.rs.utility.Utils;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.JsonLoader;
import novite.rs.utility.game.npc.NPCSpawning;

import com.google.gson.reflect.TypeToken;

public class NPCAutoSpawn extends JsonLoader<NPCSpawning> {

	public static void main(String... args) throws IOException {
		Cache.init();
		JsonHandler.initialize();
		NPCAutoSpawn loader = JsonHandler.getJsonLoader(NPCAutoSpawn.class);

		List<NPCSpawning> spawns = loader.load();

		ListIterator<NPCSpawning> it = spawns.listIterator();
		while (it.hasNext()) {
			NPCSpawning spawn = it.next();
			String name = NPCDefinitions.getNPCDefinitions(spawn.getId()).getName();

			if (spawn.getId() >= Utils.getNPCDefinitionsSize()) {
				it.remove();
				System.out.println("Removed " + spawn.getId() + " from spawns!");
			}
			if (name.toLowerCase().contains("revenant") || name.toLowerCase().contains("glacor")) {
				it.remove();
				System.out.println("Removed " + name + " from spawns!");
			}
		}
		loader.save(spawns);
		System.out.println("Saved list");
	}

	@Override
	public void initialize() {
		List<NPCSpawning> spawns = load();
		for (NPCSpawning spawn : spawns) {
			List<NPCSpawning> regionSpawns = null;

			/* Populating the region spawns or generating a new one if it doesnt exist */
			if (map.get(spawn.getTile().getRegionId()) == null) {
				regionSpawns = new ArrayList<>();
			} else {
				regionSpawns = map.get(spawn.getTile().getRegionId());
			}

			regionSpawns.add(spawn);
			map.put(spawn.getTile().getRegionId(), regionSpawns);
		}
	}

	@Override
	public String getFileLocation() {
		return "./data/json/npcspawns.json";
	}

	@Override
	public List<NPCSpawning> load() {
		List<NPCSpawning> autospawns = null;
		String json = null;
		try {
			File file = new File(getFileLocation());
			if (!file.exists()) {
				return null;
			}
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			json = new String(chars);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		autospawns = gson.fromJson(json, new TypeToken<List<NPCSpawning>>() {
		}.getType());
		return autospawns;
	}

	public Direction getDirection(NPC npc) {
		List<NPCSpawning> spawns = getSpawns(npc.getRegionId());
		if (spawns == null) {
			return Direction.NORTH;
		}
		for (NPCSpawning npcSpawning : spawns) {
			if (npcSpawning.getId() == npc.getId() && npcSpawning.getX() == npc.getStartTile().getX() && npcSpawning.getY() == npc.getStartTile().getY() && npcSpawning.getZ() == npc.getStartTile().getPlane()) {
				return npcSpawning.getDirection();
			}
		}
		return Direction.NORTH;
	}

	public List<NPCSpawning> getSpawns(int regionId) {
		return map.get(regionId);
	}

	private final Map<Integer, List<NPCSpawning>> map = new HashMap<>();

	public enum Direction {

		NORTH(
		0),
		SOUTH(
		4),
		EAST(
		2),
		WEST(
		6);

		private int value;

		Direction(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static Direction getDirection(String text) {
			for (Direction d : Direction.values()) {
				if (d.name().equalsIgnoreCase(text)) {
					return d;
				}
			}
			return null;
		}
	}

}