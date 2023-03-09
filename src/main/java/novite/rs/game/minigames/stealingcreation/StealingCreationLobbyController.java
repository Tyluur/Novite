package novite.rs.game.minigames.stealingcreation;

import novite.rs.game.Animation;
import novite.rs.game.ForceMovement;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.FloorItem;
import novite.rs.game.player.Player;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.tasks.WorldTask;
import novite.rs.game.tasks.WorldTasksManager;

public class StealingCreationLobbyController extends Controller {

	@Override
	public void start() {
		sendInterfaces();
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().setOverlay(804, false);
		StealingCreationLobby.updateTeamInterface(player, false);
	}

	@Override
	public boolean canTakeItem(FloorItem item) {
		return false;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 39508 || object.getId() == 39509) {
			player.getControllerManager().forceStop();
			climOverStile(player, object, false);
			return false;
		}
		return true;
	}

	public static void climOverStile(final Player player, final WorldObject object, final boolean enterance) {
		player.lock(3);
		if (enterance) {
			if (!StealingCreationLobby.enterTeamLobby(player, object.getId() == 39509)) {
				return;
			}
		} else {
			StealingCreationLobby.removePlayer(player);
		}
		player.setNextAnimation(new Animation(1560));
		final WorldTile toTile = new WorldTile(enterance ? object.getX() : object.getX() + 2, object.getY(), object.getPlane());
		player.setNextForceMovement(new ForceMovement(player, 0, toTile, 2, enterance ? ForceMovement.WEST : ForceMovement.EAST));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextWorldTile(toTile);
			}
		}, 1);
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage", "A magical force prevents you from teleporting from the arena.");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage", "A magical force prevents you from teleporting from the arena.");
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		StealingCreationLobby.removePlayer(player);
	}

	@Override
	public void forceClose() {
		player.getInterfaceManager().removeOverlay(false);
		StealingCreationLobby.removePlayer(player);
	}

	@Override
	public boolean logout() {
		StealingCreationLobby.removePlayer(player);
		player.setLocation(Helper.EXIT);
		return true;
	}

	@Override
	public boolean login() {
		StealingCreationLobby.removePlayer(player);
		player.setLocation(Helper.EXIT);
		return true;
	}

}
