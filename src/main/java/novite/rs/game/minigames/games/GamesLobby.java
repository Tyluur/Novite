package novite.rs.game.minigames.games;

import novite.rs.game.WorldTile;
import novite.rs.game.player.controlers.Controller;
import novite.rs.utility.Utils;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jul 14, 2014
 */
public class GamesLobby extends Controller {

	@Override
	public void start() {
		player.setNextWorldTile(new WorldTile(2660, 2639, 0));
		if (!MainGameHandler.get().getLobbyPlayers().contains(player))
			MainGameHandler.get().getLobbyPlayers().add(player);
	}

	@Override
	public void process() {
		sendTab();
	}

	@Override
	public boolean logout() {
		leaveLobby(true);
		return true;
	}

	/**
	 * Sends the tab interface and clears the text on it
	 */
	public void sendTab() {
		int interfaceId = 407;
		int resizableId = 10;
		int normalId = 8;
		boolean shouldAdd = !player.getInterfaceManager().containsInterface(interfaceId);
		player.getPackets().sendIComponentText(interfaceId, 3, "Novite Games");
		player.getPackets().sendIComponentText(interfaceId, 13, "Next Departure: " + MainGameHandler.get().getSecondsTillStart());
		player.getPackets().sendIComponentText(interfaceId, 14, "Players Ready: " + MainGameHandler.get().getLobbyPlayers().size());
		player.getPackets().sendIComponentText(interfaceId, 15, "(Need 3 to 25 Players)");
		player.getPackets().sendIComponentText(interfaceId, 16, "Points: " + Utils.format(player.getFacade().getNoviteGamePoints()));
		if (shouldAdd) {
			player.getInterfaceManager().sendTab(player.getInterfaceManager().hasResizableScreen() ? resizableId : normalId, interfaceId);
		}
	}

	/**
	 * Leaves the lobby
	 * 
	 * @param logout
	 */
	public void leaveLobby(boolean logout) {
		MainGameHandler.get().getLobbyPlayers().remove(player);
		if (logout) {
			player.setLocation(new WorldTile(2657, 2639, 0));
		} else {
			player.reset();
			player.getPackets().closeInterface(player.getInterfaceManager().hasResizableScreen() ? 10 : 8);
			player.setNextWorldTile(new WorldTile(2657, 2639, 0));
		}
		forceClose();
		removeControler();
	}
	
	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage", "You cant leave like this..");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage", "You cant leave like this..");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage", "You cant leave like this..");
		return false;
	}

}
