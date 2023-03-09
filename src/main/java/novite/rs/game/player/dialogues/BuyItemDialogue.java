package novite.rs.game.player.dialogues;

import novite.rs.game.item.Item;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Feb 21, 2014
 */
public abstract class BuyItemDialogue extends Dialogue {

	@Override
	public void start() {
		int interfaceId = 94;
		item = (Item) parameters[0];
		String message = "";
		if (parameters.length >= 2) {
			message = (String) parameters[1];
		} else {
			message = "Check out this item's benefits on the forums!";
		}
		player.getInterfaceManager().sendChatBoxInterface(interfaceId);
		player.getPackets().sendIComponentText(interfaceId, 2, "Confirm Purchasing: " + item.getAmount() + "x " + item.getName());
		player.getPackets().sendIComponentText(interfaceId, 7, message);

		player.getPackets().sendIComponentText(interfaceId, 8, item.getName());
		player.getPackets().sendItemOnIComponent(interfaceId, 9, item.getId(), item.getAmount());
	}

	@Override
	public void finish() {

	}

	protected Item item;
	protected final int YES = 3;

}
