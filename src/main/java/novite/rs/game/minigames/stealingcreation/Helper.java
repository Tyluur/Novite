package novite.rs.game.minigames.stealingcreation;

import java.util.List;

import novite.rs.game.ForceMovement;
import novite.rs.game.World;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.player.Equipment;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.utility.Utils;

/**
 * @author mgi125, the almighty
 */
public class Helper {

	public static final int[] CLASS_ITEMS_BASE = { 14132, 14122, 14142, 14152, 14172, 14162, 14367, 14357, 14347, 14411, 14401, 14391, 14337, 14327, 14317, 14297, 14287, 14307, 14192, 14202, 12850, 12851, 14422, 14377, 14421, -1, -1, 14215, 14225, 14235, 14245, 14255, 14265, 14275, 14285 };
	public static final int[] SACRED_CLAY = { 14182, 14184, 14186, 14188, 14190 };
	public static final long DURATION = 20 * 60 * 1000;
	public static final long PENALTY_DURATION = 10 * 60 * 1000;
	public static final WorldTile EXIT = new WorldTile(2968, 9710, 0);
	public static final int[] BASE_ENTRANCE_BLUE = new int[] { 4, 5 };
	public static final int[] BASE_ENTRANCE_RED = new int[] { 4, 2 };
	public static final int[][] PLOT_ENTRANCES = new int[][] { { 2, 2 }, { 2, 3 }, { 2, 4 }, { 3, 2 }, { 3, 4 }, { 4, 2 }, { 4, 4 }, { 5, 2 }, { 5, 3 }, { 5, 4 } };
	public static final int[] PLOT_OBJECT_BASE = new int[] { 3, 3 };
	public static final int KILN = 39546;
	public static final int KILN_CONFIG_BASE = 583;
	public static final int KILN_SCRIPT_BASE = 1910;
	public static final int KILN_CLAY_COMP_BASE = 98;
	public static final int BLUE_DOOR_1 = 39766;
	public static final int BLUE_DOOR_2 = 39768;
	public static final int RED_DOOR_1 = 39767;
	public static final int RED_DOOR_2 = 39769;
	public static final int[] BLUE_DOOR_P1 = new int[] { 3, 7 };
	public static final int[] BLUE_DOOR_P2 = new int[] { 4, 7 };
	public static final int[] BLUE_DOOR_P3 = new int[] { 7, 4 };
	public static final int[] BLUE_DOOR_P4 = new int[] { 7, 3 };
	public static final int[] RED_DOOR_P1 = new int[] { 4, 0 };
	public static final int[] RED_DOOR_P2 = new int[] { 3, 0 };
	public static final int[] RED_DOOR_P3 = new int[] { 0, 3 };
	public static final int[] RED_DOOR_P4 = new int[] { 0, 4 };
	public static final int[] MANAGER_NPCS = new int[] { 8234, 8235, 8236, 8238 };
	public static final int[] BLUE_MANAGER_P = new int[] { 4, 2 };
	public static final int[] RED_MANAGER_P = new int[] { 3, 5 };
	public static final int[] BARRIER_MIN = new int[] { 1, 1 };
	public static final int[] BARRIER_MAX = new int[] { 6, 6 };
	public static final int[] BARRIER_ITEMS = new int[] { 14172, 14174, 14176, 14178, 14180 };
	public static final int EMPTY_BARRIER1 = 39615;
	public static final int EMPTY_BARRIER2 = 39616;
	public static final int EMPTY_BARRIER3 = 39617;
	public static final int[][] BLUE_BARRIER_WALLS = new int[][] { { 39770, 39772, 39774, 39776 }, { 39782, 39784, 39786, 39788 }, { 39794, 39796, 39798, 39800 }, { 39806, 39808, 39810, 39812 }, { 39818, 39820, 39822, 39824 } };
	public static final int[][] RED_BARRIER_WALLS = new int[][] { { 39771, 39773, 39775, 39777 }, { 39783, 39785, 39787, 39789 }, { 39795, 39797, 39799, 39801 }, { 39807, 39809, 39811, 39813 }, { 39819, 39821, 39823, 39825 } };
	public static final int[][] BLUE_BARRIER_GATES = new int[][] { { 39778, 39780 }, { 3990, 39792 }, { 39802, 39804 }, { 39814, 39816 }, { 39826, 39828 } };
	public static final int[][] RED_BARRIER_GATES = new int[][] { { 39779, 39781 }, { 3991, 39793 }, { 39803, 39805 }, { 39815, 39817 }, { 39827, 39829 } };
	public static final int[][] ROCK_SPOTS = new int[][] { { 39548, 39548, 39549 }, { 39550, 39554, 39558 }, { 39551, 39555, 39559 }, { 39552, 39556, 39560 }, { 39553, 39557, 39561 } };
	public static final int[][] POOL_SPOTS = new int[][] { { 39548, 39548, 39549 }, { 39565, 39569, 39573 }, { 39564, 39568, 39572 }, { 39563, 39567, 39571 }, { 39562, 39566, 39570 } };
	public static final int[][] SWARM_SPOTS = new int[][] { { 39548, 39548, 39549 }, { 39577, 39581, 39585 }, { 39576, 39580, 39584 }, { 39575, 39579, 39583 }, { 39574, 39578, 39582 } };
	public static final int[][] TREE_SPOTS = new int[][] { { 39548, 39548, 39549 }, { 39589, 39593, 39597 }, { 39588, 39592, 39596 }, { 39587, 39591, 39595 }, { 39586, 39590, 39594 } };
	public static final int[] TOOL_TIERS = new int[] { 20, 51, 65, 101, 151 };
	public static final int[] OBJECT_TIERS = new int[] { 10, 90, 125, 175, 241 };
	public static final int BLUE_CAPE = 14387;
	public static final int RED_CAPE = 14389;
	public static final int PRAYER_ALTAR = 39547;

	public static void sendHome(Player player) {
		reset(player);
		player.useStairs(-1, Helper.EXIT, 0, 1);
		player.setCanPvp(false);
		player.setForceMultiArea(false);
		player.getAppearence().setHidden(false);
		player.sendDefaultPlayersOptions();
		player.getInterfaceManager().removeOverlay(false);
		player.getControllerManager().removeControlerWithoutCheck();
	}

	public static void reset(Player player) {
		player.stopAll();
		player.getEquipment().reset();
		player.getInventory().reset();
		if (player.getFamiliar() != null) {
			player.getFamiliar().sendDeath(player);
		}
		player.setFamiliar(null);
		player.getAppearence().transformIntoNPC(-1);
		player.getAppearence().setHidden(false);
		player.getAppearence().generateAppearenceData();
		player.reset();
	}

	public static void spawn(StealingCreationGame game, Player player, boolean team) {
		int size = game.getArea().getSize();
		int[] base = findNearestBase(game.getArea(), player, team);
		int[] entrance;
		if ((base[0] == 0 && base[1] == 0) || (base[0] == (size - 1) && base[1] == (size - 1))) {
			entrance = team ? BASE_ENTRANCE_RED : BASE_ENTRANCE_BLUE;
		} else {
			entrance = PLOT_ENTRANCES[Utils.random(PLOT_ENTRANCES.length)];
		}
		WorldTile tile = new WorldTile(game.getArea().getMinX() + (base[0] * 8) + entrance[0], game.getArea().getMinY() + (base[1] * 8) + entrance[1], 0);
		// System.err.println("x:" + tile.getX() + ", y:" + tile.getY() + ", z:"
		// + tile.getPlane());
		player.reset();
		player.setCanPvp(true);
		player.getPackets().sendPlayerOption("Follow", 2, false);
		player.getPackets().sendPlayerOption("Give to", 4, false);
		player.getPackets().sendPlayerOption("Pickpocket", 5, false);
		player.setForceMultiArea(true);
		player.useStairs(-1, tile, 0, 1); // player will unlock itself 600ms
		// after teleing
		player.getMusicsManager().playMusic(578);
	}

	public static int[] findNearestBase(GameArea area, Player player, boolean team) {
		int[] base = new int[2];
		double dist = Double.MAX_VALUE;
		int pX = player.getChunkX() - (area.getMinX() >> 3);
		int pY = player.getChunkY() - (area.getMinY() >> 3);
		for (int size = area.getSize(), x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (area.getWallTeam(x, y) == (team ? 2 : 1)) {
					int xDelta = pX - x;
					int yDelta = pY - y;
					double d = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
					if (d < dist) {
						dist = d;
						base[0] = x;
						base[1] = y;
					}
				}
			}
		}
		return base;
	}

	public static boolean withinSafeArea(Player player, GameArea area, boolean team) {
		WorldTile tile = getNearestRespawnPoint(player, area, team);
		int flagX = tile.getChunkX() - (area.getMinX() >> 3);
		int flagY = tile.getChunkY() - (area.getMinY() >> 3);
		if (area.getType(flagX, flagY) == 1) {
			if (team) {
				if (Helper.withinArea(player, area, flagX, flagY, new int[] { 2, 2 }, 3)) {
					return true;
				}
			} else if (Helper.withinArea(player, area, flagX, flagY, new int[] { 1, 1 }, 5)) {
				return true;
			}
		} else if (Helper.withinArea(player, area, flagX, flagY, new int[] { 2, 2 })) {
			return true;
		}
		return false;
	}

	public static WorldTile getNearestRespawnPoint(Player player, GameArea area, boolean team) {
		List<WorldObject> o = World.getRegion(player.getRegionId()).getSpawnedObjects();
		if (o != null) {
			for (int[] gateIDS : (team ? Helper.RED_BARRIER_GATES : Helper.BLUE_BARRIER_GATES)) {
				for (int id : gateIDS) {
					for (WorldObject object : o) {
						if (object == null || object.getId() != id) {
							continue;
						}
						return object;
					}
				}
			}
		}
		int size = area.getSize();
		int[] base = findNearestBase(area, player, team);
		int[] entrance;
		if ((base[0] == 0 && base[1] == 0) || (base[0] == (size - 1) && base[1] == (size - 1))) {
			entrance = team ? BASE_ENTRANCE_RED : BASE_ENTRANCE_BLUE;
		} else {
			entrance = PLOT_ENTRANCES[Utils.random(PLOT_ENTRANCES.length)];
		}
		WorldTile tile = new WorldTile(area.getMinX() + (base[0] * 8) + entrance[0], area.getMinY() + (base[1] * 8) + entrance[1], 0);
		return tile;
	}

	public static int getItemIndex(String name) {
		if (name.contains("rune") || name.contains("staff")) {
			return 7;
		} else if (name.contains("potion")) {
			return 9;
		} else if (name.contains("food")) {
			return 10;
		} else if (name.contains("scroll") || name.contains("pouch")) {
			return 11;
		} else if (name.contains("barrier")) {
			return 1;
		} else if (name.contains("hatchet") || name.contains("pickaxe") || name.contains("butterfly net") || name.contains("harpoon")) {
			return 2;
		} else if (name.contains("robe") || name.contains("hat")) {
			return 8;
		} else if (name.contains("leather body") || name.contains("coif") || name.contains("chaps")) {
			return 6;
		} else if (name.contains("dagger") || name.contains("warhammer") || name.contains("scimitar")) {
			return 3;
		} else if (name.contains("helm") || name.contains("plate")) {
			return 4;
		} else if (name.contains("bow") || name.contains("arrow")) {
			return 5;
		} else if (name.contains("clay")) {
			return 0;
		}
		return -1;
	}

	public static int getRequestedKilnSkill(int indexedId) {
		if (indexedId >= 0 && indexedId <= 1 || indexedId >= 6 && indexedId <= 8 || indexedId >= 15 && indexedId <= 17) {
			return Skills.SMITHING;
		} else if (indexedId >= 2 && indexedId <= 3 || indexedId >= 9 && indexedId <= 14 || indexedId >= 18 && indexedId <= 19 || indexedId == 23) {
			return Skills.CRAFTING;
		} else if (indexedId == 4) {
			return Skills.FARMING;// Skills.CONSTRUCTION;
		} else if (indexedId == 5) {
			return Skills.COOKING;
		} else if (indexedId >= 20 && indexedId <= 21) {
			return Skills.RUNECRAFTING;
		} else if (indexedId >= 22 && indexedId <= 24) {
			return Skills.SUMMONING;
		} else if (indexedId >= 25 && indexedId <= 32) {
			return Skills.HERBLORE;
		}
		return 1;
	}

	public static boolean checkSkillRequriments(Player player, int requestedSkill, int index) {
		int level = getLevelForIndex(index);
		if (player.getSkills().getLevel(requestedSkill) < level) {
			player.getPackets().sendGameMessage("You dont have the requried " + Skills.SKILL_NAME[requestedSkill] + " level for that quality of clay.");
			return false;
		}
		return true;
	}

	private static int getLevelForIndex(int index) {
		int level = 0;
		for (int i = 0; i < index; i++) {
			if (i == index) {
				return level;
			}
			level += 20;
		}
		return level;
	}

	public static boolean proccessKilnItems(Player player, int componentId, int index, int itemId, int amount) {
		int clayId = SACRED_CLAY[index];
		if (amount - player.getInventory().getFreeSlots() > 0) {
			amount = amount - player.getInventory().getFreeSlots();
		}
		if (amount > 0 && player.getInventory().containsItem(clayId, amount)) {
			player.getInventory().deleteItem(itemId, amount);
			player.getInventory().addItem(new Item(CLASS_ITEMS_BASE[componentId - 37] + ((componentId == 57 || componentId == 58 || componentId == 61) ? 0 : componentId == 56 ? index : componentId >= 64 ? (-index * 2) : (index * 2)), (componentId >= 56 && componentId <= 58 ? 15 * (index + 1) : componentId == 61 ? index + 1 : 1) * amount));
			return true;
		} else {
			player.getPackets().sendGameMessage("You have no clay to proccess.");
			return false;
		}
	}

	public static void giveCape(Player player, boolean team) {
		player.getEquipment().getItems().set(Equipment.SLOT_CAPE, new Item(team ? RED_CAPE : BLUE_CAPE));
		player.getEquipment().refresh(Equipment.SLOT_CAPE);
		player.getAppearence().generateAppearenceData();
	}

	public static boolean withinArea(Player player, GameArea area, int flagX, int flagY, int[] range, int distance) {
		int minX = area.getMinX() + (flagX << 3) + Helper.BARRIER_MIN[0] + range[0];
		int minY = area.getMinY() + (flagY << 3) + Helper.BARRIER_MIN[1] + range[1];
		return player.withinDistance(new WorldTile(minX, minY, player.getPlane()), distance);
	}

	public static boolean withinArea(Player player, GameArea area, int flagX, int flagY, int[] range) {
		return withinArea(player, area, flagX, flagY, range, 2);
	}

	public static boolean withinArea2(Player player, GameArea area, int flagX, int flagY, int[] range) {
		int minX = area.getMinX() + (flagX << 3) + range[0];
		int minY = area.getMinY() + (flagY << 3) + range[1];
		int maxX = area.getMinX() + (flagX << 3) + range[2];
		int maxY = area.getMinY() + (flagY << 3) + range[3];
		if (player.withinDistance(new WorldTile(minX, minY, player.getPlane()), 3)) {
			return player.inArea(minX, minY, maxX, maxY) || player.getX() >= minX && player.getX() <= maxX && player.getY() >= minY && player.getY() <= maxY;
		}
		return false;
	}

	public static boolean setWalkToGate(WorldObject gate, Player player) {
		if (player.getX() == gate.getX() && player.getY() == gate.getY()) {
			return true;
		}

		if (gate.getRotation() == 0) {
			if (player.getX() == (gate.getX() - 1) && player.getY() == gate.getY()) {
				return true;
			} else {
				return player.addWalkSteps(gate.getX(), gate.getY()) || player.addWalkSteps(gate.getX() - 1, gate.getY());
			}
		} else if (gate.getRotation() == 1) {
			if (player.getX() == gate.getX() && player.getY() == (gate.getY() + 1)) {
				return true;
			} else {
				return player.addWalkSteps(gate.getX(), gate.getY()) || player.addWalkSteps(gate.getX(), gate.getY() + 1);
			}
		} else if (gate.getRotation() == 2) {
			if (player.getX() == (gate.getX() + 1) && player.getY() == gate.getY()) {
				return true;
			} else {
				return player.addWalkSteps(gate.getX(), gate.getY()) || player.addWalkSteps(gate.getX() + 1, gate.getY());
			}
		} else if (gate.getRotation() == 3) {
			if (player.getX() == gate.getX() && player.getY() == (gate.getY() - 1)) {
				return true;
			} else {
				return player.addWalkSteps(gate.getX(), gate.getY()) || player.addWalkSteps(gate.getX(), gate.getY() - 1);
			}
		} else {
			return false;
		}
	}

	public static boolean isAtGate(WorldObject gate, Player player) {
		if (player.getX() == gate.getX() && player.getY() == gate.getY()) {
			return true;
		}
		if (gate.getRotation() == 0) {
			return player.getX() == (gate.getX() - 1) && player.getY() == gate.getY();
		} else if (gate.getRotation() == 1) {
			return player.getX() == gate.getX() && player.getY() == (gate.getY() + 1);
		} else if (gate.getRotation() == 2) {
			return player.getX() == (gate.getX() + 1) && player.getY() == gate.getY();
		} else if (gate.getRotation() == 3) {
			return player.getX() == gate.getX() && player.getY() == (gate.getY() - 1);
		} else {
			return false;
		}
	}

	public static WorldTile getFaceTile(WorldObject gate, Player player) {
		if (player.getX() != gate.getX() || player.getY() != gate.getY()) {
			return new WorldTile(gate.getX(), gate.getY(), gate.getPlane());
		}

		if (gate.getRotation() == 0) {
			return new WorldTile(gate.getX() - 1, gate.getY(), gate.getPlane());
		} else if (gate.getRotation() == 1) {
			return new WorldTile(gate.getX(), gate.getY() + 1, gate.getPlane());
		} else if (gate.getRotation() == 2) {
			return new WorldTile(gate.getX() + 1, gate.getY(), gate.getPlane());
		} else if (gate.getRotation() == 3) {
			return new WorldTile(gate.getX(), gate.getY() - 1, gate.getPlane());
		} else {
			return null;
		}
	}

	public static int getFaceDirection(WorldTile faceTile, Player player) {
		if (player.getX() < faceTile.getX()) {
			return ForceMovement.EAST;
		} else if (player.getX() > faceTile.getX()) {
			return ForceMovement.WEST;
		} else if (player.getY() < faceTile.getY()) {
			return ForceMovement.NORTH;
		} else if (player.getY() > faceTile.getY()) {
			return ForceMovement.SOUTH;
		} else {
			return 0;
		}
	}

	public static void displayClayStatus(GameArea area, Player player) {
		player.getInterfaceManager().sendInterface(806);
		int basic = 0;
		int[] fishing = new int[4];
		int[] mining = new int[4];
		int[] woodcuting = new int[4];
		int[] hunter = new int[4];
		int size = area.getSize();
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				int type = area.getType(x, y);
				int tier = area.getTier(x, y);
				int degradation = area.getDegradation(x, y);
				if (degradation > 0 && type >= 9 && type <= 12 && tier > 0 && tier < 6) {
					if (tier == 1) {
						basic++;
					} else if (type == 9) {
						mining[tier - 2]++;
					} else if (type == 10) {
						woodcuting[tier - 2]++;
					} else if (type == 11) {
						fishing[tier - 2]++;
					} else if (type == 12) {
						hunter[tier - 2]++;
					}
				}
			}
		}
		player.getPackets().sendGlobalConfig(564, basic);
		for (int i = 0; i < fishing.length; i++) {
			player.getPackets().sendGlobalConfig(565 + i, fishing[i]);
		}
		for (int i = 0; i < mining.length; i++) {
			player.getPackets().sendGlobalConfig(569 + i, mining[i]);
		}
		for (int i = 0; i < woodcuting.length; i++) {
			player.getPackets().sendGlobalConfig(573 + i, woodcuting[i]);
		}
		for (int i = 0; i < hunter.length; i++) {
			player.getPackets().sendGlobalConfig(577 + i, hunter[i]);
		}
	}

	public static int calculateReward(Score personal, int winner) {
		int total = personal.total((personal.getTeam() ? 2 : 1) == winner);
		int reward = 0;
		if (total < 20000) {
			reward = total / 1000;
		} else if (total < 22000) {
			reward = 20;
		} else if (total < 26000) {
			reward = 21;
		} else if (total < 34000) {
			reward = 22;
		} else if (total < 50000) {
			reward = 23;
		} else if (total < 82000) {
			reward = 24;
		} else if (total < 146000) {
			reward = 25;
		} else {
			reward = 26;
		}
		return reward;
	}

	public static void awardPoints(Player player, Score personal, int winner) {
		int points = calculateReward(personal, winner);
		player.setCompletedStealingCreation();
		player.increaseStealingCreationPoints(points);
		player.getPackets().sendGameMessage("You earn: " + points + " reward point" + (points != 1 ? "s" : "") + ".");
	}

	public static void displayScores(Player player, boolean redTeam, Score personal, List<Score> total, int winner) {
		player.getInterfaceManager().sendInterface(810);
		int blueTotal = Score.totalXP(total, false, winner == 1);
		int redTotal = Score.totalXP(total, true, winner == 2);
		player.getPackets().sendGlobalConfig(588, personal.getTeam() ? 2 : 1);
		player.getPackets().sendGlobalConfig(589, winner);
		player.getPackets().sendGlobalConfig(598, redTeam ? redTotal : blueTotal); // your team
		player.getPackets().sendGlobalConfig(597, redTeam ? blueTotal : redTotal); // enemy team

		// personal
		player.getPackets().sendGlobalConfig(590, personal.getGathering());
		player.getPackets().sendGlobalConfig(591, personal.getProcessing());
		player.getPackets().sendGlobalConfig(592, (personal.getDepositing() - personal.getWithdrawing()) * 2);
		player.getPackets().sendGlobalConfig(593, personal.getDamaging());
		player.getPackets().sendGlobalConfig(594, personal.getKilled());
		player.getPackets().sendGlobalConfig(595, personal.getDied());
		player.getPackets().sendGlobalConfig(596, personal.total((personal.getTeam() ? 2 : 1) == winner));
		// --------

		// awards
		Score mostProcessed = Score.mostProcessed(total);
		Score mostGathered = Score.mostGathered(total);
		Score mostDeposited = Score.mostDeposited(total);
		Score mostDamage = Score.mostDamaged(total);
		Score mostKills = Score.mostKills(total);
		Score mostDeaths = Score.mostDeaths(total);
		Score highestP = Score.highestTotal(total, winner);
		Score lowestP = Score.lowestTotal(total, winner);
		if (mostProcessed == null || mostGathered == null || mostDeposited == null || mostDamage == null || mostKills == null || mostDeaths == null || highestP == null || lowestP == null) {
			return; // client is prepared for this, and will show required
			// messages.
		}

		player.getPackets().sendGlobalString(45, mostGathered.getName());
		player.getPackets().sendGlobalConfig(608, mostGathered.getTeam() ? 2 : 1);
		player.getPackets().sendGlobalConfig(600, mostGathered.getGathering());

		player.getPackets().sendGlobalString(44, mostProcessed.getName());
		player.getPackets().sendGlobalConfig(607, mostProcessed.getTeam() ? 2 : 1);
		player.getPackets().sendGlobalConfig(599, mostProcessed.getProcessing());

		player.getPackets().sendGlobalString(49, mostDeposited.getName());
		player.getPackets().sendGlobalConfig(612, mostDeposited.getTeam() ? 2 : 1);
		player.getPackets().sendGlobalConfig(604, (mostDeposited.getDepositing() - mostDeposited.getWithdrawing()) * 2);

		player.getPackets().sendGlobalString(48, mostDamage.getName());
		player.getPackets().sendGlobalConfig(611, mostDamage.getTeam() ? 2 : 1);
		player.getPackets().sendGlobalConfig(603, mostDamage.getDamaging());

		player.getPackets().sendGlobalString(46, mostKills.getName());
		player.getPackets().sendGlobalConfig(609, mostKills.getTeam() ? 2 : 1);
		player.getPackets().sendGlobalConfig(601, mostKills.getKilled());

		player.getPackets().sendGlobalString(47, mostDeaths.getName());
		player.getPackets().sendGlobalConfig(610, mostDeaths.getTeam() ? 2 : 1);
		player.getPackets().sendGlobalConfig(602, mostDeaths.getDied());

		player.getPackets().sendGlobalString(50, highestP.getName());
		player.getPackets().sendGlobalConfig(613, highestP.getTeam() ? 2 : 1);
		player.getPackets().sendGlobalConfig(605, highestP.total((highestP.getTeam() ? 2 : 1) == winner));

		player.getPackets().sendGlobalString(51, lowestP.getName());
		player.getPackets().sendGlobalConfig(614, lowestP.getTeam() ? 2 : 1);
		player.getPackets().sendGlobalConfig(606, lowestP.total((lowestP.getTeam() ? 2 : 1) == winner));
		// --------

		player.getPackets().sendGameMessage("Team 1's score(blue team): " + blueTotal + " - Team 2's score(red team): " + redTotal);
		if (winner != 0 && winner == (personal.getTeam() ? 2 : 1)) {
			player.getPackets().sendGameMessage("Your team, Team " + (personal.getTeam() ? 2 : 1) + ", is victorious! You gain a 10% bonus to your score!");
		} else if (winner != 0 && winner != (personal.getTeam() ? 2 : 1)) {
			player.getPackets().sendGameMessage("Your team, Team " + (personal.getTeam() ? 2 : 1) + ", is defeated! Team " + (personal.getTeam() ? 1 : 2) + " is victorious!");
		} else if (winner == 0) {
			player.getPackets().sendGameMessage("Your team, Team " + (personal.getTeam() ? 2 : 1) + ", ties with Team " + (personal.getTeam() ? 1 : 2) + "!"); // TODO
			// get
			// original
			// rs
			// message
			// for
			// tie
		}
		player.getPackets().sendGameMessage("YOUR TOTAL SCORE: " + personal.total((personal.getTeam() ? 2 : 1) == winner) + " points.");

	}

	public static void displayKiln(final Player player) {
		if (!player.getInventory().containsOneItem(SACRED_CLAY)) {
			player.getPackets().sendGameMessage("You try using the processing point, but quickly realize that you have no sacred clay with you.");
			return;
		}
		player.getInterfaceManager().sendInterface(813);
		player.getPackets().sendRunScript(KILN_SCRIPT_BASE + 0); // + tabID
		int quality = 0;
		qualityLoop:
		for (int i = 4; i >= 0; i--) {
			if (player.getInventory().containsOneItem(SACRED_CLAY[i])) {
				quality = i;
				break qualityLoop;
			}
		}
		player.getTemporaryAttributtes().put("sc_kiln_quality", quality);
		player.getTemporaryAttributtes().put("in_kiln", Utils.currentTimeMillis() + 10000);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				player.getTemporaryAttributtes().remove("in_kiln");
			}
		});
		refreshKiln(player);
	}

	public static void refreshKiln(Player player) {
		int quality = player.getTemporaryAttributtes().get("sc_kiln_quality") != null ? (int) player.getTemporaryAttributtes().get("sc_kiln_quality") : 4;
		for (int i = 0; i < 5; i++) {
			player.getPackets().sendGlobalConfig(KILN_CONFIG_BASE + i, player.getInventory().getNumberOf(SACRED_CLAY[i]));
			player.getPackets().sendHideIComponent(813, Helper.KILN_CLAY_COMP_BASE + (i * 2), quality != i);
		}
	}
}
