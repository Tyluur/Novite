package novite.rs.game.player.content.loyalty;

import novite.rs.utility.Utils;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Dec 6, 2013
 */
public enum LoyaltyTitle {

	SIR(1000, 5, 0), LORD(1000, 6, 1), DUDERINO(4000, 7, 2), LIONHEART(4000, 8, 3), HELLRAISER(8000, 9, 4), CRUSADER(8000, 10, 5), DESPERADO(10000, 11, 6), BARON(10000, 12, 7), COUNT(15000, 13, 8), OVERLORD(15000, 14, 9), PRINCE(15000, 27, 17), BANDITO(20000, 15, 10), DUKE(20000, 16, 11), JUSTICIAR(20000, 30, 20), KING(25000, 17, 12), BIG_CHEESE(25000, 18, 13), BIGWIG(25000, 19, 14), ARCHON(25000, 29, 19), EMPEROR(30000, 26, 16), WUNDERKIND(50000, 20, 15), WITCH_KING(50000, 28, 18), THE_AWESOME(50000, 31, 21), THE_MAGNIFICENT(50000, 32, 22), THE_UNDEFEATED(50000, 33, 23), THE_STRANGE(50000, 34, 24), THE_DIVINE(50000, 35, 25), THE_FALLEN(50000, 36, 26), THE_WARRIOR(50000, 37, 27);

	LoyaltyTitle(int cost, int value, int slotId) {
		this.cost = cost;
		this.value = value;
		this.slotId = slotId;
	}

	private final int cost;
	private final int value;
	private final int slotId;

	public static LoyaltyTitle getLoyaltyTitleBySlot(int slotId) {
		for (LoyaltyTitle title : LoyaltyTitle.values()) {
			if (title.getSlotId() == slotId) {
				return title;
			}
		}
		return null;
	}

	public int getCost() {
		return cost;
	}

	public String getName() {
		return Utils.formatPlayerNameForDisplay(name());
	}

	public int getValue() {
		return value;
	}

	public int getSlotId() {
		return slotId;
	}

}
