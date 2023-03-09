package novite.rs.utility.tools;

import java.io.IOException;

import novite.rs.cache.Cache;

public class ItemRemoverC {

	public static void main(String[] args) {
		try {
			Cache.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Starting");
		/*
		 * File[] chars = new File("data/characters").listFiles(); for (File acc
		 * : chars) { try { Player player = (Player) SerializableFilesManager
		 * .loadSerializedFile(acc); for (Item item :
		 * player.getBank().getItems().getItems()) { if (item != null) { for
		 * (String string : strings) if (item.getDefinitions().getName()
		 * .toLowerCase().contains(string)) {
		 * player.getBank().getItems().remove(item); } } } for (Item item :
		 * player.getInventory().getItems() .getItems()) { if (item != null) {
		 * for (String string : strings) if (item.getDefinitions().getName()
		 * .toLowerCase().contains(string)) { player.getInventory().getItems()
		 * .remove(item); } } } for (Item item :
		 * player.getEquipment().getItems() .getItems()) { if (item != null) {
		 * for (String string : strings) if (item.getDefinitions().getName()
		 * .toLowerCase().contains(string)) { player.getEquipment().getItems()
		 * .removeAll(item); } } } player.getBank().getItems().shift();
		 * player.getInventory().getItems().shift(); SerializableFilesManager
		 * .storeSerializableClass(player, acc); } catch (Throwable e) {
		 * e.printStackTrace(); System.out.println("failed: " + acc.getName());
		 * } }
		 */
		System.out.println("Done.");
	}
}
