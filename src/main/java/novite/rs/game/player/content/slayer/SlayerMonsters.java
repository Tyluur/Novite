package novite.rs.game.player.content.slayer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import novite.rs.cache.loaders.NPCDefinitions;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jun 20, 2014
 */
public enum SlayerMonsters {

	CRAWLING_HAND(new int[] { 1648 }, 5),

	CAVE_BUG(new int[] { 1832 }, 7),

	CAVE_CRAWLER(new int[] { 7787 }, 10),

	BANSHEE(new int[] { 1612 }, 15),

	CAVE_SLIM(new int[] { 1831 }, 17),

	ROCK_SLUG(new int[] { 1631 }, 20),

	DESERT_LIZARD(new int[] { 2804 }, 22),

	COCKATRICE(new int[] { 1620 }, 25),

	PYREFIED(new int[] { 1633 }, 30),

	MORGE(new int[] { 114 }, 32),

	HARPIE_BUG_SWARM(new int[] { 3163 }, 33),

	WALL_BEAST(new int[] { 7823 }, 35),

	KILLERWATT(new int[] { 3201 }, 37),

	MOLANISK(new int[] { 5751 }, 39),

	BASILISK(new int[] { 1616 }, 40),

	TERROR_DOG(new int[] { 5417 }, 40),

	FEVER_SPIDER(new int[] { 2850 }, 42),

	INFERNAL_MAGE(new int[] { 1643 }, 45),

	BRINE_RAT(new int[] { 3707 }, 47),

	BLOODVELD(new int[] { 1618 }, 50),

	PHOENIX(new int[] { 8549 }, 51),

	JELLY(new int[] { 1637 }, 52),

	TUROTH(new int[] { 1622 }, 55),

	WARPED_TERRORBIRD(new int[] { 6285 }, 56),

	WARPED_TORTOISE(new int[] { 6296 }, 56),

	ZYGOMITE(new int[] { 3346 }, 57),

	CAVE_HORROR(new int[] { 4353 }, 58),

	WILD_JADE_FINE(new int[] { 3409 }, 59),

	ABERRANT_SPECTRE(new int[] { 1604 }, 60),

	SPIRITUAL_RANGE(new int[] { 6220 }, 63),

	GANODERMIC_BEAST(new int[] { 14696, 14697 }, 95),

	DUST_DEVIL(new int[] { 1624 }, 65),

	SPIRITUAL_WARRIOR(new int[] { 6219 }, 68),

	KURASK(new int[] { 1608 }, 70),

	SKELETAL_WYVERN(new int[] { 3068 }, 72),
	
	ICE_STRYKEWYRMS1(new int[] { 9466 }, 73),

	GARGOYLE(new int[] { 1610 }, 75),
	
	ICE_STRYKEWYRMS2(new int[] { 9464 }, 77),

	AQUANITE(new int[] { 9172 }, 78),

	NECHRYAEL(new int[] { 1613 }, 80),

	SPIRITUAL_MAGE(new int[] { 6221, 6231, 6257, 6278 }, 83),

	ABYSSAL_DEMON(new int[] { 1615 }, 85),

	DARK_BEAST(new int[] { 2783 }, 90),

	MUTATED_JADINKO_BABY(new int[] { 13729, 13820 }, 80),

	MUTATED_JADINKO_GUARD(new int[] { 13821 }, 86),

	MUTATED_JADINKO_MALE(new int[] { 13822 }, 91),
	
	ICE_STRYKEWYRMS(new int[] { 9462 }, 93);

	private static Map<Integer, SlayerMonsters> monsters = new HashMap<Integer, SlayerMonsters>();
	private static Map<Integer, String> names = new HashMap<>();

	public static SlayerMonsters forId(int id) {
		return monsters.get(id);
	}
	
	public static int forName(String param) {
		Iterator<Entry<Integer, String>> it = names.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Integer, String> entry = it.next();
			int id = entry.getKey();
			String name = entry.getValue();
			if (name.equalsIgnoreCase(param))
				return id;
		}
		return -1;
	}

	static {
		for (SlayerMonsters monster : SlayerMonsters.values()) {
			for (int id : monster.getId()) {
				monsters.put(id, monster);
				names.put(id, NPCDefinitions.getNPCDefinitions(id).getName());
			}
		}
	}

	private int id[];
	private int req;

	private SlayerMonsters(int[] id, int req) {
		this.id = id;
		this.req = req;
	}

	public int[] getId() {
		return id;
	}

	public int getRequirement() {
		return req;
	}

}