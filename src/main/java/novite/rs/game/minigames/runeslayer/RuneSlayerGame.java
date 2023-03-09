package novite.rs.game.minigames.runeslayer;

import novite.rs.game.Animation;
import novite.rs.game.Hit;
import novite.rs.game.WorldTile;
import novite.rs.game.Hit.HitLook;
import novite.rs.game.item.Item;
import novite.rs.game.player.clans.Clan;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;
import novite.rs.networking.protocol.game.DefaultGameDecoder;
import novite.rs.utility.game.TeleportLocations;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Dec 24, 2013
 */
public class RuneSlayerGame extends Controller {

	@Override
	public void start() {
		setFloor((RuneSlayerFloor) getArguments()[0]);
		setStartedWith(((Clan) getArguments()[1]));
		player.setForceMultiArea(true);
	}

	@Override
	public boolean logout() {
		leaveGame(true);
		return true;
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				player.stopAll();
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage("Oh dear, you have died.");
				} else if (loop == 4) {
					player.setNextAnimation(new Animation(-1));
					leaveGame(false);
					this.stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void process() {
		getFloor().sendFloorInterface(player, getDamage(), getKills());
		getFloor().checkMonstersChange();
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage", "You must log out in order to leave.");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage", "You must log out in order to leave.");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage", "You must log out in order to leave.");
		return false;
	}

	/**
	 * Handles the player leaving the game
	 *
	 * @param logout
	 *            Whether they left by logout or by choice
	 */
	public void leaveGame(boolean logout) {
		floor.giveRewards(player, getFloor(), getDamage(), getKills());
		if (logout) {
			player.setLocation(new WorldTile(TeleportLocations.GAMERS_GROTTO, 2));
		} else {
			player.setNextWorldTile(new WorldTile(TeleportLocations.GAMERS_GROTTO, 2));
		}
		forceClose();
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int packetId) {
		if (interfaceId == 679 && packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
			Item item = player.getInventory().getItem(slotId);
			if (item != null && item.getId() == 18169) {
				player.applyHit(new Hit(player, 100, HitLook.HEALED_DAMAGE));
				player.getInventory().deleteItem(player.getInventory().getItem(slotId));
			}
		}
		return true;
	}
	
	@Override
	public void forceClose() {
		player.reset();
		player.getPackets().closeInterface(player.getInterfaceManager().hasResizableScreen() ? 10 : 8);
		deleteItems();
		removeControler();
	}

	public RuneSlayerFloor getFloor() {
		return floor;
	}

	public void setFloor(RuneSlayerFloor floor) {
		this.floor = floor;
	}

	public int getDamage() {
		return damage;
	}

	public void addDamage(int amount) {
		damage += amount;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public Clan getStartedWith() {
		return startedWith;
	}

	public void setStartedWith(Clan startedWith) {
		this.startedWith = startedWith;
	}

	private void deleteItems() {
		for (int item : dungeoneerItems) {
			if (player.getInventory().contains(item)) {
				player.getInventory().deleteItem(item, player.getInventory().getNumberOf(item));
			}
		}
	}

	private int kills;
	private int damage;
	private Clan startedWith;
	private RuneSlayerFloor floor;

	private int[] dungeoneerItems = new int[] { 17582, 17584, 17594, 18169 };

}