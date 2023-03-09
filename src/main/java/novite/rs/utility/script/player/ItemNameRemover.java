package novite.rs.utility.script.player;

import java.io.File;
import java.io.IOException;

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
public class ItemNameRemover extends GameScript {

	public static void main(String... args) throws IOException {
		Cache.init();
		for (File acc : getAccounts()) {
			try {
				Player player = (Player) Saving.loadSerializedFile(acc);
				if (player != null) {
					boolean modified = false;
					for (Item item : player.getInventory().getItems().toArray()) {
						if (item == null) {
							continue;
						}
						String name = item.getName().toLowerCase();
						for (String contained : CONTAINED_TO_REMOVE) {
							if (name.contains(contained.toLowerCase())) {
								player.getInventory().forceRemove(item.getId(), player.getInventory().getNumberOf(item.getId()));
								modified = true;
							}
						}
					}
					for (Item item : player.getEquipment().getItems().toArray()) {
						if (item == null) {
							continue;
						}
						String name = item.getName().toLowerCase();
						for (String contained : CONTAINED_TO_REMOVE) {
							if (name.contains(contained.toLowerCase())) {
								player.getEquipment().forceRemove(item.getId(), player.getEquipment().getItems().getNumberOf(item.getId()));
								modified = true;
							}
						}
					}
					for (Item item : player.getBank().getContainerCopy()) {
						if (item == null) {
							continue;
						}
						String name = item.getName().toLowerCase();
						for (String contained : CONTAINED_TO_REMOVE) {
							if (name.contains(contained.toLowerCase())) {
								player.getBank().forceRemove(item.getId());
								modified = true;
							}
						}
					}
					if (modified) {
						savePlayer(player, acc);
						System.out.println(acc.getName() + " had items and was deleted");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * If the items the player has contains these strings, it will be removed.
	 */
	private static final String[] CONTAINED_TO_REMOVE = { "vesta", "statius", "morrigan", "zuriel" };

}
