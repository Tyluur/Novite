package novite.rs.api.event.listeners.objects;

import java.util.ArrayList;
import java.util.List;

import novite.rs.api.event.EventListener;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.minigames.akrisae.AkrisaeFormulae;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.game.player.dialogues.SimpleMessage;
import novite.rs.utility.Utils;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 25, 2014
 */
public class BarrowsChest extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { 10284 };
	}

	@Override
	public boolean handleButtonClick(Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		final int points = player.getFacade().getAkrisaePoints();
		player.getDialogueManager().startDialogue(new Dialogue() {

			@Override
			public void start() {
				sendOptionsDialogue("How many points would you <br>like to use? " + points + " currently", "5 Points", "15 Points", "30 Points", "50 Points");
			}

			@Override
			public void run(int interfaceId, int option) {
				int pointsToUse = option == FIRST ? 5 : option == SECOND ? 15 : option == THIRD ? 30 : 50;
				end();	
				lootChest(player, pointsToUse);
			}

			@Override
			public void finish() {
			}
		});
		return true;
	}

	/**
	 * Loots the barrows chest
	 * 
	 * @param player
	 */
	private void lootChest(final Player player, int toUse) {
		int points = player.getFacade().getAkrisaePoints();
		if (toUse > points) {
			player.getDialogueManager().startDialogue(SimpleMessage.class, "You don't have " + toUse + " akrisae points!");
			return;
		}
		player.getDialogueManager().startDialogue(SimpleMessage.class, "You loot the barrows chest!");
		player.getFacade().setAkrisaePoints(points - toUse);

		List<Item> itemList = new ArrayList<>();

		int lootAmount = AkrisaeFormulae.getLootAmount(toUse);
		int chance = AkrisaeFormulae.getBarrowChance(toUse);

		int rareCount = 0;
		
		for (int i = 0; i < lootAmount; i++) {
			if (Utils.percentageChance(chance) && rareCount < 2) {
				Item item = new Item(BARROW_REWARDS[Utils.random(BARROW_REWARDS.length - 1)]);
				itemList.add(item);
				rareCount++;
			} else {
				Item item = new Item(COMMON_LOOT[Utils.random(COMMON_LOOT.length - 1)]);
				if (item.getDefinitions().isStackable()) {
					item.setAmount(Utils.random(2, 5));
				}
				itemList.add(item);
			}
		}
		
		int amountRacks = toUse == 5 ? 20 : 100;
		itemList.add(new Item(4740, Utils.random(toUse == 20 ? 5 : 50, amountRacks)));

		final Item[] items = itemList.toArray(new Item[itemList.size()]);

		player.getInterfaceManager().sendInterface(364);
		player.getPackets().sendItems(141, items);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				for (Item item : items) {
					player.getInventory().addDroppable(item);
				}
			}
		});
	}

	@Override
	public boolean handleNPCClick(Player player, NPC npc, ClickOption option) {
		return false;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		return false;
	}

	public static int COMMON_LOOT[] = { 1080, 1114, 1148, 1164, 1202, 1214, 1276, 1304, 1320, 1334, 1402, 1404, 562, 563, 564, 565, 554, 555, 556, 557, 558, 559, 560, 561, 566, 4740 };
	public static int BARROW_REWARDS[] = { 4708, 4710, 4712, 4714, 4716, 4718, 4720, 4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745, 4747, 4749, 4751, 4753, 4755, 4757, 4749 };
}
