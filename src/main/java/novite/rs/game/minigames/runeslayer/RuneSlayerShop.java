package novite.rs.game.minigames.runeslayer;

import novite.rs.cache.loaders.ItemDefinitions;
import novite.rs.game.player.Player;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Jan 17, 2014
 */
public class RuneSlayerShop {

	/**
	 * Displays the RuneSlayer shop interface
	 * 
	 * @param player
	 */
	public static void display(Player player) {
		int interfaceId = 940;
		player.getPackets().sendIComponentText(interfaceId, 33, "RuneSlayer Rewards");
		player.getPackets().sendIComponentSettings(interfaceId, 2, 0, 205, 1278);
		player.getPackets().sendHideIComponent(interfaceId, 49, false);
		player.getInterfaceManager().sendInterface(interfaceId);
		sendTokens(player);
	}

	/**
	 * Sends the amount of tokens the player has
	 * 
	 * @param player
	 *            The player to send the tokens to
	 */
	public static void sendTokens(Player player) {
		player.getPackets().sendIComponentText(940, 31, String.valueOf(player.getFacade().getRuneSlayerPoints()));
	}

	public enum RuneSlayerReward {
		BONECRUSHER(18337, 0, 21, 34000), HERBICIDE(19675, 5, 21, 34000), SCROLL_OF_LIFE(18336, 15, 25, 10000), SCROLL_OF_CLEANSING(19890, 40, 49, 20000), SCROLL_OF_EFFICIENCY(19670, 105, 55, 20000), SCROLL_OF_AUGURY(18344, 150, 77, 153000), SCROLL_OF_RIGOUR(18839, 145, 74, 140000), SCROLL_OF_RENEWAL(18343, 125, 65, 107000), MERCENARY_GLOVES(18347, 140, 73, 48500), TOME_OF_FROST(18346, 70, 48, 43000), ARCANE_PULSE_NECKLACE(18333, 20, 30, 6500), GRAVITE_SHORTBOW(18373, 70, 45, 40000), GRAVITE_LONGSWORD(18367, 55, 45, 40000), GRAVITE_RAPIER(18365, 50, 45, 40000), GRAVITE_STAFF(18371, 65, 45, 40000), GRAVITE_2H(18369, 60, 45, 40000), ARCANE_BLAST_NECKLACE(18334, 90, 50, 15500), RING_OF_VIGOUR(19669, 120, 62, 50000), ARCANE_STREAM_NECKLACE(18335, 130, 60, 30500), CHAOTIC_RAPIER(18349, 155, 70, 200000), CHAOTIC_LONGSWORD(18351, 160, 70, 200000), CHAOTIC_MAUL(18353, 165, 70, 200000), CHAOTIC_STAFF(18355, 170, 70, 200000), CHAOTIC_CROSSBOW(18357, 175, 70, 200000), CHAOTIC_KITESHIELD(18359, 180, 70, 200000), EAGLE_EYE_KITESHIELD(18361, 185, 70, 200000), FARSEER_KITESHIELD(18363, 190, 70, 200000), SNEAKERPEEPER(19894, 195, 70, 85000), TWISTEDNECKLACE(19886, 25, 30, 8500), DRAGONTOOTHNECKLACE(19887, 115, 60, 17000), DEMONHORNNECKLACE(19888, 200, 90, 35000);

		private final int id;
		private final int req;
		private final int cost;
		private final int slotId;
		private final String name;

		/**
		 * A {@code RuneSlayerReward} constructor
		 *
		 * @param id
		 *            The id of the item
		 * @param slotId
		 *            The slot id in the interface
		 * @param req
		 *            The skill level requirement
		 * @param cost
		 *            The runeslayer point cost of the item
		 */
		private RuneSlayerReward(int id, int slotId, int req, int cost) {
			this.id = id;
			this.req = req;
			this.cost = cost;
			this.slotId = slotId;
			this.name = ItemDefinitions.getItemDefinitions(id).getName();
		}

		public static RuneSlayerReward getReward(int id) {
			for (RuneSlayerReward reward : RuneSlayerReward.values()) {
				if (reward.getSlotId() == id) {
					return reward;
				}
			}
			return null;
		}

		/**
		 * Getting the id of the item
		 * 
		 * @return
		 */
		public int getId() {
			return id;
		}

		/**
		 * Getting the name of the item
		 * 
		 * @return
		 */
		public String getName() {
			return name;
		}

		/**
		 * Getting the runeslayer points cost of the item
		 * 
		 * @return
		 */
		public int getCost() {
			return cost;
		}

		/**
		 * Getting the slot id of the item
		 * 
		 * @return
		 */
		public int getSlotId() {
			return slotId;
		}

		/**
		 * Getting the skill level requirement of the item
		 * 
		 * @return
		 */
		public int getRequirement() {
			return req;
		}
	}

}
