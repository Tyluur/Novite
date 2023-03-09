package novite.rs.game.minigames.stealingcreation;

import java.util.Arrays;

import novite.rs.game.RegionBuilder;
import novite.rs.game.World;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.utility.Utils;

/**
 * @author mgi125, the almighty
 */
public class GameArea {

	public static int[] NONE = new int[] { -1, -1 };
	public static int[] BASE = new int[] { 240, 712 };
	public static int[] EMPTY = new int[] { 241, 715 };
	public static int[] RESERVED_1 = new int[] { 240, 713 };
	public static int[] RESERVED_2 = new int[] { 241, 712 };
	public static int[] RESERVED_3 = new int[] { 241, 713 };
	public static int[] KILN = new int[] { 240, 714 };
	public static int[] ALTAR = new int[] { 241, 714 };
	public static int[] FOG = new int[] { 240, 715 };
	public static int[] RIFT = new int[] { 240, 716 };
	public static int[] WALL = new int[] { 241, 716 };
	public static int[] ROCK = new int[] { 242, 716 };

	public static int[] SKILL_ROCK = new int[] { 247, 715 };
	public static int[] SKILL_POOL = new int[] { 247, 714 };
	public static int[] SKILL_SWARM = new int[] { 247, 713 };
	public static int[] SKILL_TREE = new int[] { 247, 712 };

	/**
	 * Contains area flags. 0-3 bits - type 4-7 bits - tier (if any) 8-9 bits -
	 * rotation 10-18 bits - degradation 19-20 bits - wall team 21-23 bits -
	 * wall tier 24-29 bits - wall status Types: 0 - reserved, don't use 1 -
	 * base 2 - empty 3 - rift 4 - wall 5 - fog 6 - large rock 7 - altar 8 -
	 * kiln 9 - rock 10 - tree 11 - pool 12 - swarm
	 */
	private int[][] flags;
	/**
	 * Contains base positions.
	 */
	private int[] base;

	public GameArea(int size) {
		flags = new int[size][size];
	}

	/**
	 * Calculate's new random area.
	 */
	public void calculate() {
		for (int[] flag : flags) {
			Arrays.fill(flag, 2); // fill with empty area
		}

		set(0, 0, 1, 0, 0); // blue base
		set(0, 1, 0, 0, 0); // reserved space for blue base
		set(1, 0, 0, 1, 0); // reserved space for blue base
		set(1, 1, 0, 2, 0); // reserved space for blue base

		set(flags.length - 1, flags.length - 1, 1, 0, 2); // red base
		set(flags.length - 1, flags.length - 2, 0, 0, 2); // reserved space for
		// red base
		set(flags.length - 2, flags.length - 1, 0, 1, 2); // reserved space for
		// red base
		set(flags.length - 2, flags.length - 2, 0, 2, 2); // reserved space for
		// red base

		setWallTeam(0, 0, 1); // flag blue base to blue team
		setWallTeam(flags.length - 1, flags.length - 1, 2); // flag red base to
		// red team

		int total = flags.length * flags.length;
		int skillPlots = (int) (total * 0.3F);
		int obstacles = (int) (total * 0.2F);

		while (skillPlots-- > 0) {
			setRandom(100, 0, 0, flags.length, flags.length, Utils.random(4) + 9, skillPlots == 0 ? 5 : Utils.random(5), Utils.random(4), 60);
		}

		while (obstacles-- > 0) {
			int type = Utils.random(5) + 3;
			int rotation = type == 5 ? 3 : Utils.random(4);
			if (!setRandom(100, 0, 0, flags.length, flags.length, type, 0, rotation)) {
				System.out.println("Failed");
			}
		}

		// ensure that at least one kiln per team is created.
		int kilnsBlue = Utils.random(2) + 1;
		int kilnsRed = Utils.random(2) + 1;

		while (kilnsBlue-- > 0) {
			setRandom(100, 0, 0, flags.length / 2, flags.length / 2, 8, 0, Utils.random(4));
		}

		while (kilnsRed-- > 0) {
			setRandom(100, flags.length / 2, flags.length / 2, flags.length, flags.length, 8, 0, Utils.random(4));
		}
	}

	/**
	 * Create's dynamic maps using the info from the calculate() method.
	 */
	public void create() {
		if (base != null) {
			throw new RuntimeException("Area already created.");
		}
		int b = RegionBuilder.findEmptyRegionHash(flags.length, flags.length);
		base = new int[] { (b >> 8) << 6, (b & 0xFF) << 6 };
		// System.err.println("Base:" + base[0] + "," + base[1] + "," + b);
		for (int x = 0; x < flags.length; x++) {
			for (int y = 0; y < flags.length; y++) {
				int chunkX = (base[0] >> 3) + x;
				int chunkY = (base[1] >> 3) + y;
				int type = getType(x, y);
				int rot = getRotation(x, y);
				int tier = getTier(x, y);
				int[] copy;
				switch (type) {
					case 0: // base pad space
						if (tier == 0) {
							copy = RESERVED_1;
						} else if (tier == 1) {
							copy = RESERVED_2;
						} else if (tier == 2) {
							copy = RESERVED_3;
						} else {
							copy = EMPTY;
						}
						break;
					case 1:
						copy = BASE;
						break;
					case 2:
						copy = EMPTY;
						break;
					case 3:
						copy = RIFT;
						break;
					case 4:
						copy = WALL;
						break;
					case 5:
						copy = FOG;
						break;
					case 6:
						copy = ROCK;
						break;
					case 7:
						copy = ALTAR;
						break;
					case 8:
						copy = KILN;
						break;
					case 9:
						copy = SKILL_ROCK;
						break;
					case 10:
						copy = SKILL_TREE;
						break;
					case 11:
						copy = SKILL_POOL;
						break;
					case 12:
						copy = SKILL_SWARM;
						break;
					default:
						copy = EMPTY;
						break;
				}

				if (type >= 9 && type <= 12 && tier > 0) {
					int[] r = new int[2];
					r[0] = copy[0] - tier;
					r[1] = copy[1];
					copy = r;
				} else if (type >= 9 && type <= 12 && tier <= 0) {
					copy = EMPTY;
				}
				RegionBuilder.copyChunk(copy[0], copy[1], 0, chunkX, chunkY, 0, rot);
			}
		}

		World.spawnObject(new WorldObject(Helper.BLUE_DOOR_1, 0, 1, getMinX() + Helper.BLUE_DOOR_P1[0], getMinY() + Helper.BLUE_DOOR_P1[1], 0));
		World.spawnObject(new WorldObject(Helper.BLUE_DOOR_2, 0, 1, getMinX() + Helper.BLUE_DOOR_P2[0], getMinY() + Helper.BLUE_DOOR_P2[1], 0));
		World.spawnObject(new WorldObject(Helper.BLUE_DOOR_1, 0, 2, getMinX() + Helper.BLUE_DOOR_P3[0], getMinY() + Helper.BLUE_DOOR_P3[1], 0));
		World.spawnObject(new WorldObject(Helper.BLUE_DOOR_2, 0, 2, getMinX() + Helper.BLUE_DOOR_P4[0], getMinY() + Helper.BLUE_DOOR_P4[1], 0));

		World.spawnObject(new WorldObject(Helper.RED_DOOR_1, 0, 3, getMinX() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P1[0], getMinY() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P1[1], 0));
		World.spawnObject(new WorldObject(Helper.RED_DOOR_2, 0, 3, getMinX() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P2[0], getMinY() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P2[1], 0));
		World.spawnObject(new WorldObject(Helper.RED_DOOR_1, 0, 0, getMinX() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P3[0], getMinY() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P3[1], 0));
		World.spawnObject(new WorldObject(Helper.RED_DOOR_2, 0, 0, getMinX() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P4[0], getMinY() + ((flags.length - 1) * 8) + Helper.RED_DOOR_P4[1], 0));

		int managerBlue = Helper.MANAGER_NPCS[Utils.random(Helper.MANAGER_NPCS.length)];
		int managerRed = Helper.MANAGER_NPCS[Utils.random(Helper.MANAGER_NPCS.length)];

		World.spawnNPC(managerBlue, new WorldTile(getMinX() + Helper.BLUE_MANAGER_P[0], getMinY() + Helper.BLUE_MANAGER_P[1], 0), -1, false, true).setWalkType(0);
		World.spawnNPC(managerRed, new WorldTile(getMinX() + ((flags.length - 1) * 8) + Helper.RED_MANAGER_P[0], getMinY() + ((flags.length - 1) * 8) + Helper.RED_MANAGER_P[1], 0), -1, false, true).setWalkType(0);
	}

	/**
	 * Destroy's dynamic maps that were created using create() method.
	 */
	public void destroy() {
		if (base == null) {
			throw new RuntimeException("Area already destroyed.");
		}
		RegionBuilder.destroyMap(base[0] >> 3, base[1] >> 3, flags.length, flags.length);
		base = null;

	}

	private boolean setRandom(int attempts, int minX, int minY, int maxX, int maxY, int type, int tier, int rotation) {
		return setRandom(attempts, minX, minY, maxX, maxY, type, tier, rotation, 0);
	}

	private boolean setRandom(int attempts, int minX, int minY, int maxX, int maxY, int type, int tier, int rotation, int degradation) {
		while (attempts-- > 1) {
			int x = minX + Utils.random(maxX - minX);
			int y = minY + Utils.random(maxY - minY);
			if (getType(x, y) == 2) {
				set(x, y, type, tier, rotation);
				setDegradation(x, y, degradation);
				return true;
			}
		}
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				if (getType(x, y) == 2) {
					set(x, y, type, tier, rotation);
					setDegradation(x, y, degradation);
					return true;
				}
			}
		}
		return false;
	}

	public void set(int x, int y, int type, int tier, int rotation) {
		flags[x][y] = (type | (tier << 4) | (rotation << 8));
	}

	public void setDegradation(int x, int y, int deg) {
		flags[x][y] &= ~(0x1FF << 10);
		flags[x][y] |= deg << 10;
	}

	public void setWallTeam(int x, int y, int team) {
		flags[x][y] &= ~(0x3 << 19);
		flags[x][y] |= team << 19;
	}

	public void setWallTier(int x, int y, int tier) {
		flags[x][y] &= ~(0x7 << 21);
		flags[x][y] |= tier << 21;
	}

	public void setWallStatus(int x, int y, int status) {
		flags[x][y] &= ~(0x3F << 24);
		flags[x][y] |= status << 24;
	}

	public int getType(int x, int y) {
		return flags[x][y] & 0xF;
	}

	public int getTier(int x, int y) {
		return (flags[x][y]) >> 4 & 0xF;
	}

	public int getRotation(int x, int y) {
		return (flags[x][y] >> 8) & 0x3;
	}

	public int getDegradation(int x, int y) {
		return (flags[x][y] >> 10) & 0x1FF;
	}

	public int getWallTeam(int x, int y) {
		return (flags[x][y] >> 19) & 0x3;
	}

	public int getWallTier(int x, int y) {
		return (flags[x][y] >> 21) & 0x7;
	}

	public int getWallStatus(int x, int y) {
		return (flags[x][y] >> 24) & 0x3F;
	}

	public int getSize() {
		return flags.length;
	}

	public int getMinX() {
		return base[0];
	}

	public int getMinY() {
		return base[1];
	}

	public int getMaxX() {
		return base[0] + (flags.length << 3);
	}

	public int getMaxY() {
		return base[1] + (flags.length << 3);
	}

	public int[][] getFlags() {
		return flags;
	}
}