package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur<itstyluur@gmail.com>
 * @since Aug 20, 2014
 */
public class CrystalChestAchievement extends Achievement {
	private static final long serialVersionUID = 2943500495124404945L;

	public CrystalChestAchievement() {
		super(Types.MEDIUM, "Open the crystal chest @TOTAL@ times");
	}

	@Override
	public String getRewardInfo() {
		return "3x Achievement Points & 750K";
	}

	@Override
	public void giveReward(Player player) {
		this.addAchievementPoints(player, 3);
		this.addItem(player, new Item(995, 750000));
	}

	@Override
	public int getTotalAmount() {
		return 75;
	}

	@Override
	public String getKey() {
		return "crystal_chest_open";
	}

}
