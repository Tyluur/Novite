package novite.rs.game.player.controlers.impl.quest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import novite.rs.engine.CoresManager;
import novite.rs.game.Animation;
import novite.rs.game.RegionBuilder;
import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.npc.others.quest.RFDNpc;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.player.quests.QuestManager;
import novite.rs.game.player.quests.impl.Recipe_For_Disaster;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.utility.game.TeleportLocations;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 29, 2014
 */
public class RFDQuest extends Controller {

	@Override
	public void start() {
		createRegion();
		player.getPrayer().closeAllPrayers();
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage("You have been defeated!");
				} else if (loop == 3) {
					player.reset();
					forceClose();
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int packetId) {
		if (interfaceId == 271 && packetId == 61 || (interfaceId == 749 && componentId == 1)) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You are not allowed to use prayers in this quest.");
			return false;
		}
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		forceClose();
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage("You can't teleport out of the arena!");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage("You can't teleport out of the arena!");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage("You can't teleport out of the arena!");
		return false;
	}

	private void createRegion() {
		player.lock(); // locks player
		CoresManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				boundChuncks = RegionBuilder.findEmptyChunkBound(8, 8);
				RegionBuilder.copyAllPlanesMap(237, 669, boundChuncks[0], boundChuncks[1], 64);
				player.setNextWorldTile(getWorldTile(7, 14));
				mapMade = true;
				player.unlock();
			}
		});
	}

	private void removeRegion() {
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				RegionBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
			}
		}, 1200, TimeUnit.MILLISECONDS);
	}

	@Override
	public void forceClose() {
		player.setNextWorldTile(TeleportLocations.QUESTING_DOME);
		removeRegion();
		removeControler();
	}

	@Override
	public void process() {
		if (mapMade) {
			List<Integer> npcs = World.getRegion(player.getRegionId()).getNPCsIndexes();
			if (npcs == null || npcs.isEmpty()) {
				nextWave();
			}
		}
	}

	/**
	 * Starts up the next wave
	 */
	private void nextWave() {
		if (player.getFacade().getLastRFDWave() < 4) {
			RFDNpc target = new RFDNpc(WAVES[player.getFacade().getLastRFDWave()], getWorldTile(7, 7));
			target.setNextAnimation(new Animation(-1));
			target.getCombat().setTarget(player);
			player.getHintIconsManager().addHintIcon(target, 1, -1, false);
		} else {
			player.getControllerManager().forceStop();
			player.getDialogueManager().startDialogue("SimpleMessage", "Recipe for Disaster Complete!", "Speak with the Gloves Gypsy to purchase rewards.");
			player.getQuestManager().finishQuest(QuestManager.getQuest(Recipe_For_Disaster.class).getName());
		}
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
		return new WorldTile(boundChuncks[0] * 8 + mapX, boundChuncks[1] * 8 + mapY, 2);
	}

	private int[] boundChuncks;
	private boolean mapMade;

	private static final int[] WAVES = { 3493, 3494, 3495, 3496 };

}
