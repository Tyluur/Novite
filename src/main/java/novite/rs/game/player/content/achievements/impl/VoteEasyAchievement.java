package novite.rs.game.player.content.achievements.impl;

import novite.rs.game.player.Player;
import novite.rs.game.player.content.achievements.Achievement;
import novite.rs.game.player.content.achievements.Types;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 23, 2014
 */
public class VoteEasyAchievement extends Achievement {

	public VoteEasyAchievement() {
		super(Types.EASY, "Vote @TOTAL@ times");
	}

	@Override
	public String getRewardInfo() {
		return "3x Achievement Points";
	}

	@Override
	public void giveReward(Player player) {
		addAchievementPoints(player, 3);
	}

	@Override
	public int getTotalAmount() {
		return 10;
	}

	@Override
	public String getKey() {
		return "voted_times";
	}

	private static final long serialVersionUID = -5181480382874683886L;

}
