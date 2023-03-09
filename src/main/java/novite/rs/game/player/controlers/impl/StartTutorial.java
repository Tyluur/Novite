package novite.rs.game.player.controlers.impl;

import novite.rs.Constants;
import novite.rs.game.World;
import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.slayer.Slayer;
import novite.rs.game.player.content.slayer.Type;
import novite.rs.game.player.controlers.Controller;
import novite.rs.game.player.dialogues.impl.HelpDialogue;
import novite.rs.game.player.dialogues.impl.Tutorial_Dialogue;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 22, 2014
 */
public class StartTutorial extends Controller {

	/**
	 * Gives the starter to the player
	 *
	 * @param player
	 *            The player
	 */
	public static void giveStarter(Player player) {
		player.setNextWorldTile(Constants.HOME_TILE);
	}

	@Override
	public void start() {
		sendTutorialDialogue(false);
	}

	/**
	 * Sends the dialogue
	 */
	public void sendTutorialDialogue(boolean increment) {
		player.getDialogueManager().startDialogue(Tutorial_Dialogue.class, stage, increment);
		if (increment) {
			stage = stage + 1;
		}
	}

	/**
	 * Processes a button click
	 * 
	 * @param interfaceId
	 * @param componentId
	 * @param slotId
	 * @param packetId
	 * @return
	 */
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int packetId) {
		return true;
	}

	@Override
	public boolean canWalk() {
		sendTutorialDialogue(false);
		return false;
	}

	@Override
	public boolean login() {
		sendTutorialDialogue(false);
		return false; // so doesnt remove script
	}

	@Override
	public boolean useDialogueScript(Object key) {
		if (key.equals("Tutorial_Dialogue") || (stage >= 6 && key.equals(HelpDialogue.class.getSimpleName()))) {
			return true;
		}
		return false;
	}

	@Override
	public boolean logout() {
		sendTutorialDialogue(false);
		return false; // so doesnt remove script
	}

	/**
	 * What to do when the controler is about to be stopped
	 */
	public void onFinish() {
		Slayer.giveTask(player, "Rock Crab", Type.EASY);
		for (Item item : STARTER_ITEMS)
			player.getInventory().addItem(item);
		World.sendWorldMessage(player.getDisplayName() + " has just logged in for the first time!", false, false);
 	}

	@Override
	public void forceClose() {
		removeControler();
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	private int stage = 0;
	
	private static final Item[] STARTER_ITEMS = new Item[] { 
		new Item(995, 750000), // cash
		new Item(841), // shortbow
		new Item(861), // magic shortbow
		new Item(884, 5000), // iron arrow
		new Item(1712, 1), // glory
		new Item(1323, 1), // iron scim
		new Item(1333, 1), // rune scim
		new Item(1381, 1), // air staff
		new Item(11864), // green d'hide set
		new Item(11818), // iron armour set
		new Item(11960), // mystic robes set
		new Item(11118), // combat bracelet
		new Item(556, 5000), // air runes
		new Item(554, 5000), // fire runes
		new Item(557, 5000), // earth runes
		new Item(555, 5000), // water runes
		new Item(559, 5000), // body runes
		new Item(562, 5000), // chaos runes
		new Item(386, 1000), // sharks
		new Item(8013, 10), // teleport home
	};

}
