package novite.rs.game.player;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.item.Item;
import novite.rs.game.minigames.games.GamesHandler;
import novite.rs.game.npc.familiar.Familiar;
import novite.rs.game.player.dialogues.SimpleNPCMessage;
import novite.rs.utility.Caskets;
import novite.rs.utility.ItemExamines;
import novite.rs.utility.Utils;

public class Bank implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1551246756081236625L;

	// tab, items
	private Item[][] bankTabs;
	private transient Player player;
	private transient int currentTab;
	private transient Item[] lastContainerCopy;
	private transient boolean withdrawNotes;
	private transient boolean insertItems;

	private static final long MAX_BANK_SIZE = 506;

	private BankPinManager pin;
	private int lastX;

	public Bank() {
		bankTabs = new Item[1][0];
	}

	public void setPlayer(Player player) {
		this.player = player;
		if (bankTabs == null || bankTabs.length == 0) {
			bankTabs = new Item[1][0];
		}
		if (pin == null) {
			pin = new BankPinManager(player);
		}
	}

	public void openBank() {
		if (player.getBank().getPin().getInactiveTime() != -1 && player.getBank().getPin().getInactiveTime() > Utils.currentTimeMillis()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You failed to enter your pin properly, so you must wait " + TimeUnit.MILLISECONDS.toHours(pin.getInactiveTime() - Utils.currentTimeMillis()) + " hours until you can try again.");
			return;
		}
		if (!player.getBank().getPin().enteredPinDuringSession() && player.getBank().getPin().hasPin()) {
			player.getTemporaryAttributtes().put("entering_pin", "bank");
			player.getBank().getPin().showEnterPin();
			return;
		}
		player.getInterfaceManager().sendInterface(762);
		player.getInterfaceManager().sendInventoryInterface(763);
		refreshViewingTab();
		refreshTabs();
		unlockButtons();
		sendItems();
		refreshLastX();
		if (!getPin().hasPin()) {
			player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 2759, "Hey, " + player.getDisplayName() + ". I couldn't help but notice that you don't", "have a bank PIN set. Talk to any banker about your", "PIN settings and enjoy a secure bank account today!");
		}
	}

	public void refreshTabs() {
		for (int slot = 1; slot < 9; slot++) {
			refreshTab(slot);
		}
	}

	public int getTabSize(int slot) {
		if (slot >= bankTabs.length) {
			return 0;
		}
		return bankTabs[slot].length;
	}

	public void withdrawLastAmount(int bankSlot) {
		withdrawItem(bankSlot, lastX);
	}

	public void withdrawItemButOne(int fakeSlot) {
		int[] fromRealSlot = getRealSlot(fakeSlot);
		Item item = getItem(fromRealSlot);
		if (item == null) {
			return;
		}
		if (item.getAmount() <= 1) {
			player.getPackets().sendGameMessage("You only have one of this item in your bank");
			return;
		}
		withdrawItem(fakeSlot, item.getAmount() - 1);
	}

	public void depositLastAmount(int bankSlot) {
		depositItem(bankSlot, getLastX(), true);
	}

	public void openPlayerBank(Player victim) {
		if (victim == null) {
			return;
		}
		player.getInterfaceManager().sendInterface(762);
		player.getInterfaceManager().sendInventoryInterface(763);
		player.getPackets().sendItems(95, victim.getBank().getContainerCopy());
		refreshViewingTab();
		refreshTabs();
		unlockButtons();
	}

	public void depositAllInventory(boolean banking) {
		if (Bank.MAX_BANK_SIZE - getBankSize() < player.getInventory().getItems().getSize()) {
			player.getPackets().sendGameMessage("Not enough space in your bank.");
			return;
		}
		for (int i = 0; i < 28; i++) {
			depositItem(i, Integer.MAX_VALUE, false);
		}
		refreshTab(currentTab);
		refreshItems();
	}

	public void depositAllBob(boolean banking) {
		Familiar familiar = player.getFamiliar();
		if (familiar == null || familiar.getBob() == null) {
			return;
		}
		int space = addItems(familiar.getBob().getBeastItems().getItems(), banking);
		if (space != 0) {
			for (int i = 0; i < space; i++) {
				familiar.getBob().getBeastItems().set(i, null);
			}
			familiar.getBob().sendInterItems();
		}
		if (space < familiar.getBob().getBeastItems().getSize()) {
			player.getPackets().sendGameMessage("Not enough space in your bank.");
			return;
		}
	}

	public void depositAllEquipment(boolean banking) {
		int space = addItems(player.getEquipment().getItems().getItems(), banking);
		if (space != 0) {
			for (int i = 0; i < space; i++) {
				player.getEquipment().getItems().set(i, null);
			}
			player.getEquipment().init();
			player.getAppearence().generateAppearenceData();
		}
		if (space < player.getEquipment().getItems().getSize()) {
			player.getPackets().sendGameMessage("Not enough space in your bank.");
			return;
		}
	}

	public void refreshLastX() {
		player.getPackets().sendConfig(1249, getLastX());
	}

	public void collapse(int tabId) {
		if (tabId == 0) {
			return;
		}
		Item[] items = bankTabs[tabId];
		for (Item item : items) {
			removeItem(getItemSlot(item.getId()), item.getAmount(), false, true);
		}
		for (Item item : items) {
			addItem(item.getId(), item.getAmount(), 0, false);
		}
		refreshTabs();
		refreshItems();
	}

	public boolean containsItem(int itemId, int amount) {
		for (Item[] bankTab : bankTabs) {
			for (Item item : bankTab) {
				if (item.getId() == itemId && item.getAmount() >= amount) {
					return true;
				}
			}
		}
		return false;
	}

	public void switchItem(int fromSlot, int toSlot, int fromComponentId, int toComponentId) {
		if (toSlot == 65535) {
			int toTab = toComponentId >= 74 ? 8 - (82 - toComponentId) : 9 - ((toComponentId - 44) / 2);
			if (toTab < 0 || toTab > 9) {
				return;
			}
			if (bankTabs.length == toTab) {
				int[] fromRealSlot = getRealSlot(fromSlot);
				if (fromRealSlot == null) {
					return;
				}
				if (toTab == fromRealSlot[0]) {
					switchItem(fromSlot, getStartSlot(toTab));
					return;
				}
				Item item = getItem(fromRealSlot);
				if (item == null) {
					return;
				}
				removeItem(fromSlot, item.getAmount(), false, true);
				createTab();
				bankTabs[bankTabs.length - 1] = new Item[] { item };
				refreshTab(fromRealSlot[0]);
				refreshTab(toTab);
				refreshItems();
			} else if (bankTabs.length > toTab) {
				int[] fromRealSlot = getRealSlot(fromSlot);
				if (fromRealSlot == null) {
					return;
				}
				if (toTab == fromRealSlot[0]) {
					switchItem(fromSlot, getStartSlot(toTab));
					return;
				}
				Item item = getItem(fromRealSlot);
				if (item == null) {
					return;
				}
				boolean removed = removeItem(fromSlot, item.getAmount(), false, true);
				if (!removed) {
					refreshTab(fromRealSlot[0]);
				} else if (fromRealSlot[0] != 0 && toTab >= fromRealSlot[0]) {
					toTab -= 1;
				}
				refreshTab(fromRealSlot[0]);
				addItem(item.getId(), item.getAmount(), toTab, true);
			}
		} else {
			if (insertItems) {
				insertItem(fromSlot, toSlot);
			} else
				switchItem(fromSlot, toSlot);
		}
	}

	public void insertItem(int fromSlot, int toSlot) {
		int[] fromRealSlot = getRealSlot(fromSlot);
		Item fromItem = getItem(fromRealSlot);
		if (fromItem == null) {
			return;
		}
		int[] toRealSlot = getRealSlot(toSlot);
		Item toItem = getItem(toRealSlot);
		if (toItem == null) {
			return;
		}
		if (toRealSlot[0] != fromRealSlot[0]) {
			bankTabs[fromRealSlot[0]][fromRealSlot[1]] = toItem;
			bankTabs[toRealSlot[0]][toRealSlot[1]] = fromItem;
		} else {
			if (toRealSlot[1] > fromRealSlot[1]) {
				for (int i = fromRealSlot[1]; i < toRealSlot[1]; i++) {
					Item toShift = bankTabs[toRealSlot[0]][fromRealSlot[1] += 1];
					bankTabs[fromRealSlot[0]][i] = toShift;
				}
			} else if (fromRealSlot[1] > toRealSlot[1]) {
				for (int i = fromRealSlot[1]; i > toRealSlot[1]; i--) {
					Item toShift = bankTabs[toRealSlot[0]][fromRealSlot[1] -= 1];
					bankTabs[fromRealSlot[0]][i] = toShift;
				}
			}
			bankTabs[toRealSlot[0]][toRealSlot[1]] = fromItem;
		}
		refreshTab(fromRealSlot[0]);
		if (fromRealSlot[0] != toRealSlot[0]) {
			refreshTab(toRealSlot[0]);
		}
		refreshItems();
	}

	public void switchItem(int fromSlot, int toSlot) {
		int[] fromRealSlot = getRealSlot(fromSlot);
		Item fromItem = getItem(fromRealSlot);
		if (fromItem == null) {
			return;
		}
		int[] toRealSlot = getRealSlot(toSlot);
		Item toItem = getItem(toRealSlot);
		if (toItem == null) {
			return;
		}
		bankTabs[fromRealSlot[0]][fromRealSlot[1]] = toItem;
		bankTabs[toRealSlot[0]][toRealSlot[1]] = fromItem;
		refreshTab(fromRealSlot[0]);
		if (fromRealSlot[0] != toRealSlot[0]) {
			refreshTab(toRealSlot[0]);
		}
		refreshItems();
	}

	public void createTab() {
		int slot = bankTabs.length;
		Item[][] tabs = new Item[slot + 1][];
		System.arraycopy(bankTabs, 0, tabs, 0, slot);
		tabs[slot] = new Item[0];
		bankTabs = tabs;
	}

	public void destroyTab(int slot) {
		Item[][] tabs = new Item[bankTabs.length - 1][];
		System.arraycopy(bankTabs, 0, tabs, 0, slot);
		System.arraycopy(bankTabs, slot + 1, tabs, slot, bankTabs.length - slot - 1);
		bankTabs = tabs;
		if (currentTab != 0 && currentTab >= slot) {
			currentTab--;
		}
	}

	public boolean hasBankSpace() {
		return getBankSize() < MAX_BANK_SIZE;
	}

	public void withdrawItem(int bankSlot, int quantity) {
		if (quantity < 1) {
			return;
		}
		if (!player.getInterfaceManager().containsInterface(762)) {
			return;
		}
		Item item = getItem(getRealSlot(bankSlot));
		if (item == null) {
			return;
		}
		if (item.getAmount() < quantity) {
			item = new Item(item.getId(), item.getAmount());
		} else {
			item = new Item(item.getId(), quantity);
		}
		boolean noted = false;
		ItemDefinitions defs = item.getDefinitions();
		if (withdrawNotes) {
			if (!defs.isNoted() && defs.getCertId() != -1) {
				item.setId(defs.getCertId());
				noted = true;
			} else {
				player.getPackets().sendGameMessage("You cannot withdraw this item as a note.");
			}
		}
		if (noted || defs.isStackable()) {
			if (player.getInventory().getItems().containsOne(item)) {
				int slot = player.getInventory().getItems().getThisItemSlot(item);
				Item invItem = player.getInventory().getItems().get(slot);
				if (invItem.getAmount() + item.getAmount() <= 0) {
					item.setAmount(Integer.MAX_VALUE - invItem.getAmount());
					player.getPackets().sendGameMessage("Not enough space in your inventory.");
				}
			} else if (!player.getInventory().hasFreeSlots()) {
				player.getPackets().sendGameMessage("Not enough space in your inventory.");
				return;
			}
		} else {
			int freeSlots = player.getInventory().getFreeSlots();
			if (freeSlots == 0) {
				player.getPackets().sendGameMessage("Not enough space in your inventory.");
				return;
			}
			if (freeSlots < item.getAmount()) {
				item.setAmount(freeSlots);
				player.getPackets().sendGameMessage("Not enough space in your inventory.");
			}
		}
		removeItem(bankSlot, item.getAmount(), true, false);
		player.getInventory().addItem(item);
	}

	public void sendExamine(int fakeSlot) {
		int[] slot = getRealSlot(fakeSlot);
		if (slot == null) {
			return;
		}
		Item item = bankTabs[slot[0]][slot[1]];
		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
	}

	public void depositItem(int invSlot, int quantity, boolean refresh) {
		if (player.getControllerManager().getController() instanceof GamesHandler) {
			return;
		}
		if (quantity < 1 || invSlot < 0 || invSlot > 27) {
			return;
		}
		Item item = player.getInventory().getItem(invSlot);
		if (item == null) {
			return;
		}
		if (item.getId() == Caskets.PUZZLE_CASKET.getItemId()) {
			player.getDialogueManager().startDialogue(SimpleNPCMessage.class, 2759, "That item can't be banked. Sorry.");
			return;
		}
		int amt = player.getInventory().getItems().getNumberOf(item);
		if (amt < quantity) {
			item = new Item(item.getId(), amt);
		} else {
			item = new Item(item.getId(), quantity);
		}
		ItemDefinitions defs = item.getDefinitions();
		int originalId = item.getId();
		if (defs.isNoted() && defs.getCertId() != -1) {
			item.setId(defs.getCertId());
		}
		Item bankedItem = getItem(item.getId());
		if (bankedItem != null) {
			if (bankedItem.getAmount() + item.getAmount() <= 0) {
				item.setAmount(Integer.MAX_VALUE - bankedItem.getAmount());
				player.getPackets().sendGameMessage("Not enough space in your bank.");
			}
		} else if (!hasBankSpace()) {
			player.getPackets().sendGameMessage("Not enough space in your bank.");
			return;
		}
		player.getInventory().deleteItem(invSlot, new Item(originalId, item.getAmount()));
		addItem(item, refresh);
	}

	private void addItem(Item item, boolean refresh) {
		addItem(item.getId(), item.getAmount(), refresh);
	}

	public int addItems(Item[] items, boolean refresh) {
		int space = (int) (MAX_BANK_SIZE - getBankSize());
		if (space != 0) {
			space = (space < items.length ? space : items.length);
			for (int i = 0; i < space; i++) {
				if (items[i] == null) {
					continue;
				}
				addItem(items[i], false);
			}
			if (refresh) {
				refreshTabs();
				refreshItems();
			}
		}
		return space;
	}

	public void addItem(int id, int quantity, boolean refresh) {
		addItem(id, quantity, currentTab, refresh);
	}

	public void addItem(int id, int quantity, int creationTab, boolean refresh) {
		int[] slotInfo = getItemSlot(id);
		if (slotInfo == null) {
			if (creationTab >= bankTabs.length) {
				creationTab = bankTabs.length - 1;
			}
			if (creationTab < 0) {
				creationTab = 0;
			}
			int slot = bankTabs[creationTab].length;
			Item[] tab = new Item[slot + 1];
			System.arraycopy(bankTabs[creationTab], 0, tab, 0, slot);
			tab[slot] = new Item(id, quantity);
			bankTabs[creationTab] = tab;
			if (refresh) {
				refreshTab(creationTab);
			}
		} else {
			Item item = bankTabs[slotInfo[0]][slotInfo[1]];
			bankTabs[slotInfo[0]][slotInfo[1]] = new Item(item.getId(), item.getAmount() + quantity);
		}
		if (refresh) {
			refreshItems();
		}
	}

	public void removeItem(int id) {
		if (bankTabs != null) {
			for (Item[] bankTab : bankTabs) {
				for (int i2 = 0; i2 < bankTab.length; i2++) {
					if (bankTab[i2].getId() == id) {
						bankTab[i2].setId(0);
					}
				}
			}
		}
	}

	public boolean removeItem(int fakeSlot, int quantity, boolean refresh, boolean forceDestroy) {
		return removeItem(getRealSlot(fakeSlot), quantity, refresh, forceDestroy);
	}

	public boolean removeItem(int[] slot, int quantity, boolean refresh, boolean forceDestroy) {
		if (slot == null) {
			return false;
		}
		Item item = bankTabs[slot[0]][slot[1]];
		boolean destroyed = false;
		if (quantity >= item.getAmount()) {
			if (bankTabs[slot[0]].length == 1 && (forceDestroy || bankTabs.length != 1)) {
				destroyTab(slot[0]);
				refreshTabs();
				destroyed = true;
			} else {
				Item[] tab = new Item[bankTabs[slot[0]].length - 1];
				System.arraycopy(bankTabs[slot[0]], 0, tab, 0, slot[1]);
				System.arraycopy(bankTabs[slot[0]], slot[1] + 1, tab, slot[1], bankTabs[slot[0]].length - slot[1] - 1);
				bankTabs[slot[0]] = tab;
				if (refresh) {
					refreshTab(slot[0]);
				}
			}
		} else {
			bankTabs[slot[0]][slot[1]] = new Item(item.getId(), item.getAmount() - quantity);
		}
		if (refresh) {
			refreshItems();
		}
		return destroyed;
	}

	public Item getItem(int id) {
		for (Item[] bankTab : bankTabs) {
			for (Item item : bankTab) {
				if (item.getId() == id) {
					return item;
				}
			}
		}
		return null;
	}

	public int[] getItemSlot(int id) {
		for (int tab = 0; tab < bankTabs.length; tab++) {
			for (int slot = 0; slot < bankTabs[tab].length; slot++) {
				if (bankTabs[tab][slot].getId() == id) {
					return new int[] { tab, slot };
				}
			}
		}
		return null;
	}

	public Item getItem(int[] slot) {
		if (slot == null) {
			return null;
		}
		return bankTabs[slot[0]][slot[1]];
	}

	public int getStartSlot(int tabId) {
		int slotId = 0;
		for (int tab = 1; tab < (tabId == 0 ? bankTabs.length : tabId); tab++) {
			slotId += bankTabs[tab].length;
		}

		return slotId;

	}

	public int[] getRealSlot(int slot) {
		for (int tab = 1; tab < bankTabs.length; tab++) {
			if (slot >= bankTabs[tab].length) {
				slot -= bankTabs[tab].length;
			} else {
				return new int[] { tab, slot };
			}
		}
		if (slot >= bankTabs[0].length) {
			return null;
		}
		return new int[] { 0, slot };
	}

	public void refreshViewingTab() {
		player.getPackets().sendConfigByFile(4893, currentTab + 1);
	}

	public void refreshTab(int slot) {
		if (slot == 0) {
			return;
		}
		player.getPackets().sendConfigByFile(4885 + (slot - 1), getTabSize(slot));
	}

	public void sendItems() {
		player.getPackets().sendItems(95, getContainerCopy());
	}

	public void refreshItems(int[] slots) {
		player.getPackets().sendUpdateItems(95, getContainerCopy(), slots);
	}

	public Item[] getContainerCopy() {
		if (lastContainerCopy == null) {
			lastContainerCopy = generateContainer();
		}
		return lastContainerCopy;
	}

	public void refreshItems() {
		refreshItems(generateContainer(), getContainerCopy());
	}

	public void refreshItems(Item[] itemsAfter, Item[] itemsBefore) {
		if (itemsBefore.length != itemsAfter.length) {
			lastContainerCopy = itemsAfter;
			sendItems();
			return;
		}
		int[] changedSlots = new int[itemsAfter.length];
		int count = 0;
		for (int index = 0; index < itemsAfter.length; index++) {
			if (itemsBefore[index] != itemsAfter[index]) {
				changedSlots[count++] = index;
			}
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		lastContainerCopy = itemsAfter;
		refreshItems(finalChangedSlots);
	}

	public int getBankSize() {
		int size = 0;
		for (Item[] bankTab : bankTabs) {
			size += bankTab.length;
		}
		return size;
	}

	public Item[] generateContainer() {
		Item[] container = new Item[getBankSize()];
		int count = 0;
		for (int slot = 1; slot < bankTabs.length; slot++) {
			System.arraycopy(bankTabs[slot], 0, container, count, bankTabs[slot].length);
			count += bankTabs[slot].length;
		}
		System.arraycopy(bankTabs[0], 0, container, count, bankTabs[0].length);
		return container;
	}

	public void unlockButtons() {
		int interfaceId = 762;
		// removing the equipment stats button
		player.getPackets().sendHideIComponent(interfaceId, 117, true);
		player.getPackets().sendHideIComponent(interfaceId, 118, true);
		// unlock bank inter all options
		player.getPackets().sendIComponentSettings(interfaceId, 93, 0, 516, 2622718);
		// unlock bank inv all options
		player.getPackets().sendIComponentSettings(763, 0, 0, 27, 2425982);
	}

	public void switchWithdrawNotes() {
		withdrawNotes = !withdrawNotes;
	}

	public void switchInsertItems() {
		insertItems = !insertItems;
		player.getPackets().sendConfig(305, insertItems ? 1 : 0);
	}

	public void setCurrentTab(int currentTab) {
		if (currentTab >= bankTabs.length) {
			return;
		}
		this.currentTab = currentTab;
	}

	/**
	 * Removes the item in the bank by replacing it with 100 coins
	 * 
	 * @param id
	 *            The id of the item to remove
	 */
	public void forceRemove(int id) {
		if (bankTabs != null) {
			for (Item[] bankTab : bankTabs) {
				for (Item element : bankTab) {
					if (element.getId() == id) {
						element.setId(995);
						element.setAmount(100);
					}
				}
			}
		}
	}

	public BankPinManager getPin() {
		return pin;
	}

	public void setPin(BankPinManager pin) {
		this.pin = pin;
	}

	/**
	 * @return the lastX
	 */
	public int getLastX() {
		return lastX;
	}

	/**
	 * @param lastX
	 *            the lastX to set
	 */
	public void setLastX(int lastX) {
		this.lastX = lastX;
	}

}