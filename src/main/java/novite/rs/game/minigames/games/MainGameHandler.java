package novite.rs.game.minigames.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import novite.Main;
import novite.rs.Constants;
import novite.rs.api.input.IntegerInputAction;
import novite.rs.engine.CoresManager;
import novite.rs.game.World;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.dialogues.SimpleMessage;
import novite.rs.game.player.dialogues.SimpleNPCMessage;
import novite.rs.utility.Utils;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 13, 2014
 */
public class MainGameHandler implements Runnable {

	public static void main(String... args) {
		System.out.println(pointsToReward(100, (int) 24014.5));
	}

	/**
	 * 
	 * Rewards the player for their ingame activity
	 * 
	 * @param skillPoints
	 *            The player's skill points
	 * @param damageDealt
	 *            The damage the player dealt
	 */
	private static int pointsToReward(double skillPoints, int damageDealt) {
		int combatRewards = (damageDealt / 10);
		double total = skillPoints + combatRewards;
		double rewards = total / 1.25;
		return (int) Math.ceil(rewards);
	}

	@Override
	public void run() {
		try {
			/** We have to wait for the server to setup first */
			if (Main.STARTUP_TIME == -1)
				return;
			switch (getPhase()) {
			case GAME_NOT_STARTED:
				secondsPassed++;
				long startMinutes = getTimeTillStart();
				if (secondsPassed % 60 == 0 && startMinutes != 0) {
					if (lastPublicizedMessage == -1 || TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - lastPublicizedMessage) == 1) {
						if (startMinutes == 5 || startMinutes <= 3) {
							get().sendWorldMessage("Novite Games will be starting in " + startMinutes + " minutes! Head to Novite Games to join!");
							lastPublicizedMessage = System.currentTimeMillis();
						}
					}
				}
				if (lobbyPlayers.size() >= 3 && getSecondsTillStart() > 10) {
					secondsPassed += 30;
				}
				if (!Constants.isVPS) {
					secondsPassed += 300;
				}
				if (getSecondsTillStart() <= 0) {
					if (!Constants.isVPS || lobbyPlayers.size() >= 3) {
						get().sendWorldMessage("Novite Games have started, you can still join by teleporting to the Novite Games!");
						startGame();
						return;
					} else {
						secondsPassed = 0;
					}
				}
				break;
			case COLLECTING_MATERIALS:
				if (lobbyPlayers.size() > 0) {
					ListIterator<Player> it = lobbyPlayers.listIterator();
					while (it.hasNext()) {
						Player player = it.next();
						player.getControllerManager().startController("GamesHandler");
						it.remove();
					}
				}
				if (getCollectTimeLeft() <= 0) {
					setPhase(Phases.GAME_RUNNING);
				}
				break;
			case GAME_RUNNING:
				synchronized (lock) {
					if (getGameTimeLeft() <= 0) {
						finishGame(false);
					}
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Finishes the game and prepares a new game to be started.
	 */
	private void finishGame(boolean won) {
		List<Object> messages = new ArrayList<Object>();
		messages.add(6731);
		if (won) {
			messages.add("Congratulations, you have won the novite games.");
			messages.add("Stay tuned for the next time you can participate!");
		} else {
			messages.add("You did not win the novite games, try again next time...");
		}
		synchronized (lock) {
			Iterator<Player> it = gamePlayers.listIterator();
			while (it.hasNext()) {
				Player player = it.next();
				if (player.getControllerManager().getController() instanceof GamesHandler) {
					GamesHandler handler = (GamesHandler) player.getControllerManager().getController();
					int points = pointsToReward(handler.getSkillPoints(), handler.getDamageDealt());
					if (won) {
						points += 150;
					}
					messages.add("You receive " + Utils.format(points) + " novite game points.");
					player.getFacade().setNoviteGamePoints(player.getFacade().getNoviteGamePoints() + points);
					player.sendMessage("You receive " + Utils.format(points) + " novite game points.");
					
					player.getDialogueManager().startDialogue(SimpleNPCMessage.class, messages.toArray(new Object[messages.size()]));
					((GamesHandler) player.getControllerManager().getController()).leaveGame(false);
					player.getControllerManager().removeControlerWithoutCheck();
					it.remove();
				}
			}
			clearMonsters();
			secondsPassed = 0;
			setPhase(Phases.GAME_NOT_STARTED);
		}
	}

	private void clearMonsters() {
		Iterator<NoviteGamesNPC> it$ = this.monstersToHunt.iterator();
		while(it$.hasNext()) {
			NoviteGamesNPC npc = it$.next();
			npc.finish();
			it$.remove();
		}
	}

	/**
	 * Spawning the helpers
	 */
	private void spawnHelpers() {
		/** Spawning objects */
		World.spawnObject(new WorldObject(55357, 10, 3, new WorldTile(3803, 3527, 0))); // chest
		World.spawnObject(new WorldObject(14308, 10, 1, new WorldTile(3811, 3526, 0))); // tree
		World.spawnObject(new WorldObject(1307, 10, 1, new WorldTile(3814, 3528, 0))); // maple-tree
		World.spawnObject(new WorldObject(139, 10, 1, new WorldTile(3794, 3527, 0))); // willow-tree
		World.spawnObject(new WorldObject(1309, 10, 1, new WorldTile(3808, 3528, 0))); // yew-tree
		World.spawnObject(new WorldObject(1306, 10, 1, new WorldTile(3799, 3529, 0))); // magic-tree
		/** Runecrafting */
		World.spawnObject(new WorldObject(2478, 10, 1, new WorldTile(3816, 3545, 0))); // air-altar
		World.spawnObject(new WorldObject(2482, 10, 1, new WorldTile(3812, 3545, 0))); // fire-altar
		World.spawnObject(new WorldObject(2480, 10, 1, new WorldTile(3795, 3548, 0))); // water-altar
		World.spawnObject(new WorldObject(2481, 10, 1, new WorldTile(3786, 3547, 0))); // earth-altar
		/** Mining */
		World.spawnObject(new WorldObject(2092, 10, 1, new WorldTile(3802, 3554, 0))); // iron
		World.spawnObject(new WorldObject(2102, 10, 1, new WorldTile(3803, 3554, 0))); // mith
		World.spawnObject(new WorldObject(2104, 10, 1, new WorldTile(3804, 3554, 0))); // addy
		World.spawnObject(new WorldObject(14859, 10, 1, new WorldTile(3805, 3554, 0))); // rune
		World.spawnObject(new WorldObject(61332, 10, 1, new WorldTile(3799, 3553, 0))); // anvil
		/** Spawning npcs */
		if (!spawnedNPCs) {
			World.spawnNPC(312, new WorldTile(3822, 3527, 0), -1, true); // cage
			World.spawnNPC(329, new WorldTile(3823, 3530, 0), -1, true); // fly
			World.spawnNPC(328, new WorldTile(3820, 3526, 0), -1, true); // harpoon
			spawnedNPCs = true;
		}
		// /** Spawning objects */
		// World.spawnObject(new WorldObject(61332, 10, 1, new WorldTile(2584,
		// 3531, 0))); // anvil
		// World.spawnObject(new WorldObject(55357, 10, 1, new WorldTile(2583,
		// 3525, 0))); // chest
		// World.spawnObject(new WorldObject(14859, 10, 1, new WorldTile(2581,
		// 3531, 0))); // rune-ore
		// World.spawnObject(new WorldObject(1307, 10, 1, new WorldTile(2576,
		// 3539, 0))); // maple-tree
		// World.spawnObject(new WorldObject(1307, 10, 1, new WorldTile(2590,
		// 3537, 0))); // maple-tree
		// World.spawnObject(new WorldObject(1307, 10, 1, new WorldTile(2595,
		// 3538, 0))); // maple-tree
		// if (!spawnedNpcs) {
		// /** Spawning npcs */
		// World.spawnNPC(6267, new WorldTile(2580, 3541, 0), -1, true); //
		// lobsters
		// World.spawnNPC(952, new WorldTile(2585, 3541, 0), -1, true); //
		// monkfish
		// World.spawnNPC(313, new WorldTile(2589, 3542, 0), -1, true); // shark
		// spawnedNpcs = true;
		// }
	}

	/**
	 * Starts the main game handler tick
	 */
	public void startUp() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(this, 1, 1, TimeUnit.SECONDS);
	}

	/**
	 * Teleports everyone into the game, sets their controler, and prepares the
	 * chest + all world objects required
	 */
	private void startGame() {
		try {
			spawnHelpers();
			gamePlayers.clear();
			ListIterator<Player> it = lobbyPlayers.listIterator();
			synchronized (lock) {
				while (it.hasNext()) {
					Player player = it.next();

					player.getControllerManager().forceStop();
					player.getControllerManager().startController("GamesHandler");

					it.remove();
				}
			}
			chestItems = new ArrayList<>();
			for (int i = 0; i < CHEST_CONTAINER.length; i++) {
				chestItems.add(new Item(CHEST_CONTAINER[i]));
			}
			collectingMaterialsEnd = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
			setPhase(Phases.COLLECTING_MATERIALS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enters the game lobby for the player
	 * 
	 * @param player
	 *            The player entering the lobby
	 */
	public void enterLobby(Player player) {
		if (player.getEquipment().wearingArmour()) {
			player.getDialogueManager().startDialogue(SimpleMessage.class, "You cannot equip anything when you are joining the game.");
			return;
		}
		if (player.getInventory().getFreeSlots() != 28) {
			player.getDialogueManager().startDialogue(SimpleMessage.class, "You cannot bring anything in your inventory.");
			return;
		}
		if (player.getFamiliar() != null) {
			player.getDialogueManager().startDialogue(SimpleMessage.class, "You cannot have a familiar with you.");
			return;
		}
		player.getControllerManager().startController("GamesLobby");
	}

	/**
	 * Removes the player from the game lobby
	 * 
	 * @param player
	 */
	public void removeLobby(Player player) {
		if (player.getControllerManager().getController() instanceof GamesLobby) {
			GamesLobby controler = (GamesLobby) player.getControllerManager().getController();
			controler.leaveLobby(false);
		}
	}

	/**
	 * Sends a public message to everybody online
	 * 
	 * @param message
	 *            The message to send
	 */
	public void sendWorldMessage(String message) {
		World.sendWorldMessage(message, false, false);
		// System.out.println(message);
	}

	/**
	 * @return the instance
	 */
	public static MainGameHandler get() {
		return INSTANCE;
	}

	/**
	 * @return the lobbyPlayers
	 */
	public List<Player> getLobbyPlayers() {
		return lobbyPlayers;
	}

	/**
	 * @return the phase
	 */
	public Phases getPhase() {
		return phase;
	}

	/**
	 * @param phase
	 *            the phase to set
	 */
	public void setPhase(Phases phase) {
		this.phase = phase;
		if (phase == Phases.GAME_RUNNING) {
			playersStartedWith = gamePlayers.size();
			startHuntingPhase();
		}
	}

	/**
	 * Starts the combat phase
	 */
	private void startHuntingPhase() {
		sendWorldMessage("Novite Games are officially running! Good luck to all contestants!");
		gameTimeEnd = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(GAME_TIME_PERIOD);
		double toSpawn = getAmountToSpawn(playersStartedWith);
		for (int i = 0; i < toSpawn; i++) {
			Integer[] index = spawns[Utils.random(spawns.length)];
			WorldTile tile = new WorldTile(index[0], index[1], 0);
			int id = ids[Utils.random(ids.length)];
			monstersToHunt.add(new NoviteGamesNPC(id, tile));
		}
	}

	/**
	 * Gets the amount of monsters to spawn into the game based on the amount of
	 * players the game was started with
	 * 
	 * @param start
	 *            The amount of players
	 * @return
	 */
	private static double getAmountToSpawn(int start) {
		if (!Constants.isVPS)
			return 10;
		return Math.ceil((double) (start * 3) + (double) ((double) start / (double) 2));
	}

	/**
	 * Displays the chest to the player
	 * 
	 * @param player
	 *            The player
	 */
	public void displayChest(final Player player) {
		player.getAttributes().put("novite_games_chest", true);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				player.getAttributes().remove("novite_games_chest");
			}
		});
		player.getInterfaceManager().sendInterface(762);
		player.getPackets().sendConfigByFile(4893, 1);

		// removing the equipment stats button
		player.getPackets().sendHideIComponent(762, 117, true);
		player.getPackets().sendHideIComponent(762, 118, true);
		// unlock bank inter all options
		player.getPackets().sendIComponentSettings(762, 93, 0, 516, 2622718);
		// unlock bank inv all options
		player.getPackets().sendIComponentSettings(763, 0, 0, 27, 2425982);

		player.getPackets().sendItems(95, chestItems.toArray(new Item[chestItems.size()]));
	}

	/**
	 * Withdraws an item from the chest
	 * 
	 * @param player
	 *            The player
	 * @param slotId
	 *            The item slot
	 * @param packetId
	 *            The packet id
	 */
	public void withdrawItemChest(final Player player, int slotId, int packetId) {
		final Item item = chestItems.get(slotId);
		if (item == null)
			return;
		int amt = -1;
		switch (packetId) {
		case 61:
			amt = 1;
			break;
		case 64:
			amt = 5;
			break;
		case 4:
			amt = 10;
			break;
		case 52:
			amt = player.getBank().getLastX();
			break;
		case 81:
			player.getPackets().sendInputIntegerScript("Enter Amount:", new IntegerInputAction() {

				@Override
				public void handle(int input) {
					if (input > 100) {
						player.sendMessage("You can only withdraw 100 at a time.");
						return;
					}
					player.getInventory().addItem(item.getId(), input);
				}
			});
			break;
		case 91:
		case 18:
			player.sendMessage("You cannot withdraw that many.");
			break;
		}
		if (amt > 100) {
			player.sendMessage("You can only withdraw 100 at a time.");
			return;
		}
		if (!player.getInterfaceManager().containsInterface(762))
			return;
		if (amt != -1) {
			player.getInventory().addItem(item.getId(), amt);
		}
	}

	/**
	 * Gets the amount of time until the game starts
	 * 
	 * @return
	 */
	public long getTimeTillStart() {
		long minutesPassed = TimeUnit.SECONDS.toMinutes(secondsPassed);
		long startMinutes = GAME_START_TIME - minutesPassed;
		return startMinutes;
	}

	/**
	 * Gets the seconds until the game starts
	 * 
	 * @return
	 */
	public long getSecondsTillStart() {
		return TimeUnit.MINUTES.toSeconds(GAME_START_TIME) - secondsPassed;
	}

	/**
	 * @return the gamePlayers
	 */
	public List<Player> getGamePlayers() {
		return gamePlayers;
	}

	/**
	 * Gets the time that is left for collecting materials in seconds
	 * 
	 * @return
	 */
	public long getCollectTimeLeft() {
		return TimeUnit.MILLISECONDS.toSeconds(collectingMaterialsEnd - System.currentTimeMillis());
	}

	/**
	 * Gets the time that is left in the game in seconds
	 * 
	 * @return
	 */
	public long getGameTimeLeft() {
		return TimeUnit.MILLISECONDS.toSeconds(gameTimeEnd - System.currentTimeMillis());
	}

	/**
	 * Removes a player from the list of players
	 * 
	 * @param player
	 *            The player to remove
	 */
	public void removePlayer(Player player) {
		synchronized (lock) {
			ListIterator<Player> it = gamePlayers.listIterator();
			while (it.hasNext()) {
				Player p = it.next();
				if (p.getUsername().equals(player.getUsername())) {
					it.remove();
					break;
				}
			}
		}
	}

	/**
	 * Remove the monster from the list of {@link #monstersToHunt}
	 * 
	 * @param npc
	 */
	public void removeMonster(NoviteGamesNPC npc) {
		Iterator<NoviteGamesNPC> it$ = monstersToHunt.iterator();
		while (it$.hasNext()) {
			NoviteGamesNPC n = it$.next();
			if (n.getIndex() == npc.getIndex()) {
				it$.remove();
				break;
			}
		}
		if (monstersToHunt.size() == 0) {
			finishGame(true);
		}
	}

	public List<NoviteGamesNPC> getMonstersToHunt() {
		return monstersToHunt;
	}

	/**
	 * The amount of minutes that have passed
	 */
	private int secondsPassed = 0;

	/**
	 * The time the last publicized message was sent
	 */
	private long lastPublicizedMessage = -1;

	/**
	 * The current phase of the game
	 */
	private Phases phase = Phases.GAME_NOT_STARTED;

	/**
	 * The players in the lobby
	 */
	private final List<Player> lobbyPlayers = Collections.synchronizedList(new ArrayList<Player>());

	/**
	 * The players in the game
	 */
	private final List<Player> gamePlayers = Collections.synchronizedList(new ArrayList<Player>());

	/**
	 * The list of monsters to hunt left
	 */
	private final List<NoviteGamesNPC> monstersToHunt = Collections.synchronizedList(new ArrayList<NoviteGamesNPC>());

	/**
	 * The amount of players the game started with
	 */
	private int playersStartedWith = -1;

	/**
	 * The lock object
	 */
	private final Object lock = new Object();

	/**
	 * If the npcs have been spawned
	 */
	private boolean spawnedNPCs = false;

	/**
	 * The time that players will no longer be able to collect materials
	 */
	private long collectingMaterialsEnd = -1;

	/**
	 * The time that the game will be over in
	 */
	private long gameTimeEnd = -1;

	/**
	 * The amount of minutes that the game starts in.
	 */
	private static final long GAME_START_TIME = 10;

	/**
	 * The amount of time a game takes to finish
	 */
	private static final long GAME_TIME_PERIOD = 10;

	/**
	 * The instance of this class
	 */
	private static final MainGameHandler INSTANCE = new MainGameHandler();

	/**
	 * The tile for the center of the novite games
	 */
	public static final WorldTile NOVITE_GAMES_AREA = new WorldTile(3818, 3528, 0);

	private List<Item> chestItems = null;

	/**
	 * The array of items that will be in the chest
	 */
	private static final int[] CHEST_CONTAINER = new int[] {
			// TODO add all items
			/** Combat suppies */
			1731, 1725, 1712, 3105,
			/** Fishing supplies */
			301, 309, 307, 311, 313, 314,
			/** Woodcutting supplies */
			946, 590, 1349, 1355, 1359,
			/** Mining * Smithing supplies */
			2347, 1267, 1273, 1275,
			/** Arrowtips */
			1777, 40, 42, 44,
			/** Magic Supplies */
			6563, 7936, 558, 562, 560, 565, 566
	/**/
	};

	private static final Integer[] ids = new Integer[] { 1648, 10797, 110, 2685, 90, 1633, 2025, 688, 13661, 2028, 1615, 52, 55 };

	private static final Integer[][] spawns = new Integer[][] { { 3812, 3556 }, { 3803, 3551 }, { 3787, 3556 }, { 3820, 3529 } };

	public enum Phases {
		GAME_NOT_STARTED, COLLECTING_MATERIALS, GAME_RUNNING
	}

}
