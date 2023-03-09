package novite.rs.game.player;

import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.item.Item;
import novite.rs.game.item.ItemConstants;
import novite.rs.game.item.ItemsContainer;

public class PriceCheckManager {

	private Player player;
	private ItemsContainer<Item> pcInv;

	public PriceCheckManager(Player player) {
		this.player = player;
		pcInv = new ItemsContainer<Item>(28, false);
	}

	public void initPriceCheck() {
		pcInv.clear();
		player.getInterfaceManager().sendInterface(206);
		player.getInterfaceManager().sendInventoryInterface(207);
		sendInterItems();
		sendOptions();
		refresh(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		player.getPackets().sendGlobalConfig(728, 0);
		for (int i = 0; i < pcInv.getSize(); i++) {
			player.getPackets().sendGlobalConfig(700 + i, 0);
		}
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				player.getInventory().getItems().addAll(pcInv);
				player.getInventory().init();
				pcInv.clear();
			}
		});
	}

	public int getSlotId(int clickSlotId) {
		return clickSlotId / 2;
	}

	public void removeItem(int clickSlotId, int amount) {
		int slot = getSlotId(clickSlotId);
		Item item = pcInv.get(slot);
		if (item == null) {
			return;
		}
		Item[] itemsBefore = pcInv.getItemsCopy();
		int maxAmount = pcInv.getNumberOf(item);
		if (amount < maxAmount) {
			item = new Item(item.getId(), amount);
		} else {
			item = new Item(item.getId(), maxAmount);
		}
		pcInv.remove(slot, item);
		player.getInventory().addItem(item);
		refreshItems(itemsBefore);
	}

	public void addItem(int slot, int amount) {
		Item item = player.getInventory().getItem(slot);
		if (item == null) {
			return;
		}
		if (!ItemConstants.isTradeable(item)) {
			player.getPackets().sendGameMessage("That item isn't tradeable.");
			return;
		}
		Item[] itemsBefore = pcInv.getItemsCopy();
		int maxAmount = player.getInventory().getItems().getNumberOf(item);
		if (amount < maxAmount) {
			item = new Item(item.getId(), amount);
		} else {
			item = new Item(item.getId(), maxAmount);
		}
		pcInv.add(item);
		player.getInventory().deleteItem(slot, item);
		refreshItems(itemsBefore);
	}

	public void refreshItems(Item[] itemsBefore) {
		int totalPrice = 0;
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			Item item = pcInv.getItems()[index];
			if (item != null) {
				totalPrice += ItemDefinitions.getItemDefinitions(item.getId()).getValue() * item.getAmount();
			}
			if (itemsBefore[index] != item) {
				changedSlots[count++] = index;
				player.getPackets().sendGlobalConfig(700 + index, item == null ? 0 : ItemDefinitions.getItemDefinitions(item.getId()).getValue());
			}

		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
		player.getPackets().sendGlobalConfig(728, totalPrice);
	}

	public void refresh(int... slots) {
		player.getPackets().sendUpdateItems(90, pcInv, slots);
	}

	public void sendOptions() {
		player.getPackets().sendUnlockIComponentOptionSlots(206, 15, 0, 54, 0, 1, 2, 3, 4, 5, 6);
		player.getPackets().sendUnlockIComponentOptionSlots(207, 0, 0, 27, 0, 1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(207, 0, 93, 4, 7, "Add", "Add-5", "Add-10", "Add-All", "Add-X", "Examine");
	}

	public void sendInterItems() {
		player.getPackets().sendItems(531, pcInv);
		player.getPackets().sendItems(93, player.getInventory().getItems());
	}

}
