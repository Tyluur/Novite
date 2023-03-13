package novite.rs.game.minigames.akrisae;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import novite.rs.Constants;
import novite.rs.engine.CoresManager;
import novite.rs.game.Animation;
import novite.rs.game.Entity;
import novite.rs.game.RegionBuilder;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.player.dialogues.SimpleNPCMessage;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.Utils;
import novite.rs.utility.game.TeleportLocations;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 25, 2014
 */
public class AkrisaeController extends Controller {

	@Override
	public void start() {
		createRegion();
		sendTab();
		player.setForceMultiArea(true);
		if (Constants.isVPS)
			player.getPackets().sendBlackOut(2);
	}

	@Override
	public boolean login() {
		leaveGame(false);
		return true;
	}

	@Override
	public boolean logout() {
		leaveGame(true);
		return true;
	}

	@Override
	public void forceClose() {
		addPoints();
		player.getPackets().sendBlackOut(0);
		removeRegion();
		removeControler();
	}

	/**
	 * The algorithm to add the amount of points to the player. This is based on
	 * the amount of kills and waves completed
	 */
	private void addPoints() {
		player.getFacade().setAkrisaePoints(player.getFacade().getAkrisaePoints() + AkrisaeFormulae.getPointsToAdd(waves, killCount));
	}

	@Override
	public void process() {
		if (!regionMade || player.isLocked())
			return;
		sendTabText();
		if (getBrothers().size() == 0) {
			spawnBrothers();
			player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 8338, "Incoming wave of " + brothers.size() + " brothers!", "Good luck!");
		}
	}

	@Override
	public boolean sendDeath() {
		player.lock();
		player.stopAll();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage("Oh dear, you have died!");
				} else if (loop == 4) {
					this.stop();

					leaveGame(false);
					player.setNextAnimation(new Animation(-1));
					player.unlock();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	/**
	 * Handles the player leaving the game
	 *
	 * @param logout
	 *            Whether they left by logout or by choice
	 */
	public void leaveGame(boolean logout) {
		removeNPCs();
		if (logout) {
			player.setLocation(TeleportLocations.BARROWS);
		} else {
			player.reset();
			player.getPackets().closeInterface(player.getInterfaceManager().hasResizableScreen() ? 10 : 8);
			player.setNextWorldTile(TeleportLocations.BARROWS);
		}
		forceClose();
	}

	/**
	 * Sends the information over the interface
	 */
	private void sendTabText() {
		int interfaceId = 256;
		player.getPackets().sendIComponentText(interfaceId, 5, "Akrisae's Brothers");
		player.getPackets().sendIComponentText(interfaceId, 11, "Akrisae Points:");
		player.getPackets().sendIComponentText(interfaceId, 12, "Brothers To Kill:");
		player.getPackets().sendIComponentText(interfaceId, 13, "Brothers Killed:");
		player.getPackets().sendIComponentText(interfaceId, 14, "Current Wave:");
		player.getPackets().sendIComponentText(interfaceId, 6, "           " + Utils.format(player.getFacade().getAkrisaePoints()));
		player.getPackets().sendIComponentText(interfaceId, 7, "           " + Utils.format(getBrothers().size()));
		player.getPackets().sendIComponentText(interfaceId, 8, "           " + Utils.format(killCount));
		player.getPackets().sendIComponentText(interfaceId, 9, "           " + Utils.format(waves));
	}

	/**
	 * Sends the tab interface and clears the text on it
	 */
	public void sendTab() {
		int interfaceId = 256;
		int resizableId = 10;
		int normalId = 8;
		boolean shouldAdd = !player.getInterfaceManager().containsInterface(interfaceId);
		if (shouldAdd) {
			int length = Utils.getInterfaceDefinitionsComponentsSize(interfaceId);
			for (int i = 0; i < length; i++)
				player.getPackets().sendIComponentText(interfaceId, i, "");
			player.getInterfaceManager().sendTab(player.getInterfaceManager().hasResizableScreen() ? resizableId : normalId, interfaceId);
		}
	}

	/**
	 * Moves the player to coordinates in this dynamic region
	 * 
	 * @param x
	 *            The x
	 * @param y
	 *            The y
	 */
	public void moveEntity(Entity entity, int x, int y) {
		entity.setNextWorldTile(getWorldTile(x, y));
	}

	/**
	 * Removes the npcs
	 */
	private void removeNPCs() {
		ListIterator<AkrisaeBrother> it$ = brothers.listIterator();
		while (it$.hasNext()) {
			AkrisaeBrother brother = it$.next();
			brother.finish();
			it$.remove();
		}
	}

	/**
	 * Creates the dynamic region
	 */
	private void createRegion() {
		player.lock(); // locks player
		CoresManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				regionChunks = RegionBuilder.findEmptyChunkBound(8, 8);
				RegionBuilder.copyAllPlanesMap(444, 409, regionChunks[0], regionChunks[1], 4);
				moveEntity(player, START_X, START_Y);
				createHillSpawns();
				regionMade = true;
				player.unlock();
			}
		});
	}

	/**
	 * Spawns the brothers and makes them fight the player
	 */
	public void spawnBrothers() {
		if (player.getRegion().getNPCsIndexes() != null)
			for (int index : player.getRegion().getNPCsIndexes()) {
				NPC n = World.getNPCs().get(index);
				if (n == null || !(n.getId() >= 2025 && n.getId() <= 2030)) {
					continue;
				}
				World.removeNPC(n);
			}
		if (hillspawns.size() == 0) {
			createHillSpawns();
		}
		waves++;
		Collections.shuffle(hillspawns);
		ListIterator<WorldTile> it$ = hillspawns.listIterator();
		int spawnCount = getSpawnAmount();
		while (it$.hasNext()) {
			WorldTile spawnTile = it$.next();
			if (spawnCount - 1 >= 0) {
				AkrisaeBrother brother = new AkrisaeBrother(player, BROTHER_IDS[Utils.random(BROTHER_IDS.length)], spawnTile, -1, true);
				brother.getCombat().setTarget(player);
				getBrothers().add(brother);
				it$.remove();
				spawnCount--;
			}
		}
	}

	public void removeBrother(AkrisaeBrother brother) {
		ListIterator<AkrisaeBrother> it$ = brothers.listIterator();
		while (it$.hasNext()) {
			AkrisaeBrother brother2 = it$.next();
			if (brother2.getIndex() == brother.getIndex()) {
				it$.remove();
				break;
			}
		}
	}

	/**
	 * Gets the amount of brothers to spawn
	 * 
	 * @return
	 */
	private int getSpawnAmount() {
		if (waves <= 0)
			return Utils.random(1, 2);
		else if (waves > 0 && waves <= 3)
			return Utils.random(2, 4);
		else if (waves > 3 && waves <= 7)
			return Utils.random(3, 6);
		else
			return Utils.random(5, 8);
	}

	/**
	 * Populates the map of hill spawns
	 */
	private void createHillSpawns() {
		hillspawns.add(getWorldTile(13, 5)); // north west
		hillspawns.add(getWorldTile(23, 10)); // north east
		hillspawns.add(getWorldTile(4, 10)); // south east
		hillspawns.add(getWorldTile(4, 25)); // south west
		hillspawns.add(getWorldTile(13, 18)); // south
	}

	/**
	 * Removes the custom region from the game.
	 */
	public void removeRegion() {
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				RegionBuilder.destroyMap(regionChunks[0], regionChunks[1], 8, 8);
			}
		}, 1200, TimeUnit.MILLISECONDS);
	}

	/**
	 * Gets the world tile inside the dynamic region
	 *
	 * @param mapX
	 *            The x in the map
	 * @param mapY
	 *            The y in the map
	 * @return
	 */
	public WorldTile getWorldTile(int mapX, int mapY) {
		return new WorldTile(regionChunks[0] * 8 + mapX, regionChunks[1] * 8 + mapY, 0);
	}

	/**
	 * @return the waves
	 */
	public int getWaves() {
		return waves;
	}

	/**
	 * @param waves
	 *            the waves to set
	 */
	public void setWaves(int waves) {
		this.waves = waves;
	}

	/**
	 * @return the killCount
	 */
	public int getKillCount() {
		return killCount;
	}

	/**
	 * @param killCount
	 *            the killCount to set
	 */
	public void setKillCount(int killCount) {
		this.killCount = killCount;
	}

	/**
	 * @return the brothers
	 */
	public List<AkrisaeBrother> getBrothers() {
		return brothers;
	}

	/**
	 * The list of hill spawns
	 */
	private final List<WorldTile> hillspawns = new ArrayList<>();

	/**
	 * The list of barrows brothers to kill
	 */
	private final List<AkrisaeBrother> brothers = new ArrayList<>();

	/**
	 * The amount of brother's we've killed
	 */
	private int killCount = 0;

	/**
	 * The waves we've completed
	 */
	private int waves = 0;

	/**
	 * The array of region chunks
	 */
	private int[] regionChunks;

	/**
	 * The array of barrow brother ids
	 */
	private static final int[] BROTHER_IDS = new int[] { 2025, // ahrim
			2030, // verac
			2026, // dharok
			2027, // guthan
			2029, // torag
			2028 // karil
	};

	private static final int START_X = 13;
	private static final int START_Y = 18;
	private boolean regionMade = false;
}
