package novite.rs.api.event.command.impl;

import novite.rs.api.event.command.CommandSkeleton;
import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.dialogues.BuyItemDialogue;
import novite.rs.utility.game.Rights;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 21, 2014
 */
public class Empty extends CommandSkeleton {

	@Override
	public Rights getRightsRequired() {
		return Rights.PLAYER;
	}

	@Override
	public String[] getCommandApplicable() {
		return new String[] { "empty", "dump" };
	}

	@Override
	public void execute(Player player) {
		if (player.getInventory().getFreeSlots() == 28) {
			player.sendMessage("You have no items in your inventory to empty.");
			return;
		}
		player.getDialogueManager().startDialogue(new BuyItemDialogue() {
			
			@Override
			public void start() {
				int interfaceId = 94;
				int firstFreeSlot = -1;
				for (int i = 0; i < player.getInventory().getItems().toArray().length; i++) {
					if (player.getInventory().getItems().toArray()[i] != null) {
						firstFreeSlot = i;
						break;
					}
				}
				item = player.getInventory().getItem(firstFreeSlot);
				
				player.getPackets().sendIComponentText(interfaceId, 2, "Are you sure you want to clear your inventory?");
				player.getPackets().sendIComponentText(interfaceId, 7, "You won't be able to get your items back EVER.");
				player.getInterfaceManager().sendChatBoxInterface(interfaceId);
				
				player.getPackets().sendIComponentText(interfaceId, 8, item.getName());
				player.getPackets().sendItemOnIComponent(interfaceId, 9, item.getId(), item.getAmount());
			}
			
			@Override
			public void run(int interfaceId, int option) {
				if (option == YES) {
					for (Item item : player.getInventory().getItems().toArray()) {
						if (item == null)
							continue;
						player.getInventory().deleteItem(item);
					}
				}
				end();
			}
		});
	}

}
