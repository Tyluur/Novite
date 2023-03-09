package novite.rs.api.event.listeners.interfaces;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.minigames.runeslayer.RuneSlayerShop;
import novite.rs.game.minigames.runeslayer.RuneSlayerShop.RuneSlayerReward;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.dialogues.BuyItemDialogue;
import novite.rs.utility.Utils;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class RuneSlayerShopListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 940 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		switch (buttonId) {
		case 2: // select item
			final RuneSlayerReward reward = RuneSlayerReward.getReward(slotId);
			if (reward != null) {
				player.getDialogueManager().finishDialogue();
				player.getDialogueManager().startDialogue(new BuyItemDialogue() {
					
					@Override
					public void run(int interfaceId, int option) {
						if (option == YES) {
							if (player.getFacade().getRuneSlayerPoints() < reward.getCost()) {
								sendDialogue("You need " + Utils.format(reward.getCost()) + " RuneSlayer Points to buy this reward.");
								stage = -2;
								return;
							}
							player.getInventory().addItem(reward.getId(), 1);
							player.getFacade().setRuneSlayerPoints(player.getFacade().getRuneSlayerPoints() - reward.getCost());
							RuneSlayerShop.sendTokens(player);
						}
						end();
					}
					
				}, new Item(reward.getId()), "This costs " + Utils.format(reward.getCost()) + " RuneSlayer Tokens.");
			} else {
				player.getDialogueManager().startDialogue("SimpleMessage", "This reward has not yet been added.");
			}
			break;
		}
		return true;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleNPCClick(Player player, NPC npc, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		// TODO Auto-generated method stub
		return false;
	}

}
