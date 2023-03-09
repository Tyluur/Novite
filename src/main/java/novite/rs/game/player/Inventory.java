package novite.rs.game.player;

import java.io.Serializable;
import java.util.List;

import novite.rs.game.World;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.item.ItemsContainer;
import novite.rs.utility.ItemExamines;
import novite.rs.utility.Utils;

public final class Inventory implements Serializable {

	private static final long serialVersionUID = 8842800123753277093L;

	private ItemsContainer<Item> items;

	private transient Player player;

	public static final int INVENTORY_INTERFACE = 679;

	public Inventory() {
		items = new ItemsContainer<Item>(28, false);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void init() {
		player.getPackets().sendItems(93, items);
	}

	public void unlockInventoryOptions() {
		player.getPackets().sendIComponentSettings(INVENTORY_INTERFACE, 0, 0, 27, 4554126);
		player.getPackets().sendIComponentSettings(INVENTORY_INTERFACE, 0, 28, 55, 2097152);
	}

	public void reset() {
		items.reset();
		init(); // as all slots reseted better just send all again
	}

	public void refresh(int... slots) {
		player.getPackets().sendUpdateItems(93, items, slots);
	}

	public void addAll(ItemsContainer<Item> items) {
		if (items != null) {
			for (int i = 0; i < items.getSize(); i++) {
				if (items.get(i) != null) {
					this.items.add(items.get(i));
				}
			}
		}
	}

	public void refresh(ItemsContainer<Item> items) {
		if (items != null && player != null) {
			player.getPackets().sendItems(93, items);
		}
	}

	public boolean addItem(int itemId, int amount) {
		if (itemId < 0 || amount < 0 || itemId >= Utils.getItemDefinitionsSize() || !player.getControllerManager().canAddInventoryItem(itemId, amount)) {
			return false;
		}
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(new Item(itemId, amount))) {
			items.add(new Item(itemId, items.getFreeSlots()));
			player.getPackets().sendGameMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	/**
	 * Adds an array of items to your user. If you do not have room for the
	 * item, it will be sent to your bank.
	 * 
	 * @param items
	 *            The array of items
	 */
	public void addMassItem(Item... items) {
		boolean banked = false;
		if (player.getControllerManager().getController() == null) {
			for (Item item : items) {
				if (!player.getInventory().getItems().hasSpaceFor(item)) {
					player.getBank().addItem(item.getId(), item.getAmount(), true);
					banked = true;
				} else if (!player.getInventory().addItem(item)) {
					player.getBank().addItem(item.getId(), item.getAmount(), true);
					banked = true;
				}
			}
		} else {
			for (Item item : items) {
				player.getBank().addItem(item.getId(), item.getAmount(), true);
				banked = true;
			}
		}
		if (banked) {
			player.sendMessage("You did not have space for an item so it was placed in your bank!");
		}
	}

	public boolean addItem(Item item) {
		if (item.getId() < 0 || item.getAmount() < 0 || item.getId() >= Utils.getItemDefinitionsSize() || !player.getControllerManager().canAddInventoryItem(item.getId(), item.getAmount())) {
			return false;
		}
		if (item.getId() == 995 && this.getNumberOf(995) + item.getAmount() < 0) {
			player.getPackets().sendGameMessage("You cannot hold such a large amount of cash in your inventory.");
			World.addGroundItem(item, player, player, true, 60);
			return false;
		}
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(item)) {
			items.add(new Item(item.getId(), items.getFreeSlots()));
			player.getPackets().sendGameMessage("Not enough space in your inventory.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public void deleteItem(int slot, Item item) {
		if (!player.getControllerManager().canDeleteInventoryItem(item.getId(), item.getAmount())) {
			return;
		}
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(slot, item);
		refreshItems(itemsBefore);
	}

	public boolean removeItems(Item... list) {
		for (Item item : list) {
			if (item == null) {
				continue;
			}
			deleteItem(item);
		}
		return true;
	}

	public boolean removeItems(List<Item> list) {
		for (Item item : list) {
			if (item == null) {
				continue;
			}
			deleteItem(item);
		}
		return true;
	}

	public void deleteItem(int itemId, int amount) {
		if (!player.getControllerManager().canDeleteInventoryItem(itemId, amount)) {
			return;
		}
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(new Item(itemId, amount));
		refreshItems(itemsBefore);
	}

	public void deleteItem(Item item) {
		if (!player.getControllerManager().canDeleteInventoryItem(item.getId(), item.getAmount())) {
			return;
		}
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(item);
		refreshItems(itemsBefore);
	}

	/*
	 * No refresh needed its client to who does it :p
	 */
	public void switchItem(int fromSlot, int toSlot) {
		Item[] itemsBefore = items.getItemsCopy();
		Item fromItem = items.get(fromSlot);
		Item toItem = items.get(toSlot);
		items.set(fromSlot, toItem);
		items.set(toSlot, fromItem);
		refreshItems(itemsBefore);
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			if (itemsBefore[index] != items.getItems()[index]) {
				changedSlots[count++] = index;
			}
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
	}

	public ItemsContainer<Item> getItems() {
		return items;
	}

	public boolean hasFreeSlots() {
		return items.getFreeSlot() != -1;
	}

	public int getFreeSlots() {
		return items.getFreeSlots();
	}

	public Item getItem(int slot) {
		return items.get(slot);
	}

	public int getItemsContainerSize() {
		return items.getSize();
	}

	public void addDroppable(Item item) {
		if (item.getId() < 0 || item.getAmount() < 0 || !Utils.itemExists(item.getId()) || !player.getControllerManager().canAddInventoryItem(item.getId(), item.getAmount())) {
			return;
		}
		boolean canAdd = true;
		if (getFreeSlots() == 0) {
			if (contains(item.getId())) {
				if (!item.getDefinitions().isStackable()) {
					canAdd = false;
				}
			} else {
				canAdd = false;
			}
		}
		Item[] itemsBefore = items.getItemsCopy();
		if (!canAdd) {
			player.getPackets().sendGameMessage("Not enough inventory space for this item!");
			World.addGroundItem(item, player, player, true, 60);
		} else {
			items.add(item);
		}
		refreshItems(itemsBefore);
	}

	public boolean contains(int itemId) {
		return items.contains(new Item(itemId, 1));
	}

	public boolean containsItems(Item[] item) {
		for (int i = 0; i < item.length; i++) {
			if (!items.contains(item[i])) {
				return false;
			}
		}
		return true;
	}

	public boolean containsItems(List<Item> list) {
		for (Item item : list) {
			if (!items.contains(item)) {
				return false;
			}
		}
		return true;
	}

	public boolean containsItems(int[] itemIds, int[] ammounts) {
		int size = itemIds.length > ammounts.length ? ammounts.length : itemIds.length;
		for (int i = 0; i < size; i++) {
			if (!items.contains(new Item(itemIds[i], ammounts[i]))) {
				return false;
			}
		}
		return true;
	}

	public boolean containsItem(int itemId, int amount) {
		return items.contains(new Item(itemId, amount));
	}

	public boolean containsOneItem(int... itemIds) {
		for (int itemId : itemIds) {
			if (items.containsOne(new Item(itemId, 1))) {
				return true;
			}
		}
		return false;
	}

	public void sendExamine(int slotId) {
		if (slotId >= getItemsContainerSize()) {
			return;
		}
		Item item = items.get(slotId);
		if (item == null) {
			return;
		}
		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
	}

	public int getNumberOf(int itemId) {
		return items.getNumberOf(itemId);
	}

	public void replaceItem(int id, int amount, int slot) {
		Item item = items.get(slot);
		if (item == null) {
			return;
		}
		item.setId(id);
		item.setAmount(amount);
		refresh(slot);
	}

	public boolean addItemDrop(int itemId, int amount, WorldTile tile) {
		if (itemId < 0 || amount < 0 || !Utils.itemExists(itemId) || !player.getControllerManager().canAddInventoryItem(itemId, amount)) {
			return false;
		}
		Item[] itemsBefore = items.getItemsCopy();
		Item item = new Item(itemId, amount);
		if (!items.add(item)) {
			if (!item.getDefinitions().isNoted() && !item.getDefinitions().isStackable() && item.getDefinitions().getCertId() != -1) {
				item.setId(item.getDefinitions().getCertId());
				if (items.add(item)) {
					refreshItems(itemsBefore);
					return true;
				}
			}
			World.addGroundItem(item, tile, player, true, 180);
		} else {
			refreshItems(itemsBefore);
		}
		return true;
	}

	public void forceRemove(int itemId, int amount) {
		items.remove(new Item(itemId, amount));
	}

	public boolean addItemDrop(int itemId, int amount) {
		return addItemDrop(itemId, amount, new WorldTile(player));
	}

	public int getAmountOf(int itemId) {
		return items.getNumberOf(itemId);
	}

}