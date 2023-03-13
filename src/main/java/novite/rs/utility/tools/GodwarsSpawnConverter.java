package novite.rs.utility.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import novite.rs.cache.Cache;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.NPCAutoSpawn;
import novite.rs.utility.game.json.impl.NPCAutoSpawn.Direction;
import novite.rs.utility.game.npc.NPCSpawning;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 30, 2014
 */
public class GodwarsSpawnConverter {

	public static void main(String... args) {
		try {
			Cache.init();
			List<String> lines = (ArrayList<String>) Files.readAllLines(new File(System.getProperty("user.home") + "/Desktop/gwd.txt").toPath(), Charset.defaultCharset());
			JsonHandler.initialize();

			NPCAutoSpawn autospawn = JsonHandler.getJsonLoader(NPCAutoSpawn.class);
			List<NPCSpawning> spawns = autospawn.load();
			
			ListIterator<NPCSpawning> it = spawns.listIterator();
			while(it.hasNext()) {
				NPCSpawning entry = it.next();
				if (isGodwars(entry.getId())) {
					it.remove();
				}
			}
			
			for (String line : lines) {
				String[] split = line.split(" ");
				int id = Integer.parseInt(split[0]);
				int x = Integer.parseInt(split[1]);
				int y = Integer.parseInt(split[2]);
				int z = Integer.parseInt(split[3]);
				spawns.add(new NPCSpawning(x, y, z, id, Direction.NORTH));
			}
			autospawn.save(spawns);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean isGodwars(int id) {
		return id == 6260 || id == 6261 || id == 6263 || id == 6265 || id == 6222 || id == 6223 || id == 6225 || id == 6227 || id == 6081 || id == 6203 || id == 6204 || id == 6206 || id == 6208 || id == 6248 || id == 6250 || id == 6252 || id == 6247 || id >= 6210 && id <= 6221 || id >= 6254 && id <= 6259 || id >= 6268 && id <= 6283 || id >= 6228 && id <= 6246;
	}

}
