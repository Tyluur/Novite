package novite.rs.api.event.listeners.interfaces;

import static novite.rs.game.player.content.exchange.ExchangeConfiguration.COLLECTION_INTERFACE;
import static novite.rs.game.player.content.exchange.ExchangeConfiguration.MAIN_INTERFACE;
import static novite.rs.game.player.content.exchange.ExchangeConfiguration.SELL_INTERFACE;
import novite.rs.api.event.EventListener;
import novite.rs.api.input.IntegerInputAction;
import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.engine.CoresManager;
import novite.rs.engine.tasks.ExchangeTask;
import novite.rs.game.WorldObject;
import novite.rs.game.WorldTile;
import novite.rs.game.item.Item;
import novite.rs.game.item.ItemsContainer;
import novite.rs.game.npc.NPC;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.exchange.ExchangeManagement;
import novite.rs.game.player.content.exchange.ExchangeOffer;
import novite.rs.game.player.content.exchange.ExchangeType;
import novite.rs.networking.protocol.game.DefaultGameDecoder;
import novite.rs.utility.ItemExamines;
import novite.rs.utility.Saving;
import novite.rs.utility.Utils;
import novite.rs.utility.game.json.JsonHandler;
import novite.rs.utility.game.json.impl.ExchangeItemLoader;
import novite.rs.utility.game.json.impl.ExchangePriceLoader;

/**
 * @author Tyluur <itstyluur@icloud.com>
 * @since Jun 22, 2014
 */
public class GrandExchangeListener extends EventListener {

	@Override
	public int[] getEventIds() {
		return new int[] { COLLECTION_INTERFACE, MAIN_INTERFACE, SELL_INTERFACE };
	}

	@Override
	public boolean handleButtonClick(final Player player, int interfaceId, int buttonId, int packetId, int slotId, int itemId) {
		// System.out.println("[interfaceId=" + interfaceId + ", buttonId=" +
		// buttonId + ", packetId=" + packetId + ", itemId=" + itemId +
		// ", slotId=" + slotId + "]");
		ExchangeOffer offer;
		switch (interfaceId) {
		case MAIN_INTERFACE:
			switch (buttonId) {
			case 31: // BUY
			case 82:
			case 101:
			case 47:
			case 63:
			case 120:
				player.getAttributes().put("exchange_slot", getSlot(buttonId));
				sendScreen(player, ExchangeType.BUY);
				break;
			case 83: // SELL
			case 32:
			case 48:
			case 102:
			case 121:
			case 64:
				player.getAttributes().put("exchange_slot", getSlot(buttonId));
				sendScreen(player, ExchangeType.SELL);
				break;
			case 128: // back
				resetInterfaceConfigs(player);
				player.getInterfaceManager().closeInventory();
				player.getInterfaceManager().sendInventory();
				final int lastGameTab = player.getInterfaceManager().openGameTab(4); // inventory
				player.setCloseInterfacesEvent(new Runnable() {
					@Override
					public void run() {
						player.getInterfaceManager().sendInventory();
						player.getInventory().unlockInventoryOptions();
						player.getInterfaceManager().sendEquipment();
						player.getInterfaceManager().openGameTab(lastGameTab);
					}
				});
				ExchangeManagement.sendSummary(player);
				break;
			case 190: // choose item button
				player.getPackets().sendRunScript(570, new Object[] { "Grand Exchange Item Search" });
				break;
			case 157: // +1
				offer = (ExchangeOffer) player.getAttributes().get("exchange_offer");
				increaseAmount(player, offer, 1);
				break;
			case 160:
				offer = (ExchangeOffer) player.getAttributes().get("exchange_offer");
				if (offer == null)
					break;
				if (offer.getType() == ExchangeType.SELL) {
					offer.setAmountRequested(1);
					player.getPackets().sendConfig(1110, offer.getAmountRequested());
				} else {
					increaseAmount(player, offer, 1);
				}
				break;
			case 162:
				offer = (ExchangeOffer) player.getAttributes().get("exchange_offer");
				if (offer == null)
					break;
				if (offer.getType() == ExchangeType.SELL) {
					offer.setAmountRequested(10);
					player.getPackets().sendConfig(1110, offer.getAmountRequested());
				} else {
					increaseAmount(player, offer, 10);
				}
				break;
			case 164:
				offer = (ExchangeOffer) player.getAttributes().get("exchange_offer");
				if (offer == null)
					break;
				if (offer.getType() == ExchangeType.SELL) {
					offer.setAmountRequested(100);
					player.getPackets().sendConfig(1110, offer.getAmountRequested());
				} else {
					increaseAmount(player, offer, 100);
				}
				break;
			case 166:
				offer = (ExchangeOffer) player.getAttributes().get("exchange_offer");
				if (offer == null)
					break;
				switch (offer.getType()) {
				case BUY:
					increaseAmount(player, offer, 1000);
					break;
				case SELL:
					if (player.getAttributes().get("exchange_sell_item") != null) {
						int[] ids = (int[]) player.getAttributes().get("exchange_sell_item");
						offer.setAmountRequested(player.getInventory().getNumberOf(ids[0]));
					} else {
						offer.setAmountRequested(player.getInventory().getNumberOf(offer.getItemId()));
					}
					player.getPackets().sendConfig(1110, offer.getAmountRequested());
					break;
				}
				break;
			case 168:
				offer = (ExchangeOffer) player.getAttributes().get("exchange_offer");
				if (offer == null)
					break;
				final ExchangeOffer offer2 = offer;
				player.getPackets().sendInputIntegerScript("Enter amount", new IntegerInputAction() {

					@Override
					public void handle(int input) {
						offer2.setAmountRequested(input);
						player.getPackets().sendConfig(1110, offer2.getAmountRequested());
					}
				});
				break;
			case 181: // -5%
				offer = (ExchangeOffer) player.getAttributes().get("exchange_offer");
				if (offer == null)
					break;
				offer.setPrice((int) Math.ceil(offer.getPrice() - (offer.getPrice() * 0.05)));
				player.getPackets().sendConfig(1111, offer.getPrice());
				break;
			case 175: // set guide price
				offer = (ExchangeOffer) player.getAttributes().get("exchange_offer");
				if (offer == null)
					break;
				offer.setPrice(((ExchangePriceLoader) JsonHandler.getJsonLoader(ExchangePriceLoader.class)).getAveragePrice(offer.getItemId()));
				player.getPackets().sendConfig(1111, offer.getPrice());
				break;
			case 177: // input price
				offer = (ExchangeOffer) player.getAttributes().get("exchange_offer");
				if (offer == null)
					break;
				player.getPackets().sendInputIntegerScript("Enter Price", new IntegerInputAction() {

					@Override
					public void handle(int input) {
						((ExchangeOffer) player.getAttributes().get("exchange_offer")).setPrice(input);
						player.getPackets().sendConfig(1111, ((ExchangeOffer) player.getAttributes().get("exchange_offer")).getPrice());
					}
				});
				break;
			case 179: // +5%
				offer = (ExchangeOffer) player.getAttributes().get("exchange_offer");
				if (offer == null)
					break;
				offer.setPrice((int) Math.ceil(offer.getPrice() + (offer.getPrice() * 0.05)));
				player.getPackets().sendConfig(1111, offer.getPrice());
				break;
			case 186: // confirm
				offer = (ExchangeOffer) player.getAttributes().get("exchange_offer");
				if (offer == null)
					break;
				final int requestedCash = offer.getAmountRequested() * offer.getPrice();

				if (requestedCash > Integer.MAX_VALUE || requestedCash == Integer.MAX_VALUE || requestedCash >= Integer.MAX_VALUE || requestedCash < 0 || requestedCash <= 0 || offer.getAmountRequested() == 0 || offer.getPrice() == 0) {
					player.getPackets().sendGameMessage("Invalid input.");
					return true;
				}
				if (!ItemDefinitions.getItemDefinitions(offer.getItemId()).isExchangeable()) {
					player.getPackets().sendGameMessage("This item is not valid in the grand exchange.");
					return true;
				}
				switch (offer.getType()) {
				case BUY:
					if (player.takeMoney(requestedCash)) {
						Saving.savePlayer(player);
						ExchangeItemLoader loader = JsonHandler.getJsonLoader(ExchangeItemLoader.class);
						loader.addOffer(offer);
						ExchangeManagement.sendSummary(player);
					} else {
						player.sendMessage("You do not have " + Utils.format(requestedCash) + " coins to make this exchange.");
					}
					break;
				case SELL:
					int noteId = -1;
					int sellId = -1;
					if (player.getAttributes().get("exchange_sell_item") != null) {
						int[] ids = (int[]) player.getAttributes().get("exchange_sell_item");
						sellId = ids[1];
						noteId = ids[0];
					} else {
						sellId = offer.getItemId();
					}
					int sellingId = noteId == -1 ? sellId : noteId;
					if (player.getInventory().getNumberOf(sellingId) < offer.getAmountRequested()) {
						player.sendMessage("You do not have " + Utils.format(offer.getAmountRequested()) + " of this item to sell.");
						return true;
					}
					player.getInventory().deleteItem(sellingId, offer.getAmountRequested());
					Saving.savePlayer(player);
					ExchangeItemLoader loader = JsonHandler.getJsonLoader(ExchangeItemLoader.class);
					loader.addOffer(offer);
					ExchangeManagement.sendSummary(player);
					break;
				}
				CoresManager.slowExecutor.execute(new Runnable() {
					
					@Override
					public void run() {
						ExchangeTask.get().process();
					}
				});
				break;
			case 155:
				offer = (ExchangeOffer) player.getAttributes().get("exchange_offer");
				increaseAmount(player, offer, -1);
				break;
			case 19:
			case 35:
			case 51:
			case 108:
			case 89:
			case 70:
				player.getAttributes().put("exchange_slot", getSlot(buttonId));
				offer = getOfferBySlot(player, getSlot(buttonId));
				if (offer == null)
					break;
				if (packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET) {
					sendCollectInformation(player, getSlot(buttonId));
				} else {
					abortOffer(player, offer);
				}
				break;
			case 200: // abort via information screen:
				offer = getOfferBySlot(player, (int) player.getAttributes().get("exchange_slot"));
				if (offer == null)
					break;
				abortOffer(player, offer);
				break;
			case 208:
			case 206: // collecting
				offer = getOfferBySlot(player, (int) player.getAttributes().get("exchange_slot"));
				if (offer == null)
					break;
				collectItem(player, offer, itemId, packetId, buttonId);
				break;
			}
			break;
		case SELL_INTERFACE:
			if (!ItemDefinitions.getItemDefinitions(itemId).isTradeable() || itemId == 995) {
				player.sendMessage("That item cannot be sold on the grand exchange.");
				return true;
			}
			player.getAttributes().remove("exchange_sell_item");

			final int itemUsed = itemId;
			int itemId2 = itemId;
			if (ItemDefinitions.getItemDefinitions(itemUsed).isNoted())
				itemId2 = ItemDefinitions.getItemDefinitions(itemUsed).getCertId();

			//ExchangePriceLoader loader = JsonHandler.getJsonLoader(ExchangePriceLoader.class);

			final int amountToSell = 1;
			final int price = ExchangePriceLoader.getInfiniteQuantityPrice(itemUsed);

			ExchangeOffer sellOffer = new ExchangeOffer(player.getUsername(), itemId2, ExchangeType.SELL, (int) player.getAttributes().get("exchange_slot"), amountToSell, price);

			player.getAttributes().put("exchange_offer", sellOffer);
			if (itemId2 != itemUsed)
				player.getAttributes().put("exchange_sell_item", new int[] { itemUsed, itemId2 });

			player.getPackets().sendConfig(1109, sellOffer.getItemId());
			player.getPackets().sendConfig(1110, amountToSell);
			player.getPackets().sendConfig(1111, price);
			player.getPackets().sendConfig(1114, price);

			StringBuilder bldr = new StringBuilder();

			bldr.append(ItemExamines.getExamine(new Item(itemId2)) + "<br><br>");
			bldr.append("The grand exchange will purchase this item for 5% less than its guide price.");
			player.getPackets().sendIComponentText(MAIN_INTERFACE, 143, bldr.toString());
			break;
		case COLLECTION_INTERFACE:
			switch (buttonId) {
			default:
			case 19:
				collectItems(player, 0, slotId == 0 ? 0 : 1, packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET ? 0 : 1);
				break;
			case 23:
				collectItems(player, 1, slotId == 0 ? 0 : 1, packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET ? 0 : 1);
				break;
			case 27:
				collectItems(player, 2, slotId == 0 ? 0 : 1, packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET ? 0 : 1);
				break;
			case 32:
				collectItems(player, 3, slotId == 0 ? 0 : 1, packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET ? 0 : 1);
				break;
			case 37:
				collectItems(player, 4, slotId == 0 ? 0 : 1, packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET ? 0 : 1);
				break;
			case 42:
				collectItems(player, 5, slotId == 0 ? 0 : 1, packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET ? 0 : 1);
				break;
			}
			break;
		}
		return true;
	}

	/**
	 * Collects an offer from the collection box
	 * 
	 * @param player
	 * @param offerSlot
	 * @param itemSlot
	 * @param option
	 */
	private void collectItems(Player player, int offerSlot, int itemSlot, int option) {
		ExchangeOffer offer = getOfferBySlot(player, offerSlot);
		if (offer == null)
			return;
		ExchangeItemLoader loader = JsonHandler.<ExchangeItemLoader> getJsonLoader(ExchangeItemLoader.class);
		if (loader == null)
			return;
		Item item = offer.getItemsToCollect().get(itemSlot);
		if (item == null) {
			System.out.println("Player attempted to remove item in slot " + offerSlot + " but wasn't there");
			return;
		}
		int freeSlots = player.getInventory().getFreeSlots();
		if (freeSlots == 0) {
			player.sendMessage("Not enough space in your inventory.");
			return;
		}

		int newId = -1;
		boolean noted = false;
		int amount = item.getAmount();
		if (!item.getDefinitions().isStackable() && item.getAmount() > 1 && option == 0) {
			noted = true;
		}
		if (!item.getDefinitions().isStackable() && option == 1)
			noted = true;
		if (noted) {
			newId = item.getDefinitions().getCertId();
		}
		if (newId == -1)
			newId = item.getId();
		if (itemSlot == 0) {
			offer.setAmountReceived(0);
		} else {
			offer.setSurplus(0);
		}

		// System.out.println("Collecting item: " + item + ", [newId=" + newId +
		// ", noted=" + noted + ", itemSlot=" + itemSlot + ", option=" + option
		// + "]");

		if (offer.isAborted()) {
			loader.removeOffer(offer);
			ExchangeManagement.openCollectionBox(player);
		} else {
			if (offer.getAmountProcessed() >= offer.getAmountRequested() && offer.getItemsToCollect().getUsedSlots() == 0) {
				loader.removeOffer(offer);
				ExchangeManagement.openCollectionBox(player);
			} else {
				loader.saveProgress(offer);
				ExchangeManagement.openCollectionBox(player);
			}
		}

		player.getInventory().addItemDrop(newId, amount);
	}

	/**
	 * Collects an item from the offer's collection exchange
	 * 
	 * @param player
	 *            The player
	 * @param offer
	 *            The offer
	 * @param itemId
	 *            The item id
	 * @param packetId
	 *            The packet id
	 * @param buttonId
	 */
	private void collectItem(Player player, ExchangeOffer offer, int itemId, int packetId, int buttonId) {
		synchronized (ExchangeItemLoader.LOCK) {
			Item item = offer.getItemsToCollect().lookup(itemId);
			if (item == null)
				return;
			ExchangeItemLoader loader = JsonHandler.<ExchangeItemLoader> getJsonLoader(ExchangeItemLoader.class);
			if (loader == null)
				return;
			int freeSlots = player.getInventory().getFreeSlots();
			if (freeSlots == 0) {
				player.getPackets().sendGameMessage("Not enough space in your inventory.");
				return;
			}
			int amount = item.getAmount();
			int slot = buttonId == 206 ? 1 : 2;
			int option = packetId == DefaultGameDecoder.ACTION_BUTTON1_PACKET ? 1 : 2;
			boolean toNote = false;

			if (!item.getDefinitions().isStackable() && amount > 1 && option == 1) {
				toNote = true;
			}
			if (!item.getDefinitions().isStackable() && amount == 1 && option == 2)
				toNote = true;

			int newId = toNote ? ItemDefinitions.getItemDefinitions(itemId).getCertId() : itemId;

			if (newId == -1)
				newId = itemId;

			int amountReq = offer.getAmountRequested();

			if (slot == 1) {
				offer.setAmountReceived(0);
			} else {
				offer.setSurplus(0);
			}

			if (offer.isAborted()) {
				loader.removeOffer(offer);
				ExchangeManagement.sendSummary(player);
			} else {
				if (offer.getAmountProcessed() >= amountReq && offer.getItemsToCollect().getUsedSlots() == 0) {
					loader.removeOffer(offer);
					ExchangeManagement.sendSummary(player);
				} else {
					loader.saveProgress(offer);
					sendCollectInformation(player, offer.getSlot());
					if (offer.getItemsToCollect().getUsedSlots() == 0)
						ExchangeManagement.sendSummary(player);
				}
			}
			player.getInventory().addItemDrop(newId, amount);
		}
	}

	/**
	 * Aborts an offer for the player
	 * 
	 * @param player
	 *            The player
	 * @param offer
	 *            The offer to abort
	 */
	private void abortOffer(Player player, ExchangeOffer offer) {
		synchronized (ExchangeItemLoader.LOCK) {
			if (offer.isProcessing() || offer.isAborted()) {
				player.sendMessage("You cannot abort this offer right now...");
				return;
			}
			if (offer.getItemsToCollect().getUsedSlots() > 0) {
				player.sendMessage("You need to collect your items before aborting the offer.");
				return;
			}
			offer.setAborted(true);
			ExchangeManagement.sendSummary(player);
			sendCollectInformation(player, offer.getSlot());

			((ExchangeItemLoader) JsonHandler.getJsonLoader(ExchangeItemLoader.class)).saveProgress(offer);
			player.sendMessage("Abort request acknowledged. Please be aware that your offer may have already been completed.");
		}
	}

	/**
	 * Sends the collection box with two slots to the player, This is based on
	 * the offer in the slot provided. The offer's items to collect will display
	 * here. @see ExchangeOffer#getItemsToCollect()
	 * 
	 * @param player
	 * @param slotId
	 */
	public static void sendCollectInformation(Player player, int slotId) {
		ExchangeOffer offer = getOfferBySlot(player, slotId);
		if (offer == null) {
			ItemsContainer<Item> ic = new ItemsContainer<Item>(2, true);
			player.getPackets().sendConfig(1112, slotId);
			player.getPackets().sendItems(523 + slotId, ic);
			return;
		}
		ItemsContainer<Item> ic = offer.getItemsToCollect();
		player.getPackets().sendConfig(1113, offer.getType().ordinal());
		player.getPackets().sendConfig(1112, slotId);
		player.getPackets().sendItems(523 + slotId, ic);

		player.getPackets().sendIComponentSettings(105, 206, -1, -1, 6);
		player.getPackets().sendIComponentSettings(105, 208, -1, -1, 6);
		player.getPackets().sendIComponentText(MAIN_INTERFACE, 143, ItemExamines.getExamine(new Item(offer.getItemId())));
	}

	/**
	 * Gets the offer for the player in the selected slot
	 * 
	 * @param player
	 *            The player to get the offer of
	 * @param slot
	 *            The slot of the offer
	 * @return
	 */
	private static ExchangeOffer getOfferBySlot(Player player, int slot) {
		for (ExchangeOffer offer : ((ExchangeItemLoader) JsonHandler.getJsonLoader(ExchangeItemLoader.class)).getOffersList(player.getUsername())) {
			if (offer.getOwner().equals(player.getUsername()) && offer.getSlot() == slot)
				return offer;
		}
		return null;
	}

	/**
	 * Increases the amount of the offer
	 * 
	 * @param player
	 *            The player
	 * @param offer
	 *            The offer
	 * @param amount
	 *            The amount
	 */
	public void increaseAmount(Player player, ExchangeOffer offer, int amount) {
		if (offer == null) {
			return;
		}
		offer.setAmountRequested(offer.getAmountRequested() + amount);
		player.getPackets().sendConfig(1110, offer.getAmountRequested());
	}

	/**
	 * Sends the screen by the type
	 * 
	 * @param type
	 *            The type of offer
	 */
	public void sendScreen(Player player, ExchangeType type) {
		resetInterfaceConfigs(player);
		if (type == ExchangeType.SELL) {
			player.getPackets().sendConfig(1113, 1);
			player.getInterfaceManager().sendInventoryInterface(SELL_INTERFACE);
			final Object[] params = new Object[] { "", "", "", "", "Offer", -1, 0, 7, 4, 93, 7012370 };
			player.getPackets().sendRunScript(149, params);
			player.getPackets().sendItems(93, player.getInventory().getItems());
			player.getPackets().sendHideIComponent(SELL_INTERFACE, 0, false);
			player.getPackets().sendIComponentSettings(SELL_INTERFACE, 18, 0, 27, 1026);
			player.getPackets().sendConfig(1112, (Integer) player.getAttributes().get("exchange_slot"));
			player.getPackets().sendHideIComponent(105, 196, true);
		} else {
			player.getPackets().sendConfig1(744, 0);
			player.getPackets().sendConfig(1112, (Integer) player.getAttributes().get("exchange_slot"));
			player.getPackets().sendConfig(1113, 0);
			player.getPackets().sendInterface(true, 752, 7, 389);
			player.getPackets().sendRunScript(570, new Object[] { "Grand Exchange Item Search" });
		}
	}

	/**
	 * Resets interface configurations to prepare for displaying the buy screen
	 * 
	 * @param player
	 *            The player to reset it for
	 */
	private void resetInterfaceConfigs(Player player) {
		player.getPackets().sendConfig2(1109, -1);
		player.getPackets().sendConfig2(1110, 0);
		player.getPackets().sendConfig2(1111, 1);
		player.getPackets().sendConfig2(1112, -1);
		player.getPackets().sendConfig2(1113, 0);
	}

	/**
	 * Finds the slot of the button you are clicking
	 * 
	 * @param buttonId
	 *            The button you are clicking
	 * @return
	 */
	private int getSlot(int buttonId) {
		switch (buttonId) {
		case 31:
		case 32:
		case 19:
			return 0;
		case 47:
		case 35:
		case 48:
			return 1;
		case 63:
		case 51:
		case 64:
			return 2;
		case 82:
		case 83:
		case 70:
			return 3;
		case 101:
		case 102:
		case 89:
			return 4;
		case 120:
		case 108:
		case 121:
			return 5;
		default:
			return -1;
		}
	}

	@Override
	public boolean handleObjectClick(Player player, int objectId, WorldObject worldObject, WorldTile tile, ClickOption option) {
		return false;
	}

	@Override
	public boolean handleNPCClick(Player player, NPC npc, ClickOption option) {
		return false;
	}

	@Override
	public boolean handleItemClick(Player player, Item item, ClickOption option) {
		return false;
	}

}
