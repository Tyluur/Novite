package novite.rs.game.item;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import novite.rs.cache.Cache;
import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.player.Equipment;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.shop.ShopPrices;
import novite.rs.game.player.dialogues.impl.Bob;

public class ItemConstants {

	public static int getDegradeItemWhenWear(int id) {
		return -1;
	}

	public static int getItemDefaultCharges(int id) {
		if (Bob.isBarrows(id)) {
			int charges = Bob.getCharges(id);
			if (charges == -1)
				return 1;
			else
				return 1000; // 10 minutes
		}
		return -1;
	}

	public static int getItemDegrade(int id) {
		// nex armors
		if (id == 20137 || id == 20141 || id == 20145 || id == 20149 || id == 20153 || id == 20157 || id == 20161 || id == 20165 || id == 20169 || id == 20173) {
			return id + 1;
		}
		if (Bob.isBarrows(id)) {
			return Bob.getDegraded(false, id);
		}
		return -1;
	}

	public static int getDegradeItemWhenCombating(int id) {
		// nex armors
		if (id == 20135 || id == 20139 || id == 20143 || id == 20147 || id == 20151 || id == 20155 || id == 20159 || id == 20163 || id == 20167 || id == 20171) {
			return id + 2;
		}
		return -1;
	}

	public static boolean itemDegradesWhileHit(int id) {
		if (id == 2550) {
			return true;
		}
		return false;
	}

	public static boolean itemDegradesWhileWearing(int id) {
		return false;
	}

	public static boolean itemDegradesWhileCombating(int id) {
		if (Bob.isBarrows(id))
			return true;
		return false;
	}

	public static boolean isDestroy(Item item) {
		return item.getDefinitions().isDestroyItem() || item.getDefinitions().isLended();
	}

	public static boolean isLendable(Item item) {
		return !item.getDefinitions().isLended() && item.getDefinitions().getLendId() > -1;
	}

	public static void main(String[] args) throws IOException {
		Cache.init();
		Item item = new Item(16933);
		System.out.println(item.getName() + ", " + item.getDefinitions().getEquipSlot());
		System.out.println(canExchange(item.getId()));
		System.out.println(canExchange(item, null));
		/*
		 * boolean write = true;
		 * 
		 * if (write) { BufferedWriter writer = new BufferedWriter(new
		 * FileWriter(file)); for (int i = 0; i <
		 * Utils.getItemDefinitionsSize(); i++) { item = new Item(i); if
		 * (CacheFixer.canExchange(item)) { writer.write(item.getId() + ", " +
		 * item.getDefinitions().getName()); writer.newLine(); writer.flush(); }
		 * } writer.close(); }
		 */
	}

	public static boolean canExchange(Item item, Player player) {
		if (item.getDefinitions().isExchangeable()) {
			return true;
		}
		if (!item.getDefinitions().isExchangeable()) {
			return false;
		}
		if (!item.getDefinitions().isWearItem()) {
			return false;
		}
		if (!item.getDefinitions().isTradeable()) {
			return false;
		}
		for (int[] items : ShopPrices.VOTE_SHOP) {
			if (item.getId() == items[0]) {
				return false;
			}
		}
		for (int[] items : ShopPrices.PKP_SHOP) {
			if (item.getId() == items[0]) {
				return false;
			}
		}
		for (int[] items : ShopPrices.RUNECOINS_ITEMS) {
			if (item.getId() == items[0]) {
				return false;
			}
		}
		if (!Equipment.canWear(item, player)) {
			return false;
		}
		return true;
	}

	public static boolean isTradeable(Object object) {
		int itemId = -1;
		if (object instanceof Item) {
			itemId = ((Item) object).getId();
		} else if (object instanceof Integer) {
			itemId = (Integer) object;
		} else {
			throw new IllegalStateException("Invalid parameters! Only Item or Integer can be specified");
		}
		ItemDefinitions definitions = ItemDefinitions.getItemDefinitions(itemId);
		for (String n : forceTradeable) {
			if (definitions.getName().toLowerCase().contains(n.toLowerCase())) {
				return true;
			}
		}
		return definitions.isTradeable();
	}

	public static List<Integer> getUnexchangeables() {
		if (unexchangeables == null) {
			unexchangeables = new ArrayList<Integer>();
			try {
				List<String> text = (ArrayList<String>) Files.readAllLines(new File("./data/exchange/full_exchange_list.txt").toPath(), Charset.defaultCharset());
				for (String line : text) {
					if (line.startsWith("//"))
						continue;
					String[] split = line.split(": ");
					unexchangeables.add(Integer.parseInt(split[1]));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return unexchangeables;
	}

	private static List<Integer> unexchangeables = null;

	public static boolean shouldExchange(int itemId) {
		return getUnexchangeables().contains(itemId);
	}

	public static boolean canExchange(int itemId) {
		if (!isTradeable(itemId)) {
			return false;
		}
		novite.rs.cache.loaders.ItemDefinitions definitions = novite.rs.cache.loaders.ItemDefinitions.getItemDefinitions(itemId);
		if (definitions.isNoted()) {
			return false;
		}
		if (!definitions.isExchangeable())
			return false;
		return true;
	}

	private static final String[] forceTradeable = new String[] {};

}
