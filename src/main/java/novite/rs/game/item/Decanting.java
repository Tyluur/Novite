package novite.rs.game.item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import novite.rs.Constants;
import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.player.Player;
import novite.rs.utility.game.item.ItemNames;

/**
 * This class handles the decantation of potions in the most efficient way
 * possible.
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since August 3rd, 2013
 */
public final class Decanting {

	/**
	 * Figures out if you can use the two potions on each other
	 *
	 * @param first
	 *            The name of the first potion used
	 * @param second
	 *            The name of the second potion used
	 * @return
	 */
	private static boolean isPotionSimilar(String first, String second) {
		String firstBase = getBaseName(first);
		String secondBase = getBaseName(second);
		if (!firstBase.equalsIgnoreCase(secondBase)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the doses of the potion
	 *
	 * @param name
	 *            The name of the potion
	 * @return
	 */
	private static int getPotionDoses(String[] name) {
		int doses = 0;
		for (String split : name) {
			for (Character c : split.toCharArray()) {
				if (Character.isDigit(c)) {
					doses = Character.getNumericValue(c);
					break;
				}
			}
		}
		return doses;
	}

	/**
	 * Gets the base name of the potion
	 *
	 * @param name
	 *            The name of the potion
	 * @return
	 */
	private static String getBaseName(String name) {
		String newName = "";
		for (Character c : name.toCharArray()) {
			if (Character.isLetter(c) || Character.isWhitespace(c)) {
				newName += c;
			}
		}
		return newName.trim();
	}

	/**
	 * Figures out if the item you are using is a potion or not.
	 *
	 * @param name
	 *            The name of the item
	 * @return
	 */
	private static boolean isPotion(String name) {
		if (name.toLowerCase().contains("amulet") || name.toLowerCase().contains("games") || name.toLowerCase().contains("flask")) {
			return false;
		}
		String altString = "";
		for (Character c : name.toCharArray()) {
			if (Character.isLetter(c) || Character.isWhitespace(c)) {
				continue;
			}
			altString += c;
		}
		Pattern pattern = Pattern.compile("\\([0-9]\\)");
		Matcher m = pattern.matcher(altString);
		if (m.find()) {
			return true;
		}
		return false;
	}

	/**
	 * Handles the decantation process of two like potions
	 *
	 * @param player
	 *            The player who is decanting
	 * @param fromSlot
	 *            The slot the first item is from
	 * @param toSlot
	 *            The slot the second item is from
	 * @return
	 */
	public static boolean handleDecanting(Player player, int fromSlot, int toSlot) {
		try {
			Item used = player.getInventory().getItem(toSlot);
			Item with = player.getInventory().getItem(fromSlot);
			String usedBase = used.getName();
			String withBase = with.getName();
			if (!isPotion(usedBase) || !isPotion(withBase) || !isPotionSimilar(usedBase, withBase)) {
				return false;
			}
			String baseName = getBaseName(usedBase);
			String[] usedSplit = usedBase.split(" ");
			String[] withSplit = withBase.split(" ");
			int usedDoses = getPotionDoses(usedSplit);
			int withDoses = getPotionDoses(withSplit);
			int totalDoses = usedDoses + withDoses;
			int[] newDoses = new int[2];
			if (totalDoses > 4) {
				newDoses[0] = 4;
				newDoses[1] = totalDoses - 4;
			} else {
				newDoses[0] = totalDoses;
			}
			if (totalDoses == 8) {
				return true;
			}
			player.getInventory().deleteItem(with);
			player.getInventory().deleteItem(used);
			for (int dose : newDoses) {
				if (dose == 0) {
					player.getInventory().addItem(new Item(229));
					continue;
				}
				String newName = baseName + " (" + dose + ")";
				int newId = ItemNames.getId(newName);
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(newId);
				if (defs.isNoted() && defs.getCertId() != -1) {
					newId = defs.getCertId();
				}
				Item item = new Item(newId, 1);
				player.getInventory().addItem(item);
			}
			if (Constants.DEBUG) {
				StringBuilder bldr = new StringBuilder();
				for (int dose : newDoses) {
					bldr.append(dose + ", ");
				}
				System.out.println("Dosage info[baseName=" + baseName + ", total=" + totalDoses + ", usedDoses=" + usedDoses + ", withDoses=" + withDoses + ", newDoses=" + bldr.toString().trim() + "]");
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * All inventory items will be transformed into flasks if they're (3).
	 *
	 * @param player
	 *            The player
	 */
	public static void decantToFlask(Player player) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		Object lock = new Object();
		for (Item item : player.getInventory().getItems().toArray()) {
			if (item == null || item.getDefinitions().isNoted() || !isPotion(item.getName())) {
				continue;
			}
			synchronized (lock) {
				String name = item.getName();
				String base = Decanting.getBaseName(name);
				int doses = getPotionDoses(name.split(" "));
				if (map.containsKey(base)) {
					int amt = map.get(base);
					map.remove(base);
					map.put(base, amt + doses);
				} else {
					map.put(base, doses);
				}
			}
		}
		Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
		synchronized (lock) {
			while (it.hasNext()) {
				Entry<String, Integer> entry = it.next();
				/** The name of the potion */
				String name = entry.getKey();
				/** The amount of total doses of the potion */
				int amount = entry.getValue();
				if (amount == 0 || amount % 6 != 0) {
					player.sendMessage("You only had " + amount + " total doses of " + name + ", so you could not create a flask of this.");
					continue;
				}
				/** The amount of flasks to create */
				int amountToCreate = amount / 6;
				/** The name of the flask */
				String flaskName = name.replaceAll("potion", "").trim() + " flask (6)";
				/** The id of the flask */
				int id = ItemNames.getId(flaskName);
				ItemDefinitions definitions = ItemDefinitions.getItemDefinitions(id);
				if (definitions.isNoted() && definitions.getCertId() != -1) {
					id = definitions.getCertId();
				}
				for (Item item : player.getInventory().getItems().toArray()) {
					if (item == null) {
						continue;
					}
					if (item.getName().contains(name)) {
						player.getInventory().deleteItem(item);
					}
				}
				player.getInventory().addItem(id, amountToCreate);
			}
		}
	}

	/**
	 * Empties a potion if the item is a potion.
	 *
	 * @param player
	 *            The player
	 * @param item
	 *            The item
	 * @return
	 */
	public static boolean emptyPotion(Player player, Item item) {
		String name = item.getName();
		Pattern pattern = Pattern.compile("\\([0-9]\\)");
		Matcher m = pattern.matcher(name);
		if (item.getName().toLowerCase().contains("amulet") || item.getName().toLowerCase().contains("games") || name.toLowerCase().contains("ring")) {
			return false;
		}
		if (m.find() || item.getId() == 227) {
			player.getInventory().deleteItem(item);
			player.getInventory().addItem(229, 1);
			player.sendMessage("You empty the " + item.getName().toLowerCase() + ".");
		}
		return false;
	}

}
