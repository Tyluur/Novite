package novite.rs.game.player.content.trading;

import novite.rs.game.item.Item;
import novite.rs.game.item.ItemConstants;
import novite.rs.game.item.ItemsContainer;
import novite.rs.game.player.Player;
import novite.rs.utility.ItemExamines;
import novite.rs.utility.Utils;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ExchangePriceLoader;
import novite.rs.utility.logging.types.FileLogger;

public class Trade {

	private Player player, target;
	private ItemsContainer<Item> items;
	private boolean tradeModified;
	private boolean accepted;

	public Trade(Player player) {
		this.player = player;
		items = new ItemsContainer<Item>(28, false);
	}

	/*
	 * called to both players
	 */
	public void openTrade(Player target) {
		synchronized (this) {
			synchronized (target.getTrade()) {
				this.target = target;
				player.getPackets().sendIComponentText(335, 15, "Trading With: " + target.getDisplayName());
				player.getPackets().sendGlobalString(203, target.getDisplayName());
				sendInterItems();
				sendOptions();
				sendTradeModified();
				refreshFreeInventorySlots();
				refreshTradeWealth();
				refreshStageMessage(true);
				player.getInterfaceManager().sendInterface(335);
				player.getInterfaceManager().sendInventoryInterface(336);
				player.setCloseInterfacesEvent(new Runnable() {
					@Override
					public void run() {
						closeTrade(CloseTradeStage.CANCEL);
					}
				});
			}
		}
	}

	public void removeItem(final int slot, int amount) {
		synchronized (this) {
			if (!isTrading()) {
				return;
			}
			if (!player.getInterfaceManager().containsInventoryInter()) {
				System.out.println(player.getUsername() + " attempted to remove items after trade stage.");
				return;
			}
			synchronized (target.getTrade()) {
				Item item = items.get(slot);
				if (item == null) {
					return;
				}
				Item[] itemsBefore = items.getItemsCopy();
				int maxAmount = items.getNumberOf(item);
				if (amount < maxAmount) {
					item = new Item(item.getId(), amount);
				} else {
					item = new Item(item.getId(), maxAmount);
				}
				items.remove(slot, item);
				player.getInventory().addItem(item);
				refreshItems(itemsBefore);
				cancelAccepted();
				setTradeModified(true);
			}
		}
	}

	public void sendFlash(int slot) {
		player.getPackets().sendInterFlashScript(335, 32, 4, 7, slot);
		target.getPackets().sendInterFlashScript(335, 32, 4, 7, slot);
	}

	public void cancelAccepted() {
		boolean canceled = false;
		if (accepted) {
			accepted = false;
			canceled = true;
		}
		if (target.getTrade().accepted) {
			target.getTrade().accepted = false;
			canceled = true;
		}
		if (canceled) {
			refreshBothStageMessage(canceled);
		}
	}

	public void addItem(int slot, int amount) {
		synchronized (this) {
			if (!isTrading()) {
				return;
			}
			synchronized (target.getTrade()) {
				Item item = player.getInventory().getItem(slot);
				if (item == null) {
					return;
				}
				if (!ItemConstants.isTradeable(item) && player.getRights() < 3) {
					player.getPackets().sendGameMessage("That item isn't tradeable.");
					return;
				}
				Item[] itemsBefore = items.getItemsCopy();
				int maxAmount = player.getInventory().getItems().getNumberOf(item);
				if (amount < maxAmount) {
					item = new Item(item.getId(), amount);
				} else {
					item = new Item(item.getId(), maxAmount);
				}
				items.add(item);
				player.getInventory().deleteItem(slot, item);
				refreshItems(itemsBefore);
				cancelAccepted(); 
			}
		}
	}

	public void lendItem(int slot, int hours) {
		synchronized (this) {
			if (!isTrading()) {
				return;
			}
			synchronized (target.getTrade()) {
				Item item = player.getInventory().getItem(slot);
				if (item == null) {
					return;
				}
				if (!ItemConstants.isLendable(item)) {
					player.getPackets().sendGameMessage("That item isn't lendable.");
					return;
				}
				Item[] itemsBefore = items.getItemsCopy();

				items.add(item);
				player.getInventory().deleteItem(slot, item);
				refreshItems(itemsBefore);
				cancelAccepted();
			}
		}
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			Item item = items.getItems()[index];
			if (itemsBefore[index] != item) {
				if (itemsBefore[index] != null && (item == null || item.getId() != itemsBefore[index].getId() || item.getAmount() < itemsBefore[index].getAmount())) {
					sendFlash(index);
				}
				changedSlots[count++] = index;
			}
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
		refreshFreeInventorySlots();
		refreshTradeWealth();
	}

	public void sendOptions() {
		Object[] tparams1 = new Object[] { "", "", "", "Value<col=FF9040>", "Remove-X", "Remove-All", "Remove-10", "Remove-5", "Remove", -1, 0, 7, 4, 90, 335 << 16 | 31 };
		player.getPackets().sendRunScript(150, tparams1);
		player.getPackets().sendIComponentSettings(335, 31, 0, 27, 1150); // Access
		Object[] tparams3 = new Object[] { "", "", "", "", "", "", "", "", "Value<col=FF9040>", -1, 0, 7, 4, 90, 335 << 16 | 34 };
		player.getPackets().sendRunScript(695, tparams3);
		player.getPackets().sendIComponentSettings(335, 34, 0, 27, 1026); // Access
		Object[] tparams2 = new Object[] { "", "", "Lend", "Value<col=FF9040>", "Offer-X", "Offer-All", "Offer-10", "Offer-5", "Offer", -1, 0, 7, 4, 93, 336 << 16 };
		player.getPackets().sendRunScript(150, tparams2);
		player.getPackets().sendIComponentSettings(336, 0, 0, 27, 1278); // Access
		/*
		 * player.getPackets().sendIComponentSettings(335, 32, 0, 27, 1150);
		 * player.getPackets().sendInterSetItemsOptionsScript(335, 35, 90, true,
		 * 4, 7, "Value"); player.getPackets().sendIComponentSettings(335, 35,
		 * 0, 27, 1026); player.getPackets().sendInterSetItemsOptionsScript(335,
		 * 60, 541, 4, 7, "'Until logout'", "Edit");
		 * player.getPackets().sendIComponentSettings(335, 62, -1, -1, 6);
		 * player.getPackets().sendIComponentSettings(335, 60, 0, 1, 1014);
		 */
	}

	public boolean isTrading() {
		return target != null;
	}

	public void setTradeModified(boolean modified) {
		if (modified == tradeModified) {
			return;
		}
		tradeModified = modified;
		sendTradeModified();
	}

	public void sendInterItems() {
		player.getPackets().sendItems(90, items);
		target.getPackets().sendItems(90, true, items);
	}

	public void refresh(int... slots) {
		player.getPackets().sendItems(90, false, items);
		player.getPackets().sendItems(90, true, target.getTrade().items);
		target.getPackets().sendItems(90, false, target.getTrade().items);
		target.getPackets().sendItems(90, true, items);
	}

	public void accept(boolean firstStage) {
		synchronized (this) {
			if (!isTrading()) {
				return;
			}
			synchronized (target.getTrade()) {
				if (target.getTrade().accepted) {
					if (firstStage) {
						if (nextStage()) {
							target.getTrade().nextStage();
						}
					} else {
						player.setCloseInterfacesEvent(null);
						player.closeInterfaces();
						closeTrade(CloseTradeStage.DONE);
					}
					return;
				}
				accepted = true;
				refreshBothStageMessage(firstStage);
			}
		}
	}

	public void sendValue(int slot, boolean traders) {
		if (!isTrading()) {
			return;
		}
		Item item = traders ? target.getTrade().items.get(slot) : items.get(slot);
		if (item == null) {
			return;
		}
		if (!ItemConstants.isTradeable(item) && player.getRights() < 3) {
			player.getPackets().sendGameMessage("That item isn't tradeable.");
			return;
		}
		int price = JsonHandler.<ExchangePriceLoader> getJsonLoader(ExchangePriceLoader.class).getAveragePrice(item.getId());
		player.getPackets().sendGameMessage(item.getDefinitions().getName() + ": market price is " + Utils.format(price) + " coins.");
	}

	public void sendValue(int slot) {
		Item item = player.getInventory().getItem(slot);
		if (item == null) {
			return;
		}
		if (!ItemConstants.isTradeable(item) && player.getRights() < 3) {
			player.getPackets().sendGameMessage("That item isn't tradeable.");
			return;
		}
		int price = JsonHandler.<ExchangePriceLoader> getJsonLoader(ExchangePriceLoader.class).getAveragePrice(item.getId());
		player.getPackets().sendGameMessage(item.getDefinitions().getName() + ": market price is " + price + " coins.");
	}

	public void sendExamine(int slot, boolean traders) {
		if (!isTrading()) {
			return;
		}
		Item item = traders ? target.getTrade().items.get(slot) : items.get(slot);
		if (item == null) {
			return;
		}
		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
	}

	public boolean nextStage() {
		if (!isTrading()) {
			return false;
		}
		if (player.getInventory().getItems().getUsedSlots() + target.getTrade().items.getUsedSlots() > 28) {
			player.setCloseInterfacesEvent(null);
			player.closeInterfaces();
			closeTrade(CloseTradeStage.NO_SPACE);
			return false;
		}
		accepted = false;
		player.getInterfaceManager().sendInterface(334);
		player.getInterfaceManager().closeInventoryInterface();
		player.getPackets().sendHideIComponent(334, 55, !(tradeModified || target.getTrade().tradeModified));
		refreshBothStageMessage(false);
		return true;
	}

	public void refreshBothStageMessage(boolean firstStage) {
		refreshStageMessage(firstStage);
		target.getTrade().refreshStageMessage(firstStage);
	}

	public void refreshStageMessage(boolean firstStage) {
		player.getPackets().sendIComponentText(firstStage ? 335 : 334, firstStage ? 37 : 34, getAcceptMessage(firstStage));
	}

	public String getAcceptMessage(boolean firstStage) {
		if (accepted) {
			return "Waiting for other player...";
		}
		if (target.getTrade().accepted) {
			return "Other player has accepted.";
		}
		return firstStage ? "" : "Are you sure you want to make this trade?";
	}

	public void sendTradeModified() {
		player.getPackets().sendConfig(1042, tradeModified ? 1 : 0);
		target.getPackets().sendConfig(1043, tradeModified ? 1 : 0);
	}

	public void refreshTradeWealth() {
		int wealth = getTradeWealth();
		player.getPackets().sendGlobalConfig(729, wealth);
		target.getPackets().sendGlobalConfig(697, wealth);
	}

	public void refreshFreeInventorySlots() {
		int freeSlots = player.getInventory().getFreeSlots();
		target.getPackets().sendIComponentText(335, 21, "has " + (freeSlots == 0 ? "no" : freeSlots) + " free" + "<br>inventory slots");
	}

	public int getTradeWealth() {
		int wealth = 0;
		for (Item item : items.getItems()) {
			if (item == null) {
				continue;
			}
			wealth += JsonHandler.<ExchangePriceLoader> getJsonLoader(ExchangePriceLoader.class).getAveragePrice(item.getId()) * item.getAmount();
		}
		return wealth;
	}

	private static enum CloseTradeStage {
		CANCEL, NO_SPACE, DONE
	}

	public void closeTrade(CloseTradeStage stage) {
		synchronized (this) {
			synchronized (target.getTrade()) {
				Player oldTarget = target;
				target = null;
				tradeModified = false;
				accepted = false;
				if (CloseTradeStage.DONE != stage) {
					for (Item item : items.toArray()) {
						if (item == null) {
							continue;
						}
						player.getInventory().addItem(item);
					}
					player.getInventory().init();
					items.clear();
				} else {
					player.getPackets().sendGameMessage("Accepted trade.");
					FileLogger.getFileLogger().writeLog("trade/", "[TRADE SESSION]", true);
					for (Item item : oldTarget.getTrade().items.toArray()) {
						if (item == null) {
							continue;
						}
						FileLogger.getFileLogger().writeLog("trade/", player.getDisplayName() + " received " + Utils.format(item.getAmount()) + "x " + item.getDefinitions().getName() + " from " + oldTarget.getDisplayName() + ".", true);
						player.getInventory().addItem(item);
					}
					FileLogger.getFileLogger().writeLog("trade/", "[TRADE SESSION COMPLETE]", true);
					// player.getInventory().getItems().addAll(oldTarget.getTrade().items);
					player.getInventory().init();
					oldTarget.getTrade().items.clear();
				}
				if (oldTarget.getTrade().isTrading()) {
					oldTarget.setCloseInterfacesEvent(null);
					oldTarget.closeInterfaces();
					oldTarget.getTrade().closeTrade(stage);
					if (CloseTradeStage.CANCEL == stage) {
						oldTarget.getPackets().sendGameMessage("<col=ff0000>Other player declined trade!");
					} else if (CloseTradeStage.NO_SPACE == stage) {
						player.getPackets().sendGameMessage("You don't have enough space in your inventory for this trade.");
						oldTarget.getPackets().sendGameMessage("Other player doesn't have enough space in their inventory for this trade.");
					}
				}
			}
		}
	}

	public boolean forceAddItem(int id, int amount) {
		synchronized (this) {
			if (!isTrading()) {
				return false;
			}
			for (Item allItems : items.toArray()) {
				if (allItems == null) {
					continue;
				}
				if (allItems.getId() == 995) {
					if (allItems.getAmount() + amount > Integer.MAX_VALUE || allItems.getAmount() + amount < 0) {
						player.sendMessage("You cannot add that many...");
						return false;
					}
				}
			}
			synchronized (target.getTrade()) {
				Item item = new Item(id, amount);
				if (!ItemConstants.isTradeable(item) && player.getRights() < 3) {
					player.getPackets().sendGameMessage("That item isn't tradeable.");
					return false;
				}
				Item[] itemsBefore = items.getItemsCopy();
				items.add(item);
				refreshItems(itemsBefore);
				cancelAccepted();
			}
		}
		return true;
	}
}
