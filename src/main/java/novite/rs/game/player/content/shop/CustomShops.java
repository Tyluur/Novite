package novite.rs.game.player.content.shop;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.utility.Utils;
import novite.rs.utility.game.ChatColors;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 31, 2014
 */
public enum CustomShops {

	VOTE("Vote_Exchange") {
		@Override
		public void sendCost(Player player, Item item) {
			sendCustomCost(player, ShopPrices.VOTE_SHOP, item, "Vote Tokens");
		}

		@Override
		public boolean buyCustomCurrency(Player player, Item item, int buyQuantity) {
			return buyWith(player, "Vote Tokens", item, buyQuantity, player.getInventory().getNumberOf(10944), ShopPrices.VOTE_SHOP);
		}

		@Override
		public boolean onBuy(Player player, Item item, int buyQuantity, int customPrice) {
			if (player.getInventory().addItem(item.getId(), item.getId() == 18831 ? item.getAmount() : buyQuantity)) {
				player.getInventory().deleteItem(10944, customPrice);
				return true;
			}
			return false;
		}
	},
	TOKKUL("Tokkul_Store") {
		@Override
		public void sendCost(Player player, Item item) {
			sendCustomCost(player, ShopPrices.TOKKUL_SHOP, item, "Tokkul");
		}

		@Override
		public boolean buyCustomCurrency(Player player, Item item, int buyQuantity) {
			return buyWith(player, "Tokkul", item, buyQuantity, player.getInventory().getNumberOf(6529), ShopPrices.TOKKUL_SHOP);
		}

		@Override
		public boolean onBuy(Player player, Item item, int buyQuantity, int customPrice) {
			if (player.getInventory().addItem(item.getId(), buyQuantity)) {
				player.getInventory().deleteItem(6529, customPrice);
				return true;
			}
			return false;
		}
	},

	GOLDPOINTS(new String[] { "Gold_Points_Armours", "Gold_Points_Weapons", "Gold_Points_Untradeables", "Gold_Points_Rares" }) {
		@Override
		public void sendCost(Player player, Item item) {
			sendCustomCost(player, ShopPrices.GOLD_POINTS_SHOP, item, "Gold Points");
		}

		@Override
		public boolean buyCustomCurrency(Player player, Item item, int buyQuantity) {
			return buyWith(player, "Gold Points", item, buyQuantity, ((Long) player.getFacade().getGoldPoints()).intValue(), ShopPrices.GOLD_POINTS_SHOP);
		}

		@Override
		public boolean onBuy(Player player, Item item, int buyQuantity, int customPrice) {
			if (player.getInventory().addItem(item.getId(), buyQuantity)) {
				player.getFacade().setGoldPoints(player.getFacade().getGoldPoints() - customPrice);
				return true;
			}
			return false;
		}
	},

	NAZI(new String[] { "Novite_Games_Rewards" }) {
		@Override
		public void sendCost(Player player, Item item) {
			sendCustomCost(player, ShopPrices.NOVITE_GAMES_SHOP, item, "Novite Game Points");
		}

		@Override
		public boolean buyCustomCurrency(Player player, Item item, int buyQuantity) {
			return buyWith(player, "Novite Game Points", item, buyQuantity, player.getFacade().getNoviteGamePoints(), ShopPrices.NOVITE_GAMES_SHOP);
		}

		@Override
		public boolean onBuy(Player player, Item item, int buyQuantity, int customPrice) {
			if (player.getInventory().addItem(item.getId(), buyQuantity)) {
				player.getFacade().setNoviteGamePoints(player.getFacade().getNoviteGamePoints() - customPrice);
				return true;
			}
			return false;
		}
	},

	PkPoints(new String[] { "PK_Point_Exchange" }) {
		@Override
		public void sendCost(Player player, Item item) {
			sendCustomCost(player, ShopPrices.PKP_SHOP, item, "Pk Points");
		}

		@Override
		public boolean buyCustomCurrency(Player player, Item item, int buyQuantity) {
			return buyWith(player, "Pk Points", item, buyQuantity, player.getFacade().getPkPoints(), ShopPrices.PKP_SHOP);
		}

		@Override
		public boolean onBuy(Player player, Item item, int buyQuantity, int customPrice) {
			if (player.getInventory().addItem(item.getId(), item.getAmount())) {
				player.getFacade().setPkPoints(player.getFacade().getPkPoints() - customPrice);
				return true;
			}
			return false;
		}
	},

	Achievement("Achievement_Rewards") {
		@Override
		public void sendCost(Player player, Item item) {
			sendCustomCost(player, ShopPrices.ACHIEVEMENT_SHOP, item, "Achievement Points");
		}

		@Override
		public boolean buyCustomCurrency(Player player, Item item, int buyQuantity) {
			return buyWith(player, "Achievement Points", item, buyQuantity, player.getFacade().getAchievementPoints(), ShopPrices.ACHIEVEMENT_SHOP);
		}

		@Override
		public boolean onBuy(Player player, Item item, int buyQuantity, int customPrice) {
			if (player.getInventory().addItem(item.getId(), buyQuantity)) {
				player.getFacade().setAchievementPoints(player.getFacade().getAchievementPoints() - customPrice);
				return true;
			}
			return false;
		}
	},

	DONOR_SHOP("Donator_Shop") {
		@Override
		public void sendCost(Player player, Item item) {
			sendCustomCost(player, ShopPrices.DONATOR_SHOP, item, "coins");
		}

		@Override
		public boolean buyCustomCurrency(Player player, Item item, int buyQuantity) {
			return buyWith(player, "coins", item, buyQuantity, player.getInventory().getNumberOf(995), ShopPrices.DONATOR_SHOP);
		}

		@Override
		public boolean onBuy(Player player, Item item, int buyQuantity, int customPrice) {
			if (player.getInventory().addItem(item.getId(), buyQuantity)) {
				player.getInventory().deleteItem(995, customPrice);
				return true;
			}
			return false;
		}
	};

	CustomShops(String[] name) {
		this.name = name;
	}

	CustomShops(String name) {
		this.name = new String[] { name };
	}

	private final String[] name;

	/**
	 * Sends the custom cost of the item
	 * 
	 * @param player
	 *            The player
	 * @param item
	 *            The item
	 */
	public abstract void sendCost(Player player, Item item);

	/**
	 * Buys an item with the custom currency
	 * 
	 * @param player
	 *            The player
	 * @param item
	 *            The item
	 * @param buyQuantity
	 *            The amount to buy
	 * @return
	 */
	public abstract boolean buyCustomCurrency(Player player, Item item, int buyQuantity);

	/**
	 * What to do when the item is bought
	 * 
	 * @param player
	 *            The player
	 * @param item
	 *            The item bought
	 * @param buyQuantity
	 *            The amount bought
	 * @param customPrice
	 *            The custom price for the item
	 */
	public abstract boolean onBuy(Player player, Item item, int buyQuantity, int customPrice);

	/**
	 * Sends the custom cost of the item
	 * 
	 * @param player
	 *            The player
	 * @param items
	 *            The items array
	 * @param item
	 *            The item
	 * @param name
	 *            The currency name
	 */
	protected void sendCustomCost(Player player, int[][] items, Item item, String name) {
		boolean found = false;
		for (int[] item2 : items) {
			if (item.getId() == item2[0]) {
				player.getPackets().sendGameMessage("<col=" + ChatColors.BLUE + ">" + item.getAmount() + "x " + item.getName() + "</col>: currently costs: <col=" + ChatColors.MAROON + ">" + Utils.format(item2[1]) + " " + name + ".");
				found = true;
			}
		}
		if (!found) {
			player.sendMessage(item.getId() + " ~" + item.getName() + " wasnt registered.");
			System.err.println(item.getId() + " ~" + item.getName() + " wasnt registered.");
		}
	}

	/**
	 * Buys the item with the custom currency
	 * 
	 * @param player
	 *            The player
	 * @param name
	 *            The name of the currency
	 * @param item
	 *            The item bought
	 * @param buyQuantity
	 *            The amount of the item bought
	 * @param points
	 *            The points used
	 * @param prices
	 *            The array of prices
	 * @return
	 */
	protected boolean buyWith(Player player, String name, Item item, int buyQuantity, int points, int[][] prices) {
		for (int[] price : prices) {
			if (item.getId() == price[0]) {
				int iPrice = price[1] * buyQuantity;
				boolean shouldPurchaseQuantity = name.equalsIgnoreCase("Pk Points") || item.getId() == 18831;
				if (shouldPurchaseQuantity) {
					iPrice = price[1] * 1;
					buyQuantity = item.getAmount();
				}
				if (buyQuantity < 1 || iPrice < 1) {
					player.sendMessage("You need to buy more than 0 of that!");
					return false;
				}
				if (points < iPrice) {
					player.sendMessage("You need <col=" + ChatColors.MAROON + ">" + Utils.format(iPrice - points) + "</col> more " + name + " to purchase <col=" + ChatColors.MAROON + ">" + buyQuantity + "</col> " + item.getName().toLowerCase() + ".");
					return false;
				} else {
					player.getTemporaryAttributtes().put("custom_shop_price", iPrice);
					player.sendMessage("You buy <col=" + ChatColors.MAROON + ">" + Utils.format(buyQuantity) + "</col> " + item.getName() + "" + (item.getName().endsWith("s") ? "" : "s") + " from the " + this.name[0]);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the custom shop by the name
	 * 
	 * @param name
	 *            The name to search by
	 * @return A {@link #CustomShops()} {@code Object}
	 */
	public static CustomShops getCustomShop(String name) {
		for (CustomShops shop : CustomShops.values()) {
			for (String shopName : shop.name) {
				if (shopName.equalsIgnoreCase(name.replaceAll(" ", "_"))) {
					return shop;
				}
			}
		}
		return null;
	}

}
