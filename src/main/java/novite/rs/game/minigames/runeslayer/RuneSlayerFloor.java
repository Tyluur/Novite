package novite.rs.game.minigames.runeslayer;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import novite.rs.Constants;
import novite.rs.cache.loaders.NPCDefinitions;
import novite.rs.game.Region;
import novite.rs.game.RegionBuilder;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.item.FloorItem;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Dec 24, 2013
 */
public class RuneSlayerFloor {

	public RuneSlayerFloor(List<Player> players) {
		this.players = players;
		this.initialPlayers = players.size();
		this.boundChunks = RegionBuilder.findEmptyChunkBound(20, 20);
	}

	/**
	 * Passes everyone to the new floor.
	 *
	 * @param first
	 *            If the floor you're going on is the first floor (food room)
	 */
	public void passToFloor(boolean first) {
		if (first) {
			createSuppliesRoom();
		} else {
			long start = System.currentTimeMillis();
			synchronized (lock) {
				randomizeFloor();
				moveToNextRoom();
			}
			System.out.println("Created random floor in " + (System.currentTimeMillis() - start) + " ms.");
		}
	}

	/**
	 * Creates the room with all of the supplies.
	 */
	private void createSuppliesRoom() {
		setFoodRoomEntrance(Utils.currentTimeMillis());
		setInFoodRoom(true);

		int xr = 70;
		int yr = 600;
		RegionBuilder.copyAllPlanesMap(xr, yr, boundChunks[0], boundChunks[1], 20);
		WorldTile tile = new WorldTile(boundChunks[0] * 8 + 23, boundChunks[1] * 8 + 7, 0);

		// TODO add supplies such as bronze hatchet, pickaxe, and fly fishing
		// rod here

		for (Player p : players) {
			p.setNextWorldTile(tile);
		}
	}

	/**
	 * Removes the waiting room from the server memory
	 */
	public void removeWaitingRoom() {
		RegionBuilder.destroyMap(boundChunks[0], boundChunks[1], 8, 8);
	}

	/**
	 * Randomized the floor to go to
	 */
	private void randomizeFloor() {
		if (getRandomizedFloor() != null) {
			getRandomizedFloor().removeRegion();
		}
		setRandomizedFloor(new RandomizedFloor());
		randomizeWeakness();
		this.roomsLeft = Utils.random(3, 6);
	}

	/**
	 * Generates different monsters for the room everybody is in with a
	 * randomized algorithm. The tile of the monster cannot be on an object.
	 */
	private void generateMonsters() {
		for (int i = 0; i < getMonstersByFloor(); i++) {
			WorldTile tile = null;
			while (tile == null) {
				int x = getRandomCoordinate(currentRoom.getX());
				int y = getRandomCoordinate(currentRoom.getY());
				int z = 0;
				if (World.canMoveNPC(z, x, y, 1)) {
					tile = new WorldTile(x, y, z);
				}
			}
			monsters.add(new RuneSlayerNPC(RuneSlayerMonsters.getBestRandomId(getFloorsComplete(), false), tile, this, false));
		}
	}

	/**
	 * Creates a random coordinate based on the coordinate in the parameters by
	 * adding a random number to it.
	 *
	 * @param coord
	 *            The coordinate to base the calculation on
	 * @return
	 */
	private int getRandomCoordinate(int coord) {
		return coord + Utils.random(-3, 3);
	}

	/**
	 * Gets the number of monsters to kill based on the floor you are on.
	 *
	 * @return
	 */
	private int getMonstersByFloor() {
		return Math.abs((5 - getFloorsComplete()) + Utils.random(1, 5));
	}

	private void setCurrentRoom() {
		List<WorldTile> tiles = getFloor().getFloor().getFloorTiles();
		WorldTile randomRoom = tiles.get(Utils.random(tiles.size()));
		currentRoom = getFloor().getWorldTile(randomRoom.getX(), randomRoom.getY());
	}

	/**
	 * Spawns the monsters that belong in the room.
	 */
	private void spawnMonsters() {
		if (roomsLeft == 1) {
			createBossMonsters();
		} else {
			generateMonsters();
		}
		setMonstersGenerated(true);
		roomsLeft--;
		if (!isInFoodRoom()) {
			roomsComplete++;
		}
	}

	/**
	 * Moves the players into the next room full of monsters to kill
	 */
	private void moveToNextRoom() {
		if (roomsLeft > 0) {
			setCurrentRoom();
			randomizeWeakness();
			for (Player p : players) {
				clearGroundItems(p);
				p.setNextWorldTile(currentRoom);
			}
		} else {
			passToFloor(false);
			setFloorsComplete(getFloorsComplete() + 1);
		}
	}

	/**
	 * Create the bosses in the current room, only when you're on the last room.
	 * Gets the best location for the boss based on its size.
	 */
	private void createBossMonsters() {
		for (int i = 0; i < 1; i++) {
			WorldTile tile = null;
			int id = RuneSlayerMonsters.getBestRandomId(getFloorsComplete(), true);
			int count = 0;
			while (tile == null) {
				if (count++ >= 25) {
					tile = currentRoom;
					break;
				}
				int x = getRandomCoordinate(currentRoom.getX());
				int y = getRandomCoordinate(currentRoom.getY());
				int z = 0;
				if (World.canMoveNPC(z, x, y, NPCDefinitions.getNPCDefinitions(id).size)) {
					tile = new WorldTile(x, y, z);
				}
			}
			RuneSlayerNPC npc = new RuneSlayerNPC(id, tile, this, true);
			monsters.add(npc);
		}
	}

	/**
	 * Clearing the ground items in the current region.
	 *
	 * @param p
	 *            The player
	 */
	private void clearGroundItems(Player p) {
		Region r = World.getRegion(p.getRegionId());
		if (r != null) {
			if (r.getGroundItems() != null) {
				for (FloorItem item : r.getGroundItems()) {
					World.removeGroundItem(p, item, false);
				}
			}
		}
	}

	/**
	 * Sends the interface about the floors
	 * 
	 * @param i
	 */
	public void sendFloorInterface(Player player, int damage, int kills) {
		int interfaceId = 532;
		for (Player pl : players) {
			int resizableId = 10;
			int normalId = 8;
			boolean shouldAdd = !pl.getInterfaceManager().containsInterface(interfaceId);
			if (shouldAdd) {
				pl.getInterfaceManager().sendTab(player.getInterfaceManager().hasResizableScreen() ? resizableId : normalId, interfaceId);
			}
		}
		StringBuilder bldr = new StringBuilder();
		if (!isInFoodRoom()) {
			if (getMonsterSpawnDelay() <= 0) {
				bldr.append("Monster Weakness: " + getWeakness().name() + "<br>");
				bldr.append("Remaining: " + monsters.size() + "<br>");
				bldr.append("Floors Done: " + getFloorsComplete() + "<br>");
				bldr.append("Rooms Left: " + roomsLeft + "<br>");
				bldr.append("Damage/Kills: " + Utils.format(damage) + "/" + kills + "<br>");
				bldr.append("Partners: " + players.size() + "<br>");
			} else {
				bldr.append("Battle Countdown: " + getMonsterSpawnDelay());
			}
		} else {
			bldr.append("Preparation Time Left: " + getGatherTimeLeft());
		}
		player.getPackets().sendIComponentText(interfaceId, 0, "");
		player.getPackets().sendIComponentText(interfaceId, 1, bldr.toString());
	}

	/**
	 * Get the amount of time to gather food left.
	 *
	 * @return
	 */
	private long getGatherTimeLeft() {
		return (TimeUnit.MILLISECONDS.toSeconds((getFoodRoomEntrance() + TimeUnit.SECONDS.toMillis(Constants.isVPS ? 30 : 15)) - Utils.currentTimeMillis()));
	}

	/**
	 * Get the amount of time before monsters spawn up.
	 *
	 * @return
	 */
	private long getMonsterSpawnDelay() {
		return TimeUnit.MILLISECONDS.toSeconds((getMonsterRoomEntrance() + TimeUnit.SECONDS.toMillis(10)) - Utils.currentTimeMillis());
	}

	/**
	 * Removes an npc from the list of monsters
	 * 
	 * @param npc
	 */
	public void removeNPC(RuneSlayerNPC npc) {
		ListIterator<RuneSlayerNPC> it = monsters.listIterator();
		while (it.hasNext()) {
			RuneSlayerNPC slayerNPC = it.next();
			if (slayerNPC == npc) {
				it.remove();
			}
		}
	}

	public void checkMonstersChange() {
		if (isInFoodRoom() && getGatherTimeLeft() > 0) {
			return;
		}
		if (!isInFoodRoom() && getMonsterSpawnDelay() > 0) {
			return;
		}
		if (currentRoom != null && !isMonstersGenerated() && monsters.size() == 0) {
			spawnMonsters();
		}
		if (monsters.size() <= 0 && (isMonstersGenerated() || isInFoodRoom())) {
			setMonsterRoomEntrance(Utils.currentTimeMillis());
			moveToNextRoom();
			if (isInFoodRoom()) {
				setInFoodRoom(false);
				removeWaitingRoom();
			}
			setMonstersGenerated(false);
		}
	}

	public void randomizeRoom() {
		setFloor(new RandomizedFloor());
	}

	/**
	 * Sets the floor weakness to a random one in the {@link Weakness}
	 * enumeration.
	 */
	public void randomizeWeakness() {
		List<Weakness> weaknesses = new ArrayList<>();
		for (Weakness w : Weakness.values()) {
			weaknesses.add(w);
		}
		setWeakness(weaknesses.get(Utils.random(weaknesses.size())));
	}

	public Weakness getWeakness() {
		return weakness;
	}

	public void setWeakness(Weakness weakness) {
		this.weakness = weakness;
	}

	public RandomizedFloor getFloor() {
		return getRandomizedFloor();
	}

	public void setFloor(RandomizedFloor floor) {
		this.setRandomizedFloor(floor);
	}

	public RandomizedFloor getRandomizedFloor() {
		return randomizedFloor;
	}

	public void setRandomizedFloor(RandomizedFloor randomizedFloor) {
		this.randomizedFloor = randomizedFloor;
	}

	public int getFloorsComplete() {
		return floorsComplete;
	}

	public void setFloorsComplete(int floorsComplete) {
		this.floorsComplete = floorsComplete;
	}

	/**
	 * The current room people on the floor are in.
	 */
	private WorldTile currentRoom;

	/**
	 * The amount of floors that have been completed
	 */
	private int floorsComplete;

	/**
	 * The list of players in this game.
	 */
	private final List<Player> players;

	/**
	 * The amount of players that were in the floor at the beginning
	 */
	private final int initialPlayers;

	/**
	 * The lock which startup actions are synchronized with.
	 */
	private final Object lock = new Object();

	/**
	 * The list of monsters in the game.
	 */
	private final List<RuneSlayerNPC> monsters = new ArrayList<RuneSlayerNPC>(45);

	/**
	 * If you are in the food room.
	 */
	private boolean inFoodRoom;

	/**
	 * The time in ms people that people entered the food room
	 */
	private long foodRoomEntrance;

	/**
	 * The time in ms that people entered the monster room.
	 */
	private long monsterRoomEntrance;

	/**
	 * If the monsters have been generated
	 */
	private boolean monstersGenerated;

	/**
	 * The current floor we are on.
	 */
	private RandomizedFloor randomizedFloor;

	/**
	 * The current weakness of the room.
	 */
	private Weakness weakness;

	/**
	 * The rooms left on the floor
	 */
	private int roomsLeft;

	/**
	 * The amount of rooms that have been completed.
	 */
	private int roomsComplete;

	/**
	 * The waiting room bound chunks;
	 */
	private final int[] boundChunks;

	/**
	 * Generates the amount of points to give the player based on their progress
	 *
	 * @param damage
	 *            The damage done
	 * @param kills
	 *            The amount of kills
	 * @param rooms
	 *            The rooms complete
	 * @param players
	 *            The players started with
	 * @return
	 */
	public static double getPointsRewardAmount(int damage, int kills, int rooms, int players) {
		double exp = Math.floor(((damage / 20.5) * kills) / players);
		return exp;
	}

	public static void main(String... args) {
		int damageDealt = 20000;
		int kills = 20;
		int rooms = 2;
		int players = 5;
		System.out.println(getPointsRewardAmount(damageDealt, kills, rooms, players));
	}

	/**
	 * Gives the player the rewards for completing everything they did in the
	 * floor.
	 *
	 * @param player
	 *            The player to give rewards
	 * @param floor
	 *            The current floor the player is on
	 * @param damage
	 *            The damage the player has dealt in the game
	 */
	public void giveRewards(Player player, RuneSlayerFloor floor, int damage, int kills) {
		floor.players.remove(player);
		if (floor.players.size() <= 0) {
			if (floor.randomizedFloor != null) {
				floor.randomizedFloor.removeRegion();
			}
		}
		int initialPlayers = floor.initialPlayers;
		int rooms = floor.roomsComplete;

		double points = getPointsRewardAmount(damage, kills, rooms, initialPlayers);

		int interfaceId = 921;
		int lines = Utils.getInterfaceDefinitionsComponentsSize(interfaceId);

		/** Clearing up the interface for text */
		for (int i = 0; i < lines; i++) {
			player.getPackets().sendIComponentText(interfaceId, i, "");
		}

		/** Giving the rewards */
		player.getFacade().setRuneSlayerPoints((int) (player.getFacade().getRuneSlayerPoints() + points));

		/** Sending the interface text for info */
		player.getPackets().sendIComponentText(interfaceId, 44, "RuneSlayer Progress");

		/* Titles */
		player.getPackets().sendIComponentText(interfaceId, 24, "Progress");
		player.getPackets().sendIComponentText(interfaceId, 11, "Rewards");

		/* Left Row */
		player.getPackets().sendIComponentText(interfaceId, 25, "Initial Players");
		player.getPackets().sendIComponentText(interfaceId, 29, String.valueOf(initialPlayers));

		player.getPackets().sendIComponentText(interfaceId, 26, "Rooms Complete");
		player.getPackets().sendIComponentText(interfaceId, 30, String.valueOf(roomsComplete));

		player.getPackets().sendIComponentText(interfaceId, 27, "Damage Done");
		player.getPackets().sendIComponentText(interfaceId, 31, String.valueOf(damage));

		player.getPackets().sendIComponentText(interfaceId, 28, "Monsters Killed");
		player.getPackets().sendIComponentText(interfaceId, 32, String.valueOf(kills));

		/* Right Row */
		player.getPackets().sendIComponentText(interfaceId, 17, "Tokens Given");
		player.getPackets().sendIComponentText(interfaceId, 14, String.valueOf(points));

		player.getInterfaceManager().sendInterface(interfaceId);
	}

	public boolean isInFoodRoom() {
		return inFoodRoom;
	}

	public void setInFoodRoom(boolean inFoodRoom) {
		this.inFoodRoom = inFoodRoom;
	}

	public long getFoodRoomEntrance() {
		return foodRoomEntrance;
	}

	public void setFoodRoomEntrance(long foodRoomEntrance) {
		this.foodRoomEntrance = foodRoomEntrance;
	}

	public long getMonsterRoomEntrance() {
		return monsterRoomEntrance;
	}

	public void setMonsterRoomEntrance(long monsterRoomEntrance) {
		this.monsterRoomEntrance = monsterRoomEntrance;
	}

	public boolean isMonstersGenerated() {
		return monstersGenerated;
	}

	public void setMonstersGenerated(boolean monstersGenerated) {
		this.monstersGenerated = monstersGenerated;
	}

}