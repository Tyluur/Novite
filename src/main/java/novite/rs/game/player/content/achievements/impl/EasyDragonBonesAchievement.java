package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.item.Item;
import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class EasyDragonBonesAchievement extends Achievement {

	public EasyDragonBonesAchievement() {
		super(Types.MEDIUM, "Bury @TOTAL@ Dragon Bones");
	}

	@Override
	public String getRewardInfo() {
		return "3x Achievement Points & 750K Cash";
	}

	@Override
	public void giveReward(Player player) {
		addAchievementPoints(player, 3);
		addItem(player, new Item(995, 750000));
	}

	@Override
	public int getTotalAmount() {
		return 75;
	}

	@Override
	public String getKey() {
		return "dragon_bones_buried";
	}

	private static final long serialVersionUID = 5044688837764377277L;

}
