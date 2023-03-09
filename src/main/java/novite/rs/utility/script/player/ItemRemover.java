package novite.rs.utility.script.player;

import java.io.File;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.utility.Saving;
import novite.rs.utility.script.GameScript;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Jul 10, 2013
 */
public class ItemRemover extends GameScript {

	public static void main(String... args) {
		for (File acc : getAccounts()) {
			try {
				Player player = (Player) Saving.loadSerializedFile(acc);
				if (player != null) {
					for (int item : items) {
						if (player.getInventory().containsItem(item, 1)) {
							player.getInventory().forceRemove(item, player.getInventory().getNumberOf(item));
						}
						if (player.getEquipment().getItems().contains(new Item(item))) {
							player.getEquipment().forceRemove(item, player.getEquipment().getItems().getNumberOf(item));
						}
						if (player.getBank().getItem(item) != null) {
							player.getBank().forceRemove(item);
						}
						if (player.getFamiliar() != null) {
							if (player.getFamiliar().getBob() != null) {
								for (Item i : player.getFamiliar().getBob().getBeastItems().getItems()) {
									if (i == null) {
										continue;
									}
									if (i.getId() == item) {
										player.getFamiliar().getBob().getBeastItems().remove(i);
									}
								}
							}
						}
					}
					savePlayer(player, acc);
				}
			} catch (Exception e) {
				System.out.println("Error with " + acc.getAbsolutePath());
			}
		}
	}

	private static final int[] items = new int[] { 2591, 2593, 2595, 2597, 2583, 2585, 2587, 2589, 10462, 10466, 10458, 10464, 10460, 10468, 10404, 10406, 10408, 10410, 10412, 10414, 10416, 10418, 10420, 10422, 10424, 10426, 10428, 10430, 10714, 10715, 10716, 10717, 10718, 10744, 10740, 10742, 10746, 7386, 7390, 7392, 7394, 7396, 7388, 7362, 10681, 7366, 7368, 10692, 13095, 13105, 13081, 13083, 10446, 10448, 10450, 10452, 10454, 10456, 10458, 10460, 10462, 10464, 10466, 10468, 10768, 10770, 10772, 10758, 10760, 10762, 10764, 10766, 19173, 19175, 2599, 2601, 2603, 2605, 2607, 2609, 2611, 2613, 2577, 2579, 13097, 13483, 13491, 13103, 13111, 13113, 13115, 13107, 10364, 10470, 10472, 10474, 10440, 10442, 10444, 19368, 19370, 19372, 19374, 19376, 19378, 2615, 2617, 2619, 2621, 2623, 2625, 2627, 2629, 2653, 2655, 2657, 2659, 2661, 2663, 2665, 2667, 2669, 2671, 2673, 2675, 2677, 3481, 3483, 3485, 3487, 3489, 10330, 10332, 10334, 10336, 10338, 10340, 10342, 10344, 10346, 10348, 10350, 10352, 10368, 10370, 10372, 10374, 10376, 10378, 10380, 10382, 10384, 10386, 10388, 10390, 7398, 7399, 7400, 19272, 19275, 19278, 19281, 19284, 19287, 19290, 19293, 19296, 19299, 19302, 19305, 2639, 2641, 2643, 8950, 2581, 13099, 19362, 19364, 19366, 19374, 19376, 19378, 19380, 19382, 19384, 19386, 19388, 19390, 19392, 19394, 19396, 19398, 19401, 19404, 19407, 19410, 19413, 19416, 19419, 19422, 19425, 19428, 19431, 19434, 19437, 19440, 19443, 19445, 19447, 19449, 19451, 19453, 19455, 19457, 19459, 19461, 19463, 19465, 19467, 19308, 19311, 19314, 19317, 19320, 19323, 19325, 19327, 19329, 19331, 19143, 19146, 19149 };

}
