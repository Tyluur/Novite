package novite.rs.utility.script.player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import novite.rs.cache.Cache;
import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.utility.Saving;
import novite.rs.utility.script.GameScript;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Feb 13, 2014
 */
public class ItemNameFinder extends GameScript {

	public static void main(String... args) throws IOException {
		Cache.init();
		for (File acc : getAccounts()) {
			try {
				Player player = (Player) Saving.loadSerializedFile(acc);
				if (player != null) {
					for (Item item : player.getInventory().getItems().toArray()) {
						if (item == null) {
							continue;
						}
						String name = item.getName().toLowerCase();
						for (String contained : ITEMS_TO_FIND) {
							if (name.contains(contained.toLowerCase())) {
								DETAILS.add(acc.getName() + ":\t[INVENTORY]" + item.getAmount() + "x " + item.getName() + ", id=" + item.getId() + " ");
							}
						}
					}
					for (Item item : player.getEquipment().getItems().toArray()) {
						if (item == null) {
							continue;
						}
						String name = item.getName().toLowerCase();
						for (String contained : ITEMS_TO_FIND) {
							if (name.contains(contained.toLowerCase())) {
								DETAILS.add(acc.getName() + ":\t[EQUIPMENT]" + item.getAmount() + "x " + item.getName() + ", id=" + item.getId() + " ");
							}
						}
					}
					for (Item item : player.getBank().getContainerCopy()) {
						if (item == null) {
							continue;
						}
						String name = item.getName().toLowerCase();
						for (String contained : ITEMS_TO_FIND) {
							if (name.contains(contained.toLowerCase())) {
								DETAILS.add(acc.getName() + ":\t[BANK]" + item.getAmount() + "x " + item.getName() + ", id=" + item.getId() + " ");
							}
						}
					}
					if (player.getFamiliar() != null) {
						if (player.getFamiliar().getBob() != null) {
							for (Item item : player.getFamiliar().getBob().getBeastItems().toArray()) {
								if (item == null) {
									continue;
								}
								String name = item.getName().toLowerCase();
								for (String contained : ITEMS_TO_FIND) {
									if (name.contains(contained.toLowerCase())) {
										DETAILS.add(acc.getName() + ":\t[BOB]" + item.getAmount() + "x " + item.getName() + ", id=" + item.getId() + " ");
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				System.err.println(acc.getAbsolutePath());
				e.printStackTrace();
			}
		}
		Iterator<String> it = DETAILS.iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}
	
	/**
	 * If the items the player has contains these strings, it will be added to
	 * the {@link #DETAILS} list
	 */
	private static final String[] ITEMS_TO_FIND = { "chaotic", "korasi" };

	private static final List<String> DETAILS = new ArrayList<>();

}
