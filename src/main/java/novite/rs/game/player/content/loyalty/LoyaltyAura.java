package novite.rs.game.player.content.loyalty;

import novite.rs.utility.Utils;

/*
 * Map:
 *  [0] 2586
 [1] 2587
 [2] 2588
 [3] 2589
 [4] 2590
 [5] 2591
 [6] 2592
 [7] 2593
 [8] 2594
 [9] 2595
 [10] 2596
 [11] 2597
 [12] 2598
 [13] 2599
 [14] 2600
 [15] 2601
 [17] 2603
 [16] 2602
 [19] 2605
 [18] 2604
 [21] 2607
 [20] 2606
 [23] 2609
 [22] 2608
 [25] 2611
 [24] 2610
 [27] 2613
 [26] 2612
 [29] 2861
 [28] 2860
 [31] 2863
 [30] 2862
 [34] 2866
 [35] 2867
 [32] 2864
 [33] 2865
 [38] 2870
 [39] 2871
 [36] 2868
 [37] 2869
 [42] 2874
 [43] 2875
 [40] 2872
 [41] 2873
 [46] 2878
 [47] 2879
 [44] 2876
 [45] 2877
 [51] 2883
 [50] 2882
 [49] 2881
 [48] 2880
 [55] 9723
 [54] 9722
 [53] 2885
 [52] 2884
 [59] 9741
 [58] 9740
 [57] 9739
 [56] 9738
 [63] 9737
 [62] 9736
 [61] 9735
 [60] 9734
 [68] 9728
 [69] 9729
 [70] 9730
 [71] 9731
 [64] 9724
 [65] 9725
 [66] 9726
 [67] 9727
 [72] 9733
 [73] 9732

 */

public enum LoyaltyAura {

	// quarrym = 262144

	ODDBALL("Oddball", 20957, 2000, 0, 1, 1, 2586), QUARRYMASTER(22284, 5000, 18), WISDOM(22302, 40000, 27), POISON_PURGE("Poison Purge", 20958, 2750, 1, 2, 2, 2587), KNOCKOUT("Knock-Out", 20961, 3500, 3, 8, 8, 2589), FRIEND_IN_NEED("Friend in Need", 20963, 3500, 2, 4, 4, 2588), TRACKER("Tracker", 22927, 3500, 36, 32, 32, 2868, true), RUNIC_ACCURACY("Runic Accuracy", 20962, 4250, 5, 32, 32, 2591), SHARPSHOOTER("Sharpshooter", 20967, 4250, 4, 16, 16, 2590), SUREFOOTED("Surefooted", 20964, 5000, 6, 64, 64, 2592), REVERENCE("Reverence", 20965, 5000, 7, 128, 128, 2593), CALL_OF_THE_SEA("Call of the Sea", 20966, 5000, 8, 256, 256, 2594), LUMBERJACK("Lumberjack", 22280, 5000, 16, 65536, 65536, 2602), INVIGORATE("Invigorate", 23840, 5000, 60, 536870912, 536870912, 9734, true), SALVATION("Salvation", 22899, 5000, 39, 256, 256, 2871, true), FIVE_FINGER_DISCOUNT("Five Finger Discount", 22288, 5000, 20, 1048576, 1048576, 2606), CORRUPTION("Corruption", 22905, 5000, 42, 2048, 2048, 2874, true), GREATER_SUREFOOTED("Greater Surefooted", 222278, 17000, 15), GREATER_CORRUPTION("Greater Corruption", 22907, 12000, 43), GREATER_SALVATION("Greater Salvation", 22901, 12000, 40), GREATER_HARMONY("Greater Harmony", 23850, 12000, 57), GREATER_POISON_PURGE("Greater Poison Purge", 22268, 15000, 10), GREATER_LUMBERJACK("Greater Lumberjack", 22282, 19000, 17), GREATER_FIVE_FINGER_DISCOUNT("Greater Five Finger Discount", 22290, 19000, 21), GREATER_CALL_OF_THE_SEA(22274, 19000, 13), GREATER_QUARRYMASTER(22286, 19000, 19), JACK_OF_TRADES(20959, 15000, 9), GREATER_TRACKER(22929, 16000, 37), GREATER_REVERENCE(22276, 21000, 14), GREATER_INVIGORATE(23842, 16000, 61), GREATER_GREENFINGERS(22885, 16000, 34), GREATER_SHARPSHOOTER(22272, 21000, 12), GREATER_RUNIC_ACCURACY(22270, 21000, 11), VAMPYRISM(22298, 23000, 25), EQUILIBRIUM(22294, 23000, 23), INSPIRATION(22296, 23000, 24), RESOURCEFUL(22292, 23000, 22), PENANCE(22300, 23000, 26);

	// HARMONY("Harmony", 23848, 5000, 56, 9738, true);

	public static final int a = 1 << 28;

	String name;
	int itemId;
	int pointCost;
	int buttonId;
	int buyBit;
	int favBit;
	int scriptValue;
	int configId = 2229;
	int favConfigId = 2231;

	private LoyaltyAura(int itemId, int pointCost, int buttonId) {
		this.name = Utils.formatPlayerNameForDisplay(name());
		this.itemId = itemId;
		this.pointCost = pointCost;
		this.buttonId = buttonId;
		this.buyBit = -1;
		this.favBit = -1;
		this.scriptValue = -1;
	}

	private LoyaltyAura(String name, int itemId, int pointCost, int buttonId) {
		this.name = name;
		this.itemId = itemId;
		this.pointCost = pointCost;
		this.buttonId = buttonId;
		this.buyBit = -1;
		this.favBit = -1;
		this.scriptValue = -1;
	}

	private LoyaltyAura(String name, int itemId, int pointCost, int buttonId, int buyBit, int favBit, int scriptValue) {
		this.name = name;
		this.itemId = itemId;
		this.pointCost = pointCost;
		this.buttonId = buttonId;
		this.buyBit = buyBit;
		this.favBit = favBit;
		this.scriptValue = scriptValue;
	}

	private LoyaltyAura(String name, int itemId, int pointCost, int buttonId, int buyBit, int favBit, int scriptValue, boolean nothing) {
		this.name = name;
		this.itemId = itemId;
		this.pointCost = pointCost;
		this.buttonId = buttonId;
		this.buyBit = buyBit;
		this.favBit = favBit;
		this.scriptValue = scriptValue;
		configId = 2443;
		favConfigId = 2444;
	}

	public int getItemId() {
		return itemId;
	}

	public int getPrice() {
		return pointCost;
	}

	public int getButtonId() {
		return buttonId;
	}

	public int getBuyBit() {
		return buyBit;
	}

	public int getFavBit() {
		return favBit;
	}

	public int getScriptValue() {
		return scriptValue;
	}

	public String getName() {
		return name;
	}

}