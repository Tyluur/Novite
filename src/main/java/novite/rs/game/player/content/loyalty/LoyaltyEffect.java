package novite.rs.game.player.content.loyalty;

public enum LoyaltyEffect {

	//title 2613-2629

	//lunar helm 2585
	//gnome scarf 2584
	//sof 2583
	//pet rock 2582
	//robin 2581
	MYSTICAL_GAZE(
	"Mystical Gaze",
	23888,
	18000,
	4,
	1,
	1,
	9769),
	BLAZING_GAZE(
	"Blazing Gaze",
	23890,
	18000,
	5,
	2,
	2,
	9770),
	SERENE_GAZE(
	"Serene Gaze",
	23882,
	18000,
	1,
	4,
	4,
	9766),
	ABYSSAL_GAZE(
	"Abyssal Gaze",
	23892,
	18000,
	6,
	8,
	8,
	9771),
	NOCTURNAL_GAZE(
	"Nocturnal Gaze",
	23886,
	18000,
	3,
	16,
	16,
	9768),
	DIVINE_GAZE(
	"Divine Gaze",
	23894,
	18000,
	7,
	32,
	32,
	9772),
	VERNAL_GAZE(
	"Vernal Gaze",
	23884,
	18000,
	2,
	64,
	64,
	9767),
	INFERNAL_GAZE(
	"Infernal Gaze",
	23880,
	18000,
	0,
	128,
	128,
	9765);

	String name;
	int itemId;
	int pointCost;
	int buttonId;
	int buyBit;
	int favBit;
	int scriptValue;
	int configId = 2229;
	int favConfigId = 2231;

	private LoyaltyEffect(String name, int itemId, int pointCost, int buttonId, int buyBit, int favBit, int scriptValue) {
		this.name = name;
		this.itemId = itemId;
		this.pointCost = pointCost;
		this.buttonId = buttonId;
		this.buyBit = buyBit;
		this.favBit = favBit;
		this.scriptValue = scriptValue;
	}

	private LoyaltyEffect(String name, int itemId, int pointCost, int buttonId, int buyBit, int favBit, int scriptValue, boolean nothing) {
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

}