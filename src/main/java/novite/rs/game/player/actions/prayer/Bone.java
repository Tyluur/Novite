package novite.rs.game.player.actions.prayer;

/**
 * @author Lazarus <lazarus.rs.king@gmail.com>
 * @since Jul 6, 2014
 */
public enum Bone {
	NORMAL(526, 100),

	BURNT(528, 100),

	MONKEY(3183, 125),

	BAT(530, 125),

	BIG(532, 200),

	JOGRE(3125, 200),

	ZOGRE(4812, 250),

	SHAIKAHAN(3123, 300),

	BABY(534, 350),

	WYVERN(6812, 400),

	DRAGON(536, 500),

	FAYRG(4830, 525),

	RAURG(4832, 550),

	DAGANNOTH(6729, 650),

	OURG(4834, 750),

	FROST_DRAGON(18830, 850);

	public static Bone forId(int id) {
		for (Bone bone : Bone.values()) {
			if (bone.getId() == id)
				return bone;
		}
		return null;
	}

	private Bone(int id, double experience) {
		this.id = id;
		this.experience = experience;
	}

	public int getId() {
		return id;
	}

	public double getExperience() {
		return experience;
	}

	private final int id;
	private final double experience;
}
