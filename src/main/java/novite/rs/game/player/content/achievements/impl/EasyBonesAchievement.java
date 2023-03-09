package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class EasyBonesAchievement extends Achievement {

	public EasyBonesAchievement() {
		super(Types.EASY, "Bury @TOTAL@ regular bones");
	}

	@Override
	public String getRewardInfo() {
		return "1x Achievement Points";
	}

	@Override
	public void giveReward(Player player) {
		addAchievementPoints(player, 1);
	}

	@Override
	public int getTotalAmount() {
		return 100;
	}

	@Override
	public String getKey() {
		return "regular_bones";
	}

	private static final long serialVersionUID = 1394902596024271336L;

}
