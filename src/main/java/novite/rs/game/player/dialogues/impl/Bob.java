package novite.rs.game.player.dialogues.impl;

import java.io.IOException;

import novite.rs.cache.Cache;
import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.item.Item;
import novite.rs.game.player.dialogues.ChatAnimation;
import novite.rs.game.player.dialogues.Dialogue;
import novite.rs.utility.Utils;
import novite.rs.utility.game.item.ItemNames;

/**
 * 
 * @author Tyluur <itstyluur@gmail.com>
 * @since Jul 12, 2013
 */
public class Bob extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		this.sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Hello boss, I can repair all of your broken", "armour - such as barrows - for a cost.", "Would you like me to do this?");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Select an Option", "Yes - repair broken armour", "Never mind.");
			stage = 0;
			break;
		case 0:
			if (option == FIRST) {
				int cost = getRepairCost();
				if (cost == 0) {
					sendNPCDialogue(npcId, ChatAnimation.NORMAL, "You have nothing for me to repair!");
					stage = -2;
				} else {
					sendNPCDialogue(npcId, ChatAnimation.LISTENING, "This will cost you " + Utils.format(getRepairCost()) + " coins, is this okay?");
					stage = 1;
				}
			} else {
				end();
			}
			break;
		case 1:
			sendOptionsDialogue("Pay fee to repair?", "Yes", "No");
			stage = 2;
			break;
		case 2:
			if (option == FIRST) {
				if (player.takeMoney(getRepairCost())) {
					repairAll();
					sendNPCDialogue(npcId, ChatAnimation.NORMAL, "Thanks for the business!");
				} else {
					sendNPCDialogue(npcId, ChatAnimation.SAD, "Seems like you've ran out of money for my service...");
				}
				stage = -2;
			} else {
				end();
			}
			break;
		}
	}

	public static void main(String... args) throws IOException {
		Cache.init();
		int[] ids = { 4714, 4999, 4716, 4974, 4734, 20135 };
		for (int id : ids) {
			System.out.println(ItemDefinitions.getItemDefinitions(id).name + " - " + getDegradeType(id));
		}
	}

	/**
	 * If the item is a barrows item
	 * 
	 * @param id
	 *            The id of the item
	 * @return
	 */
	public static DegradeType getDegradeType(int id) {
		String name = ItemDefinitions.getItemDefinitions(id).getName().toLowerCase();
		for (int i = 0; i < data.length; i++) {
			String repairName = ((String) data[i][0]).toLowerCase();
			if (name.contains(repairName)) {
				return (i > 4 ? DegradeType.NEX_ARMOUR : DegradeType.BARROWS);
			}
		}
		return null;
	}

	public static boolean isBarrows(int id) {
		DegradeType type = Bob.getDegradeType(id);
		if (type != null && type == DegradeType.BARROWS)
			return true;
		return false;
	}

	private int getRepairCost() {
		int cost = 0;
		for (Item item : player.getInventory().getItems().toArray()) {
			if (item == null)
				continue;
			if (getDegradeType(item.getId()) == null || item.getDefinitions().isNoted()) {
				continue;
			}
			String name = item.getName().toLowerCase();
			for (int i = 0; i < data.length; i++) {
				String repairName = ((String) data[i][0]).toLowerCase();
				if (name.contains(repairName)) {
					int c = getCharges(item.getId());
					int percent = (c == 100 ? 25 : c == 75 ? 40 : c == 50 ? 50 : c == 25 ? 80 : 100);
					double fee = (int) data[i][1] * (double) ((double) percent / (double) 100);
					cost += fee;
				}
			}
		}
		return cost;
	}

	public static int getCharges(int itemId) {
		String name = ItemDefinitions.getItemDefinitions(itemId).getName();
		int charges = -1;
		switch (getDegradeType(itemId)) {
		case BARROWS:
			try {
				charges = Integer.parseInt(name.split(" ")[name.split(" ").length - 1]);
			} catch (Exception e) {
				charges = -1;
			}
			break;
		case NEX_ARMOUR:
			charges = 0;
			break;
		}
		return charges;
	}

	private void repairAll() {
		for (int i = 0; i < player.getInventory().getItems().toArray().length; i++) {
			Item item = player.getInventory().getItems().toArray()[i];
			if (item == null)
				continue;
			if (getDegradeType(item.getId()) == null || item.getDefinitions().isNoted()) {
				continue;
			}
			int fixedId = getFixedId(item);
			if (fixedId != -1) {
				player.getInventory().replaceItem(fixedId, 1, i);
			}
		}
		player.getInventory().refresh();
	}

	private int getFixedId(Item item) {
		String name = item.getName();
		switch (getDegradeType(item.getId())) {
		case BARROWS:
			String newName = name.substring(0, name.indexOf("" + getCharges(item.getId())) - 1);
			System.out.println(newName);
			return ItemNames.getTradeableId(newName);
		case NEX_ARMOUR:
			return ItemNames.getTradeableId(name);
		}
		return -1;
	}

	public static int getDegraded(boolean completely, int itemId) {
		int charges = getCharges(itemId);
		int nextCharges = -1;
		if (charges == -1) {
			nextCharges = 100;
		} else {
			nextCharges = charges - 25;
		}
		if (completely)
			nextCharges = 0;
		if (nextCharges != -1) {
			String name = ItemDefinitions.getItemDefinitions(itemId).getName();
			String newName = "";
			for (int i = 0; i < name.toCharArray().length; i++) {
				Character c = name.toCharArray()[i];
				if (Character.isDigit(c)) {
					break;
				}
				newName += c;
			}
			newName = newName.trim();
			newName = newName + " " + nextCharges;
			return ItemNames.getTradeableId(newName);
		}
		return -1;
	}

	@Override
	public void finish() {

	}

	int npcId;

	public enum DegradeType {
		BARROWS, NEX_ARMOUR
	}

	private static Object[][] data = new Object[][] { { "Verac", 500000 }, { "Ahrim", 600000 }, { "Dharok", 1000000 }, { "Torag", 500000 }, { "Karil", 600000 }, { "Torva", 1500000 }, { "Virtus", 1200000 }, { "Pernix", 1250000 } };
}
