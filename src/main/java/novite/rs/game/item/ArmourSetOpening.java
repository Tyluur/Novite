package novite.rs.game.item;

import novite.rs.cache.loaders.ClientScriptMap;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.ArmourSets.Sets;
import novite.rs.utility.ItemExamines;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 21, 2014
 */
public class ArmourSetOpening {

	public static void openSets(Player player) {
		player.getInterfaceManager().sendInterface(645);
		player.getInterfaceManager().sendInventoryInterface(644);
		player.getPackets().sendIComponentSettings(645, 16, 0, 115, 14);
		player.getPackets().sendUnlockIComponentOptionSlots(644, 0, 0, 27, 0, 1, 2);
		player.getPackets().sendInterSetItemsOptionsScript(644, 0, 93, 4, 7, "Components", "Exchange", "Examine");
		player.getPackets().sendRunScript(676);
	}

	public static Sets getSet(int id) {
		for (Sets set : Sets.values())
			if (set.getId() == id)
				return set;
		return null;
	}

	public static void exchangeSet(Player player, int slot, int id) {
		Item item = player.getInventory().getItem(slot);
		if (item == null || item.getId() != id)
			return;
		Sets set = getSet(id);
		if (set == null) {
			player.getPackets().sendGameMessage("This isn't a set item, you can't break it up into component parts.");
			return;
		}
		if (player.getInventory().getFreeSlots() < set.getItems().length - 1) {
			player.getPackets().sendGameMessage("You don't have enough inventory space for the component parts.");
			return;
		}
		player.getInventory().deleteItem(slot, item);
		for (int itemId : set.getItems())
			player.getInventory().addItem(itemId, 1);
		player.getPackets().sendGameMessage("You sucessfully traded your item components for a set!");
	}

	public static void exchangeSet(Player player, int id) {
		Sets set = getSet(id);
		if (set == null) {
			player.getPackets().sendGameMessage("This isn't a set item.");
			return;
		}
		for (int itemId : set.getItems()) {
			if (!player.getInventory().containsItem(itemId, 1)) {
				player.getPackets().sendGameMessage("You don't have the parts to make up this set.");
				return;
			}
		}
		for (int itemId : set.getItems())
			player.getInventory().deleteItem(itemId, 1);
		player.getInventory().addItem(id, 1);
	}

	public static void sendComponentsBySlot(Player player, int slot, int itemId) {
		Item item = player.getInventory().getItem(slot);
		if (item == null || item.getId() != itemId)
			return;
		sendComponents(player, itemId);
	}

	public static void examineSet(Player player, int id) {
		Sets set = getSet(id);
		if (set == null) {
			player.getPackets().sendGameMessage("This isn't a set item.");
			return;
		}
		player.getPackets().sendGameMessage(ItemExamines.getExamine(new Item(id, 1)));
	}

	public static void sendComponents(Player player, int id) {
		Sets set = getSet(id);
		if (set == null) {
			player.getPackets().sendGameMessage("This isn't a set item.");
			return;
		}
		String message = ClientScriptMap.getMap(1088).getStringValue(id);
		if (message == null)
			return;
		player.getPackets().sendGameMessage(message);
	}

	/**
	 * Handles the opening of an armour set
	 *
	 * @param player
	 *            The player
	 * @param item
	 *            The item clicked
	 * @return True if handled
	 */
	public static boolean handleSetOpening(Player player, Item item) {
		String[] inventoryOptions = item.getDefinitions().inventoryOptions;
		if (inventoryOptions == null) {
			return false;
		}
		String name = item.getDefinitions().name;
		boolean hasOpenOption = false;
		for (String option : inventoryOptions) {
			if (option == null) {
				continue;
			}
			if (option.equals("Open") || option.equals("Unpack")) {
				hasOpenOption = true;
			}
		}
		if ((name.toLowerCase().contains("set") || name.toLowerCase().contains("harness")) && hasOpenOption) {
			Sets set = Sets.forId(item.getId());
			if (set != null) {
				int[] items = set.getItems();
				int size = (items.length) - 1;
				if (player.getInventory().getFreeSlots() < size) {
					player.sendMessage("You need " + (size - player.getInventory().getFreeSlots()) + " more free inventory slots to open this armour set.");
					return true;
				}
				player.getInventory().deleteItem(item);
				for (int itemId : items) {
					player.getInventory().addItem(itemId, 1);
				}
				return true;
			}
		}
		return false;
	}

}
