package novite.rs.game.player.actions.crafting;

import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.Animation;
import novite.rs.game.player.Player;
import novite.rs.game.player.Skills;
import novite.rs.game.player.actions.Action;

/**
 * 
 * @author Tyluur <itstyluur@gmail.com>
 * @since 2012-12-02
 */
public class Jewellery extends Action {

	private static final int SAPPHIRE = 1607, EMERALD = 1605, RUBY = 1603, DIAMOND = 1601, DRAGONSTONE = 1615, ONYX = 6573;

	public enum GemCreation {

		GOLD_RING(20, GemTypes.RINGS, 1635, 5, 15, -1),

		SAPPHIRE_RING(22, GemTypes.RINGS, 1637, 20, 40, SAPPHIRE),

		EMERALD_RING(24, GemTypes.RINGS, 1639, 27, 55, EMERALD),

		RUBY_RING(26, GemTypes.RINGS, 1641, 34, 70, RUBY),

		DIAMOND_RING(28, GemTypes.RINGS, 1643, 43, 85, DIAMOND),

		DRAGONSTONE_RING(30, GemTypes.RINGS, 1645, 55, 100, DRAGONSTONE),

		ONYX_RING(32, GemTypes.RINGS, 6575, 67, 115, ONYX),

		GOLD_NECKLACE(39, GemTypes.NECKLACES, 1654, 6, 20, -1),

		SAPPHIRE_NECKLACE(41, GemTypes.NECKLACES, 1656, 22, 55, SAPPHIRE),

		EMERALD_NECKLACE(43, GemTypes.NECKLACES, 1658, 29, 60, EMERALD),

		RUBY_NECKLACE(45, GemTypes.NECKLACES, 1660, 40, 75, RUBY),

		DIAMOND_NECKLACE(47, GemTypes.NECKLACES, 1662, 56, 90, DIAMOND),

		DRAGONSTONE_NECKLACE(49, GemTypes.NECKLACES, 1664, 72, 105, DRAGONSTONE),

		ONYX_NECKLACE(51, GemTypes.NECKLACES, 6577, 84, 125, ONYX),

		GOLD_AMULET(58, GemTypes.AMULETS, 1692, 8, 30, -1),

		SAPPHIRE_AMULET(60, GemTypes.AMULETS, 1694, 24, 65, SAPPHIRE),

		EMERALD_AMULET(62, GemTypes.AMULETS, 1696, 31, 70, EMERALD),

		RUBY_AMULET(64, GemTypes.AMULETS, 1698, 50, 85, RUBY),

		DIAMOND_AMULET(66, GemTypes.AMULETS, 1700, 70, 100, DIAMOND),

		DRAGONSTONE_AMULET(68, GemTypes.AMULETS, 1702, 80, 150, DRAGONSTONE),

		ONYX_AMULET(70, GemTypes.AMULETS, 6581, 90, 165, ONYX),

		GOLD_BRACELET(77, GemTypes.BRACELETS, 11069, 7, 25, -1),

		SAPPHIRE_BRACELET(79, GemTypes.BRACELETS, 11072, 23, 60, SAPPHIRE),

		EMERALD_BRACELET(81, GemTypes.BRACELETS, 11076, 30, 65, EMERALD),

		RUBY_BRACELET(83, GemTypes.BRACELETS, 11085, 42, 80, RUBY),

		DIAMOND_BRACELET(85, GemTypes.BRACELETS, 11092, 58, 95, DIAMOND),

		DRAGONSTONE_BRACELET(87, GemTypes.BRACELETS, 11115, 74, 110, DRAGONSTONE),

		ONYX_BRACELET(89, GemTypes.BRACELETS, 11130, 84, 125, ONYX);

		GemCreation(int buttonId, GemTypes type, int reward, int level, int exp, int uncut) {
			this.buttonId = buttonId;
			this.type = type;
			this.level = level;
			this.reward = reward;
			this.exp = exp;
			this.uncut = uncut;
		}
		
		private final int reward, level, buttonId, exp, uncut;
		private final GemTypes type;

		public static GemCreation getGem(int buttonId) {
			for (GemCreation gem : GemCreation.values()) {
				if (gem.buttonId == buttonId)
					return gem;
			}
			return null;
		}

	}

	public enum GemTypes {
		RINGS, NECKLACES, AMULETS, BRACELETS
	}

	private int getMould() {
		switch (gem.type) {
		case AMULETS:
			return 1595;
		case BRACELETS:
			return 11065;
		case NECKLACES:
			return 1597;
		case RINGS:
			return 1592;
		default:
			return 0;
		}
	}

	protected static final int GOLD_BAR = 2357;

	private int buttonId, amount;

	private GemCreation gem;

	@Override
	public boolean start(Player player) {
		if (!checkAll(player)) {
			return false;
		}
		amount = player.getInventory().getNumberOf(GOLD_BAR);
		player.closeInterfaces();
		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		if (amount > 0) {
			amount--;
			player.getInventory().deleteItem(GOLD_BAR, 1);
			if (gem.uncut != -1) {
				player.getInventory().deleteItem(gem.uncut, 1);
			}
			player.setNextAnimation(new Animation(3243));
			player.getInventory().addItem(gem.reward, 1);
			String message = gem.uncut == -1 ? "You smelt the gold bar into a " + ItemDefinitions.getItemDefinitions(gem.reward).getName().toLowerCase() + "." : "You fuse the Gold Bar and " + ItemDefinitions.getItemDefinitions(gem.uncut).getName().toLowerCase() + " together and create a " + ItemDefinitions.getItemDefinitions(gem.reward).getName().toLowerCase() + ".";
			player.sendMessage(message);
			player.getSkills().addXp(Skills.CRAFTING, gem.exp);
		} else {
			stop(player);
		}
		return 2;
	}

	private boolean checkAll(Player player) {
		if (gem == null) {
			player.sendMessage("This gem has not yet been added. (" + buttonId + ")");
			return false;
		}
		String gemName = gem.name().toLowerCase().replace("_", " ");
		if (player.getSkills().getLevel(Skills.CRAFTING) < gem.level) {
			player.sendMessage("You need a Crafting level of " + gem.level + " to create a " + gemName + ".");
			return false;
		}
		if (!player.getInventory().containsItem(getMould(), 1)) {
			player.sendMessage("You need a " + ItemDefinitions.getItemDefinitions(getMould()).getName().toLowerCase() + " to create a " + gemName + ".");
			return false;
		}
		if (!player.getInventory().containsItem(2357, 1)) {
			player.sendMessage("You need a Gold Bar to create a " + gemName + ".");
			return false;
		}
		if (gem.uncut != -1) {
			if (!player.getInventory().containsItem(gem.uncut, 1)) {
				player.sendMessage("You need a " + ItemDefinitions.getItemDefinitions(gem.uncut).getName().toLowerCase() + " to craft " + gemName + "s.");
				return false;
			}
		}
		return true;
	}

	@Override
	public void stop(Player player) {

	}

	public Jewellery(GemCreation gem, int buttonId, int amount) {
		this.gem = gem;
		this.buttonId = buttonId;
		this.amount = amount;
	}

	private static final int[][] JEWELLERY_INTERFACE_VARS = {
		// mould id, id to remove the text, index the images start at
		{ 1592, 14, 19 }, { 1597, 33, 38 }, { 1595, 52, 57 }, { 11065, 71, 76 }, };

	public static void displayJewelleryInterface(Player player) {
		for (int i = 0; i < JEWELLERY_INTERFACE_VARS.length; i++) {
			if (player.getInventory().containsItem(JEWELLERY_INTERFACE_VARS[i][0], 1)) {
				player.getPackets().sendHideIComponent(675, JEWELLERY_INTERFACE_VARS[i][1], true);
				displayJewellery(player, i);
			}
		}
		player.getInterfaceManager().sendInterface(675);
	}

	public static void displayJewellery(Player player, int index) {
		Object[][] items = getItemArray(player, index);
		if (items == null) {
			return;
		}
		int SIZE = 100;
		int interfaceSlot = JEWELLERY_INTERFACE_VARS[index][2];
		player.getPackets().sendItemOnIComponent(675, interfaceSlot, (Integer) items[0][0], SIZE);
		interfaceSlot += 2;
		for (int i = 1; i < items.length; i++) { // i is set to 1 to ignore the
			// gold
			for (int j = 0; j < items[i].length; j++) {
				/*
				 * boolean hasItem =
				 * player.getInventory().containsItem((Integer) GEMS[i - 1][1],
				 * 1); boolean hasLevel =
				 * player.getSkills().getLevel(Skills.CRAFTING) >=
				 * (Integer)GEMS[i - 1][2]; System.out.println("(" +
				 * ItemDefinitions .getItemDefinitions((Integer) GEMS[i - 1][1])
				 * .getName().toLowerCase() + ")[" + (hasItem ?
				 * "You have the item" : "You don't have the item") + " " +
				 * (hasLevel ? "and you have the level" :
				 * "and you don't have the level.") + " (" + items[i][1]); if
				 * (hasItem && hasLevel) {
				 */
				player.getPackets().sendItemOnIComponent(675, interfaceSlot, (Integer) items[i][0], SIZE);
				/*
				 * } else { player.getPackets().sendItemOnIComponent(675,
				 * interfaceSlot, NULL_JEWELLERY[index], SIZE); }
				 */
			}
			interfaceSlot += 2;
		}
	}

	private static Object[][] getItemArray(Player p, int index) {
		switch (index) {
		case 0:
			return RINGS;
		case 1:
			return NECKLACES;
		case 2:
			return AMULETS;
		case 3:
			return BRACELETS;
		}
		return null;
	}

	protected static final int[] NULL_JEWELLERY = { 1647, 1666, 1685, 11067 };

	protected static final Object[][] GEMS = {
		// uncut, cut, level, xp, name, cut emote
		{ 1625, 1609, 1, 15.0, "Opal", 886 }, { 1627, 1611, 13, 20.0, "Jade", 886 }, { 1629, 1613, 16, 25.0, "Red topaz", 887 }, { 1623, 1607, 20, 50.0, "Sapphire", 888 }, { 1621, 1605, 27, 67.5, "Emerald", 889 }, { 1619, 1603, 34, 85.0, "Ruby", 887 }, { 1617, 1601, 43, 107.5, "Diamond", 886 }, { 1631, 1615, 55, 137.5, "Dragonstone", 885 }, { 6571, 6573, 67, 167.5, "Onyx", 2717 }, };

	protected static final Object[][] RINGS = { { 1635, 5, 15.0, "ring" }, { 1637, 20, 40.0, "ring" }, { 1639, 27, 55.0, "ring" }, { 1641, 34, 70.0, "ring" }, { 1643, 43, 85.0, "ring" }, { 1645, 55, 100.0, "ring" }, { 6575, 67, 115.0, "ring" }, };

	protected static final Object[][] NECKLACES = { { 1654, 6, 20.0, "necklace" }, { 1656, 22, 55.0, "necklace" }, { 1658, 29, 60.0, "necklace" }, { 1660, 40, 75.0, "necklace" }, { 1662, 56, 90.0, "necklace" }, { 1664, 72, 105.0, "necklace" }, { 6577, 82, 120.0, "necklace" }, };

	protected static final Object[][] BRACELETS = { { 11069, 7, 25.0, "bracelet" }, { 11072, 23, 60.0, "bracelet" }, { 11076, 30, 65.0, "bracelet" }, { 11085, 42, 80.0, "bracelet" }, { 11092, 58, 95.0, "bracelet" }, { 11115, 74, 110.0, "bracelet" }, { 11130, 84, 125.0, "bracelet" }, };

	protected static final Object[][] AMULETS = {
		// finished id, level, xp, message
		{ 1692, 8, 30.0, "amulet" }, { 1694, 24, 65.0, "amulet" }, { 1696, 31, 70.0, "amulet" }, { 1698, 50, 85.0, "amulet" }, { 1700, 70, 100.0, "amulet" }, { 1702, 80, 150.0, "amulet" }, { 6581, 90, 165.0, "amulet" }, };

}
